package fr.ralala.hexviewer.ui.tasks;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.ralala.hexviewer.ApplicationCtx;
import fr.ralala.hexviewer.R;
import fr.ralala.hexviewer.models.FileData;
import fr.ralala.hexviewer.models.LineEntry;
import fr.ralala.hexviewer.ui.adapters.HexTextArrayAdapter;
import fr.ralala.hexviewer.ui.utils.UIHelper;
import fr.ralala.hexviewer.utils.MemoryMonitor;
import fr.ralala.hexviewer.utils.SysHelper;

/**
 * ******************************************************************************
 * <p><b>Project HexViewer</b><br/>
 * Task used to open a file.
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class TaskOpen extends ProgressTask<ContentResolver, FileData, TaskOpen.Result> implements MemoryMonitor.MemoryListener {
  private static final String TAG = TaskOpen.class.getSimpleName();
  private final Context mContext;
  private static final int MAX_LENGTH = SysHelper.MAX_BY_ROW_16 * 20000;
  private final HexTextArrayAdapter mAdapter;
  private final OpenResultListener mListener;
  private InputStream mInputStream = null;
  private final boolean mAddRecent;
  private final ContentResolver mContentResolver;
  private final MemoryMonitor mMemoryMonitor;
  private final AtomicBoolean mLowMemory = new AtomicBoolean(false);

  public static class Result {
    private List<LineEntry> listHex = null;
    private String exception = null;
    private long startOffset = 0;
  }

  public interface OpenResultListener {
    void onOpenResult(boolean success, boolean fromOpen);
  }

  public TaskOpen(final Activity activity,
                  final HexTextArrayAdapter adapter,
                  final OpenResultListener listener, final boolean addRecent) {
    super(activity, true);
    mMemoryMonitor = new MemoryMonitor(ApplicationCtx.getInstance().getMemoryThreshold(), 2000);
    mContext = activity;
    mContentResolver = activity.getContentResolver();
    mAdapter = adapter;
    mListener = listener;
    mAddRecent = addRecent;
  }

  /**
   * Called before the execution of the task.
   *
   * @return The Config.
   */
  @Override
  public ContentResolver onPreExecute() {
    super.onPreExecute();
    mLowMemory.set(false);
    mMemoryMonitor.start(this, true);
    mAdapter.clear();
    return mContentResolver;
  }

  /**
   * Called after the execution of the task.
   *
   * @param result The result.
   */
  @Override
  public void onPostExecute(final Result result) {
    super.onPostExecute(result);
    mMemoryMonitor.stop();
    if (mLowMemory.get())
      UIHelper.toast(mContext, mContext.getString(R.string.not_enough_memory));
    else if (isCancelled())
      UIHelper.toast(mContext, mContext.getString(R.string.operation_canceled));
    else if (result.exception != null)
      UIHelper.toast(mContext, mContext.getString(R.string.exception) + ": " + result.exception);
    else {
      if (result.listHex != null) {
        mAdapter.setStartOffset(result.startOffset);
        mAdapter.addAll(result.listHex);
      }
    }
    if (mListener != null)
      mListener.onOpenResult(result.exception == null && !isCancelled() && !mLowMemory.get(), true);
    super.onPostExecute(result);
  }

  /**
   * Closes the stream.
   */
  private void close() {
    if (mInputStream != null) {
      try {
        mInputStream.close();
      } catch (final IOException e) {
        Log.e(TAG, "Exception: " + e.getMessage(), e);
      }
      mInputStream = null;
    }
  }

  /**
   * Called when the async task is cancelled.
   */
  @Override
  public void onCancelled() {
    close();
    if (mListener != null)
      mListener.onOpenResult(false, true);
  }

  /**
   * Performs a computation on a background thread.
   *
   * @param contentResolver ContentResolver.
   * @param fd              FileData.
   * @return The result.
   */
  @Override
  public Result doInBackground(ContentResolver contentResolver, FileData fd) {
    //final Activity activity = mActivityRef.get();
    final Result result = new Result();
    final List<LineEntry> list = new ArrayList<>();
    try {
      result.startOffset = fd.getStartOffset();
      final ApplicationCtx app = ApplicationCtx.getInstance();
      /* Size + stream */
      mTotalSize = fd.getSize();
      publishProgress(0L);
      mInputStream = contentResolver.openInputStream(fd.getUri());

      if (mInputStream != null) {
        int maxLength = moveCursorIfSequential(fd, result);

        if (result.exception == null) {
          MemoryMonitor.forceGC(); /* force GC before */
          /* prepare buffer */
          final byte[] data = new byte[maxLength];
          int reads;
          long totalSequential = fd.getStartOffset();
          evaluateShiftOffset(fd, totalSequential);
          boolean first = true;
          /* read data */
          while (!isCancelled() && (reads = mInputStream.read(data)) != -1) {
            try {
              SysHelper.formatBuffer(list, data, reads, mCancel,
                  ApplicationCtx.getInstance().getNbBytesPerLine(), first ? fd.getShiftOffset() : 0);
              first = false;
            } catch (IllegalArgumentException iae) {
              result.exception = iae.getMessage();
              break;
            }
            publishProgress((long) reads);
            if (fd.isSequential()) {
              totalSequential += reads;
              if (totalSequential >= fd.getEndOffset())
                break;
            }
          }
          /* prepare result */
          if (result.exception == null) {
            result.listHex = list;
            if (mAddRecent && !mCancel.get())
              app.getRecentlyOpened().add(fd);
          }
        }
      }
    } catch (final Exception e) {
      result.exception = e.getMessage();
    } finally {
      close();
    }
    return result;
  }

  private int moveCursorIfSequential(FileData fd, Result result) throws IOException {
    int maxLength = MAX_LENGTH;
    if (fd.isSequential()) {
      if (mInputStream.skip(fd.getStartOffset()) != fd.getStartOffset()) {
        result.exception = "Unable to skip file data!";
      }
      maxLength = fd.getSize() < MAX_LENGTH ? (int) fd.getSize() : MAX_LENGTH;
    }
    return maxLength;
  }

  private void evaluateShiftOffset(FileData fd, long totalSequential) {
    if (totalSequential != 0) {
      final int nbBytesPerLine = ApplicationCtx.getInstance().getNbBytesPerLine();
      final long count = totalSequential / nbBytesPerLine;
      final long remain = totalSequential - (count * nbBytesPerLine);
      fd.setShiftOffset((int) remain);
    }
  }

  public void onLowAppMemory() {
    mLowMemory.set(true);
    mCancel.set(true);
  }
}
