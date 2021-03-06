package fr.ralala.hexviewer;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import fr.ralala.hexviewer.utils.Payload;

/**
 * ******************************************************************************
 * <p><b>Project HexViewer</b><br/>
 * Main application context
 * </p>
 *
 * @author Keidan
 * <p>
 * ******************************************************************************
 */
public class ApplicationCtx extends Application {
  public static final String CFG_ABBREVIATE_PORTRAIT = "abbreviatePortrait";
  public static final String CFG_ABBREVIATE_LANDSCAPE = "abbreviateLandscape";
  public static final String CFG_HEX_ROW_HEIGHT = "hexRowHeight";
  public static final String CFG_HEX_ROW_HEIGHT_AUTO = "hexRowHeightAuto";
  public static final String CFG_HEX_FONT_SIZE = "hexFontSize";
  public static final String CFG_PLAIN_ROW_HEIGHT = "plainRowHeight";
  public static final String CFG_PLAIN_ROW_HEIGHT_AUTO = "plainRowHeightAuto";
  public static final String CFG_PLAIN_FONT_SIZE = "plainFontSize";
  private final Payload mPayload;
  private SharedPreferences mSharedPreferences;
  private String mDefaultAbbreviatePortrait;
  private String mDefaultAbbreviateLandscape;
  private String mDefaultHexRowHeight;
  private boolean mDefaultHexRowHeightAuto;
  private String mDefaultHexFontSize;
  private String mDefaultPlainRowHeight;
  private boolean mDefaultPlainRowHeightAuto;
  private String mDefaultPlainFontSize;

  /**
   * Constructs the application context.
   */
  public ApplicationCtx() {
    super();
    mPayload = new Payload();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mDefaultAbbreviatePortrait = getString(R.string.default_abbreviate_portrait);
    mDefaultAbbreviateLandscape = getString(R.string.default_abbreviate_landscape);
    mDefaultHexRowHeightAuto = Boolean.parseBoolean(getString(R.string.default_hex_row_height_auto));
    mDefaultHexRowHeight = getString(R.string.default_hex_row_height);
    mDefaultHexFontSize = getString(R.string.default_hex_font_size);
    mDefaultPlainRowHeightAuto = Boolean.parseBoolean(getString(R.string.default_plain_row_height_auto));
    mDefaultPlainRowHeight = getString(R.string.default_plain_row_height);
    mDefaultPlainFontSize = getString(R.string.default_plain_font_size);
  }

  /**
   * Returns the payload.
   *
   * @return Payload
   */
  public Payload getPayload() {
    return mPayload;
  }

  /* ---------- Settings ---------- */

  /**
   * Returns the number of characters to display for the file name in portrait mode.
   * @return int
   */
  public int getAbbreviatePortrait() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_ABBREVIATE_PORTRAIT, mDefaultAbbreviatePortrait));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultAbbreviatePortrait);
    }
  }

  /**
   * Change the number of characters to display for the file name in portrait mode.
   * @param number The new number.
   */
  public void setAbbreviatePortrait(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_ABBREVIATE_PORTRAIT, String.valueOf(number));
    e.apply();
  }

  /**
   * Returns the number of characters to display for the file name in landscape mode.
   * @return int
   */
  public int getAbbreviateLandscape() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_ABBREVIATE_LANDSCAPE, mDefaultAbbreviateLandscape));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultAbbreviateLandscape);
    }
  }

  /**
   * Change the number of characters to display for the file name in landscape mode.
   * @param number The new number.
   */
  public void setAbbreviateLandscape(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_ABBREVIATE_LANDSCAPE, String.valueOf(number));
    e.apply();
  }

  /**
   * Returns the row height auto state for the hex listview.
   * @return boolean
   */
  public boolean isHexRowHeightAuto() {
    try {
      return mSharedPreferences.getBoolean(CFG_HEX_ROW_HEIGHT_AUTO, mDefaultHexRowHeightAuto);
    } catch(Exception ignore) {
      return mDefaultHexRowHeightAuto;
    }
  }

  /**
   * Returns the row height for the hex listview.
   * @return int
   */
  public int getHexRowHeight() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_HEX_ROW_HEIGHT, mDefaultHexRowHeight));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultHexRowHeight);
    }
  }

  /**
   * Change the the row height for the hex listview.
   * @param number The new number.
   */
  public void setHexRowHeight(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_HEX_ROW_HEIGHT, String.valueOf(number));
    e.apply();
  }

  /**
   * Returns the font size for the hex listview.
   * @return int
   */
  public int getHexFontSize() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_HEX_FONT_SIZE, mDefaultHexFontSize));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultHexFontSize);
    }
  }

  /**
   * Change the the font size for the hex listview.
   * @param number The new number.
   */
  public void setHexFontSize(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_HEX_FONT_SIZE, String.valueOf(number));
    e.apply();
  }


  /**
   * Returns the row height auto state for the hex listview.
   * @return boolean
   */
  public boolean isPlainRowHeightAuto() {
    try {
      return mSharedPreferences.getBoolean(CFG_PLAIN_ROW_HEIGHT_AUTO, mDefaultPlainRowHeightAuto);
    } catch(Exception ignore) {
      return mDefaultPlainRowHeightAuto;
    }
  }

  /**
   * Returns the row height for the hex listview.
   * @return int
   */
  public int getPlainRowHeight() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_PLAIN_ROW_HEIGHT, mDefaultPlainRowHeight));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultPlainRowHeight);
    }
  }

  /**
   * Change the the row height for the hex listview.
   * @param number The new number.
   */
  public void setPlainRowHeight(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_PLAIN_ROW_HEIGHT, String.valueOf(number));
    e.apply();
  }

  /**
   * Returns the font size for the hex listview.
   * @return int
   */
  public int getPlainFontSize() {
    try {
      return Integer.parseInt(mSharedPreferences.getString(CFG_PLAIN_FONT_SIZE, mDefaultPlainFontSize));
    } catch(Exception ignore) {
      return Integer.parseInt(mDefaultPlainFontSize);
    }
  }

  /**
   * Change the the font size for the hex listview.
   * @param number The new number.
   */
  public void setPlainFontSize(int number) {
    SharedPreferences.Editor e = mSharedPreferences.edit();
    e.putString(CFG_PLAIN_FONT_SIZE, String.valueOf(number));
    e.apply();
  }
}
