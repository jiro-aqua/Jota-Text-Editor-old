package jp.sblo.pandora.jota;

import java.net.URISyntaxException;
import java.util.HashMap;

import jp.sblo.pandora.jota.text.EditText.ShortcutSettings;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener,OnSharedPreferenceChangeListener {

    private static final String KEY_FONT                    = "FONT";
    private static final String KEY_FONT_SIZE               = "FONT_SIZE";
    private static final String KEY_TEXT_COLOR              = "TEXT_COLOR";
    private static final String KEY_HIGHLIGHT_COLOR         = "HIGHLIGHT_COLOR";
    private static final String KEY_BACKGROUND              = "BACKGROUND";
//    private static final String KEY_BACKGROUND_WHITE        = "BACKGROUND_WHITE";
//    private static final String KEY_BACKGROUND_BLACK        = "BACKGROUND_BLACK";
    private static final String KEY_RE                      = "RE";
    private static final String KEY_IGNORE_CASE             = "IGNORE_CASE";
    private static final String KEY_DIRECT_INTENT           = "DIRECT_INTENT";
    private static final String KEY_DIRECT_INTENT_INTENT    = "DIRECT_INTENT_INTENT";
    private static final String KEY_DIRECT_INTENT2          = "DIRECT_INTENT2";
    private static final String KEY_DIRECT_INTENT_INTENT2   = "DIRECT_INTENT_INTENT2";
    private static final String KEY_DEFAULT_FOLDER          = "DEFAULT_FOLDER";
    private static final String KEY_SHORTCUT_ALT_LEFT       = "SHORTCUT_ALT_LEFT";
    private static final String KEY_SHORTCUT_ALT_RIGHT      = "SHORTCUT_ALT_RIGHT";
    private static final String KEY_SHORTCUT_CTRL           = "SHORTCUT_CTRL";
    private static final String KEY_SHORTCUT_CTRL_LTN       = "SHORTCUT_CTRL_LTN";
    private static final String KEY_SPECIAL_KEY_DESIREZ     = "SPECIALKEY_DESIREZ";
    private static final String KEY_REMEMBER_LAST_FILE      = "REMEMBER_LAST_FILE";
    private static final String KEY_WORD_WRAP               = "WORD_WRAP";
    private static final String KEY_THEME                   = "THEME";
    private static final String KEY_UNDERLINE               = "UNDERLINE";
    private static final String KEY_UNDERLINE_COLOR         = "UNDERLINE_COLOR";
    private static final String KEY_CRETAE_BACKUP           = "CRETAE_BACKUP";
    private static final String KEY_CHARSET_OPEN            = "CHARSET_OPEN";
    private static final String KEY_CHARSET_SAVE            = "CHARSET_SAVE";
    private static final String KEY_LINEBREAK_SAVE          = "LINEBREAK_SAVE";
    private static final String KEY_HIDETITLEBAR            = "HIDETITLEBAR";
    private static final String KEY_HIDESOFTKEY_IS01        = "HIDESOFTKEY_IS01";
    private static final String KEY_VIEWER_MODE             = "VIEWER_MODE";
    private static final String KEY_USE_VOLUMEKEY           = "USE_VOLUMEKEY";
    private static final String KEY_WRAPWIDTH_P             = "WRAPWIDTH_P";
    private static final String KEY_WRAPWIDTH_L             = "WRAPWIDTH_L";
    private static final String KEY_WRAPCHAR_P              = "WRAPCHAR_P";
    private static final String KEY_WRAPCHAR_L              = "WRAPCHAR_L";
    private static final String KEY_TAB_WIDTH               = "TAB_WIDTH";
    private static final String KEY_TAB_CHAR                = "TAB_CHAR";
    private static final String KEY_TRACKBALL_BUTTON        = "TRACKBALL_BUTTON";
    private static final String KEY_SHOW_LINENUMBERS        = "SHOW_LINENUMBERS";
    private static final String KEY_AUTO_SAVE               = "AUTO_SAVE";
    private static final String KEY_AUTO_INDENT             = "AUTO_INDENT";
    private static final String KEY_LINE_SPACE              = "LINE_SPACE";
    private static final String KEY_SHOW_TAB                = "SHOW_TAB";
    private static final String KEY_ACTION_SHARE            = "ACTION_SHARE";
    private static final String KEY_AUTO_CAPITALIZE         = "KEY_AUTO_CAPITALIZE";
    private static final String KEY_BLINK_CURSOR            = "KEY_BLINK_CURSOR";
    private static final String KEY_ORIENTATION             = "KEY_ORIENTATION";

	public static final String KEY_LASTVERSION = "LastVersion";

    public static final String  DI_INSERT = "insert";
	public static final String  DI_SHARE = "share";
	public static final String  DI_SEARCH = "search";
    public static final String  DI_MUSHROOM = "mushroom";
    public static final String  DI_VIEW = "view";

    public static final String  AS_INSERT = "insert";
    public static final String  AS_NEWFILE = "newfile";

    public static final String  THEME_DEFAULT = "default";
    public static final String  THEME_BLACK   = "black";

    public static final int BACKGROUND_DEFAULT = 0xFFF6F6F6;
    public static final int BACKGROUND_BLACK   = 0xFF000000;
    public static final int COLOR_DEFAULT = 0xFF000000;
    public static final int COLOR_BLACK   = 0xFFF6F6F6;
    public static final int UNDERLINE_COLOR = 0xFFFF0000;

    private static final int REQUEST_CODE_PICK_SHARE = 1;
    private static final int REQUEST_CODE_PICK_SEARCH = 2;
    private static final int REQUEST_CODE_PICK_MUSHROOM = 3;
    private static final int REQUEST_CODE_DEFAULT_DIR = 4;
    private static final int REQUEST_CODE_PICK_VIEW = 5;
    private static final int REQUEST_CODE_PICK_SHARE2 = 6;
    private static final int REQUEST_CODE_PICK_SEARCH2 = 7;
    private static final int REQUEST_CODE_PICK_MUSHROOM2 = 8;
    private static final int REQUEST_CODE_PICK_VIEW2 = 9;

    public static final String DEFAULT_WRAP_WIDTH_CHAR = "m";

    public static final String TB_CENTERING = "CENTERING";
    public static final String TB_ENTER = "ENTER";
    public static final String TB_CONTEXTMENU = "CONTEXTMENU";

    public static final String PREF_HISTORY = "history";   // .xml

    public static final String EXTRA_CATEGORY = "category";
    public static final String CAT_TOP = "top";
    public static final String CAT_SEARCH = "search";
    public static final String CAT_FONT = "font";
    public static final String CAT_VIEW = "view";
    public static final String CAT_INPUT = "input";
    public static final String CAT_FILE = "file";
    public static final String CAT_MISC = "misc";

    public static final String ORI_AUTO="auto";
    public static final String ORI_PORTRAIT="portrait";
    public static final String ORI_LANDSCAPE="landscape";

    private PreferenceScreen mPs = null;
	private PreferenceManager mPm = getPreferenceManager();

    private ListPreference mPrefFont;
    private ListPreference mPrefFontSize;
	private ListPreference mPrefCharsetOpen;
	private ListPreference mPrefCharsetSave;
	private ListPreference mPrefLinebreakSave;
    private ListPreference mPrefDirectIntent;
    private ListPreference mPrefInsert;
    private ListPreference mPrefTrackball;
    private ListPreference mPrefActionShare;
    private ListPreference mPrefOrientation;
    private Preference mPrefWrapWidthP;
    private Preference mPrefWrapWidthL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPm = getPreferenceManager();

		Intent it = getIntent();
		String category = it.getStringExtra(EXTRA_CATEGORY);

		createDictionaryPreference(category);
	}


    private void createDictionaryPreference(String categ) {
        // new PreferenceScreen
        mPs = mPm.createPreferenceScreen(this);

        {
//            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            if ( CAT_TOP.equals(categ) ){
                setTitle(R.string.menu_preferences);
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.menu_pref_search);
                    pr.setOnPreferenceClickListener(mProcPrefSearch);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.menu_pref_font);
                    pr.setOnPreferenceClickListener(mProcPrefFont);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.menu_pref_view);
                    pr.setOnPreferenceClickListener(mProcPrefView);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.menu_pref_file);
                    pr.setOnPreferenceClickListener(mProcPrefFile);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.menu_pref_input);
                    pr.setOnPreferenceClickListener(mProcPrefInput);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_miscllaneous);
                    pr.setOnPreferenceClickListener(mProcPrefMisc);
                    mPs.addPreference(pr);
                }
            }
            if ( CAT_SEARCH.equals(categ) ){
                // Search Category
                setTitle(R.string.menu_pref_search);
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_search);

                mPs.addPreference(category);
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_RE);
                    pr.setTitle(R.string.label_re);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_IGNORE_CASE);
                    pr.setTitle(R.string.label_ignore_case);
                    category.addPreference(pr);
                }
            }

            if ( CAT_FONT.equals(categ) ){
                // Font Category
                setTitle(R.string.menu_pref_font);
                final PreferenceCategory catfont = new PreferenceCategory(this);
                catfont.setTitle(R.string.label_font);
                mPs.addPreference(catfont);
                {
                    // Font Typeface
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_FONT);
                    pr.setTitle(R.string.label_font_type);
                    pr.setEntries(new String[]{ getString(R.string.label_font_type_normal) , getString(R.string.label_font_type_monospace) } );
                    pr.setEntryValues( new CharSequence[] { "NORMAL" , "MONOSPACE" } );
//                    pr.setSummary(sp.getString(pr.getKey(), ""));
                    catfont.addPreference(pr);
                    mPrefFont = pr;
                }
                {
                    // FontSize
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_FONT_SIZE);
//                    pr.setSummary(sp.getString(pr.getKey(), ""));
                    pr.setTitle(R.string.label_font_size);
                    pr.setEntries(new String[]     {"8", "9", "10", "11", "12" ,"14", "16", "18", "20", "24", "30", "36", "48", "64", "72", "96",  });
                    pr.setEntryValues(new String[] {"8", "9", "10", "11", "12" ,"14", "16", "18", "20", "24", "30", "36", "48", "64", "72", "96",  });
                    catfont.addPreference(pr);
                    mPrefFontSize = pr;
                }
            }
            if ( CAT_VIEW.equals(categ) ){
                // View Category
                setTitle(R.string.menu_pref_view);
                final PreferenceCategory cat = new PreferenceCategory(this);
                cat.setTitle(R.string.label_view);
                mPs.addPreference(cat);
                {
                    // word wrap
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_WORD_WRAP);
                    pr.setTitle(R.string.label_word_wrap);
                    cat.addPreference(pr);
                }
                {
                    // Wrap width portrait
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_wrapwidth_p);
                    pr.setSummary(R.string.summary_wrapwidth_p);
                    pr.setOnPreferenceClickListener(mProcWrapWidthPortrait);
                    cat.addPreference(pr);
                    mPrefWrapWidthP = pr;
                }
                {
                    // Wrap width landscape
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_wrapwidth_l);
                    pr.setSummary(R.string.summary_wrapwidth_l);
                    pr.setOnPreferenceClickListener(mProcWrapWidthLandscape);
                    cat.addPreference(pr);
                    mPrefWrapWidthL = pr;
                }
                {
                    // Tab width
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_tabwidth);
                    pr.setOnPreferenceClickListener(mProcTabWidthLandscape);
                    cat.addPreference(pr);
                }
                {
                    // Line Spacing
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_line_space);
                    pr.setOnPreferenceClickListener(mProcLineSpace);
                    cat.addPreference(pr);
                }
                {
                    // show underline
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_UNDERLINE);
                    pr.setTitle(R.string.label_underline);
                    cat.addPreference(pr);
                }
                {
                    // show line numbers
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHOW_LINENUMBERS);
                    pr.setTitle(R.string.label_show_linenumbers);
                    cat.addPreference(pr);
                }
                {
                    // show tab
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHOW_TAB);
                    pr.setTitle(R.string.label_show_tab);
                    cat.addPreference(pr);
                }
                {   // theme
                    final ListPreference pr = new ListPreference(this);
                    pr.setDialogTitle(R.string.label_theme);
                    pr.setKey(KEY_THEME);
                    pr.setTitle(R.string.label_theme);

                    pr.setEntries(new String[] {
                            getResources().getString(R.string.label_background_white),
                            getResources().getString(R.string.label_background_black),
                    });

                    final String[] values = new String[] {
                            THEME_DEFAULT,
                            THEME_BLACK,
                    };
                    pr.setEntryValues(values);
                    pr.setOnPreferenceChangeListener( mProcTheme );
                    cat.addPreference(pr);
                }
                {
                    // Text Color
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_text_color);
                    pr.setOnPreferenceClickListener(mProcTextColor);
                    cat.addPreference(pr);
                }
                {
                    // Selection Color
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_highlight_color);
                    pr.setOnPreferenceClickListener(mProcHighlightColor);
                    cat.addPreference(pr);
                }
                {
                    // Underline Color
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_underline_color);
                    pr.setOnPreferenceClickListener(mProcUnderlineColor);
                    cat.addPreference(pr);
                }
                {
                    // blink cursor
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_BLINK_CURSOR);
                    pr.setTitle(R.string.label_blink_cursor);
                    cat.addPreference(pr);
                }
                {
                    // hide titlebar
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_HIDETITLEBAR );
                    pr.setTitle(R.string.label_hide_titlebar);
                    pr.setSummary(R.string.summary_need_restart);
                    cat.addPreference(pr);
                }
                {
                    // screen orientation
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey(KEY_ORIENTATION);
                    pr.setDialogTitle(R.string.label_orientation);
                    pr.setTitle(R.string.label_orientation);

                    pr.setEntries(new String[] {
                            getResources().getString(R.string.label_orientation_auto),
                            getResources().getString(R.string.label_orientation_portrait),
                            getResources().getString(R.string.label_orientation_landscape),
                    });

                    final String[] values = new String[] {
                            ORI_AUTO,
                            ORI_PORTRAIT,
                            ORI_LANDSCAPE,
                    };
                    pr.setEntryValues(values);
                    cat.addPreference(pr);
                    mPrefOrientation = pr;
                }
                if ( IS01FullScreen.isIS01orLynx() ){
                    // hide softkey
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_HIDESOFTKEY_IS01);
                    pr.setTitle(R.string.label_hide_softkey_is01);
                    pr.setSummary(R.string.summary_need_restart);
                    cat.addPreference(pr);
                }
            }

            if ( CAT_FILE.equals(categ) ){
                // File Category
                setTitle(R.string.menu_pref_file);
                final PreferenceCategory cat = new PreferenceCategory(this);
                cat.setTitle(R.string.label_file);
                mPs.addPreference(cat);
                {
                    // default directory
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_default_new_file);
                    pr.setOnPreferenceClickListener(mProcDefaultDirectory);
                    cat.addPreference(pr);
                }
                {
                    // rememer last file
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_REMEMBER_LAST_FILE);
                    pr.setTitle(R.string.label_open_last_file);
                    pr.setSummary(R.string.label_open_last_file_summary);
                    cat.addPreference(pr);
                }
                {
                    // create backup file
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_CRETAE_BACKUP);
                    pr.setTitle(R.string.label_create_backup);
                    pr.setSummary(R.string.summary_create_backup);
                    cat.addPreference(pr);
                }
                {
                    // Characterset for Open
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_CHARSET_OPEN );
                    pr.setTitle(R.string.label_charset_open);
                    String[] entries = getResources().getStringArray(R.array.CharcterSet_open);
                    String[] values = getResources().getStringArray(R.array.CharcterSet_open);
                    values[0]="";
                    pr.setEntries( entries );
                    pr.setEntryValues( values );
                    cat.addPreference(pr);
                    mPrefCharsetOpen = pr;
                }
                {
                    // Characterset for Save
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_CHARSET_SAVE );
                    pr.setTitle(R.string.label_charset_save);
                    String[] entries = getResources().getStringArray(R.array.CharcterSet_save);
                    String[] values = getResources().getStringArray(R.array.CharcterSet_save);
                    values[0]="";
                    pr.setEntries( entries );
                    pr.setEntryValues( values );
                    cat.addPreference(pr);
                    mPrefCharsetSave = pr;
                }
                {
                    // Characterset for Save
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_LINEBREAK_SAVE );
                    pr.setTitle(R.string.label_linebreak_save);
                    String[] entries = getResources().getStringArray(R.array.LineBreak_save);
                    String[] values = new String[] { "-1" , "0", "1" , "2" };
                    pr.setEntries( entries );
                    pr.setEntryValues( values );
                    cat.addPreference(pr);
                    mPrefLinebreakSave = pr;
                }
                {
                    // auto save
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_AUTO_SAVE);
                    pr.setTitle(R.string.label_auto_save);
                    pr.setSummary(R.string.summary_auto_save);
                    cat.addPreference(pr);
                }
            }
            if ( CAT_MISC.equals(categ) ){
                // Direct Intent Category
                setTitle(R.string.label_miscllaneous);
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_direct_intent);

                mPs.addPreference(category);
                {
                    final ListPreference pr = new ListPreference(this);
                    pr.setDialogTitle(R.string.label_select_kind);
                    pr.setKey(KEY_DIRECT_INTENT);
                    pr.setTitle(R.string.label_select_direct_intent);

                    pr.setEntries(new String[] {
                            getResources().getString(R.string.label_di_share),
                            getResources().getString(R.string.label_di_search),
                            getResources().getString(R.string.label_di_mushroom),
                            getResources().getString(R.string.label_di_view),
                    });

                    final String[] values = new String[] {
                            DI_SHARE,
                            DI_SEARCH,
                            DI_MUSHROOM,
                            DI_VIEW,
                    };
                    pr.setEntryValues(values);

                    pr.setOnPreferenceChangeListener( mProcDirectIntent );
                    category.addPreference(pr);
                    mPrefDirectIntent = pr;
                }
                {
                    final ListPreference pr = new ListPreference(this);
                    pr.setDialogTitle(R.string.label_select_kind);
                    pr.setKey(KEY_DIRECT_INTENT2);
                    pr.setTitle(R.string.label_select_insert);

                    pr.setEntries(new String[] {
                            getResources().getString(R.string.label_di_insert),
                            getResources().getString(R.string.label_di_share),
                            getResources().getString(R.string.label_di_search),
                            getResources().getString(R.string.label_di_mushroom),
                            getResources().getString(R.string.label_di_view),
                    });

                    final String[] values = new String[] {
                            DI_INSERT,
                            DI_SHARE,
                            DI_SEARCH,
                            DI_MUSHROOM,
                            DI_VIEW,
                    };
                    pr.setEntryValues(values);

                    pr.setOnPreferenceChangeListener( mProcDirectIntent2 );
                    category.addPreference(pr);
                    mPrefInsert = pr;
                }
            }
            if ( CAT_INPUT.equals(categ) ){
                // Input Category
                setTitle(R.string.menu_pref_input);
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_input);

                mPs.addPreference(category);
                {
                    // viewer mode
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_VIEWER_MODE );
                    pr.setTitle(R.string.label_viewer_mode);
                    pr.setSummary(R.string.summary_viewer_mode);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_USE_VOLUMEKEY);
                    pr.setTitle(R.string.label_use_volumekey);
                    pr.setSummary(R.string.summary_use_volumekey);
                    category.addPreference(pr);
                }
                {
                    // Trackball Button
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_TRACKBALL_BUTTON );
                    pr.setTitle(R.string.label_trackball_button);
                    pr.setEntries(new String[]{ getString(R.string.trackball_centering) , getString(R.string.trackball_enter) , getString(R.string.trackball_contextmenu) } );
                    pr.setEntryValues( new CharSequence[] { TB_CENTERING , TB_ENTER , TB_CONTEXTMENU } );
                    category.addPreference(pr);
                    mPrefTrackball = pr;
                }
                {   // AUto capitalize
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_AUTO_CAPITALIZE);
                    pr.setTitle(R.string.label_auto_capitalize);
                    pr.setSummary(R.string.summary_auto_capitalize);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHORTCUT_ALT_LEFT);
                    pr.setTitle(R.string.label_shortcut_alt_left);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHORTCUT_ALT_RIGHT);
                    pr.setTitle(R.string.label_shortcut_alt_right);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHORTCUT_CTRL);
                    pr.setTitle(R.string.label_shortcut_ctrl);
                    pr.setSummary(R.string.summary_ctrl_daz);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SHORTCUT_CTRL_LTN);
                    pr.setTitle(R.string.label_shortcut_ctrl);
                    pr.setSummary(R.string.summary_ctrl_ltn);
                    category.addPreference(pr);
                }
                {
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_SPECIAL_KEY_DESIREZ);
                    pr.setTitle(R.string.label_shortcut_ctrl);
                    pr.setSummary(R.string.summary_desirez);
                    category.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_customize_shortcut);
                    pr.setOnPreferenceClickListener(mProcShortcutSettings);
                    category.addPreference(pr);
                }
                {
                    // auto indent
                    final CheckBoxPreference pr = new CheckBoxPreference(this);
                    pr.setKey(KEY_AUTO_INDENT);
                    pr.setTitle(R.string.label_auto_indent);
                    category.addPreference(pr);
                }
            }
            if ( CAT_MISC.equals(categ) ){
                // Misc Category
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_miscllaneous);
                mPs.addPreference(category);

                {
                    // ACTION SHARE
                    final ListPreference pr = new ListPreference(this);
                    pr.setKey( KEY_ACTION_SHARE );
                    pr.setTitle(R.string.label_action_share);
                    pr.setSummary(R.string.summary_action_share);
                    pr.setEntries(new String[]{ getString(R.string.label_action_share_insert) , getString(R.string.label_action_share_newfile) } );
                    pr.setEntryValues( new CharSequence[] { AS_INSERT , AS_NEWFILE , } );
                    category.addPreference(pr);
                    mPrefActionShare = pr;
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_clear_history);
                    pr.setOnPreferenceClickListener(mProcClearHisotry);
                    category.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_init);
                    pr.setOnPreferenceClickListener(mProcInit);
                    category.addPreference(pr);
                }
            }

            if ( CAT_MISC.equals(categ) ){
                // Help Category
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_help);

                mPs.addPreference(category);
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_help);
                    pr.setOnPreferenceClickListener(mProcHelp);
                    category.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_mail);
                    pr.setOnPreferenceClickListener(mProcMail);
                    pr.setSummary(R.string.label_mail_summary);
                    category.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_tweet);
                    pr.setOnPreferenceClickListener(mProcTweet);
                    pr.setSummary(R.string.label_tweet_summary);
                    category.addPreference(pr);
                }
                {      // donate
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_donate);
                    pr.setSummary(R.string.summary_donate);
                    pr.setOnPreferenceClickListener(mProcDonate);
                    category.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_about);
                    pr.setOnPreferenceClickListener(mProcAbout);
                    category.addPreference(pr);
                }
            }
            if ( CAT_TOP.equals(categ) ){
                if ( sSettings.donateCounter == 0 ){      // donate
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_donate);
                    pr.setSummary(R.string.summary_donate);
                    pr.setOnPreferenceClickListener(mProcDonate);
                    mPs.addPreference(pr);
                }
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_about);
                    pr.setOnPreferenceClickListener(mProcAbout);
                    mPs.addPreference(pr);
                }

            }
        }
        setPreferenceScreen(mPs);

        setSummary();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode ==  RESULT_OK ){
            switch( requestCode )
            {
                case REQUEST_CODE_PICK_SHARE:
                case REQUEST_CODE_PICK_SEARCH:
                case REQUEST_CODE_PICK_MUSHROOM:
                case REQUEST_CODE_PICK_VIEW:
                {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(KEY_DIRECT_INTENT_INTENT, data.toUri(0) );
                    editor.commit();
                    setSummary();
                    break;
                }
                case REQUEST_CODE_DEFAULT_DIR:{
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    Bundle extras = data.getExtras();
                    String path = extras.getString(FileSelectorActivity.INTENT_DIRPATH);
                    editor.putString(KEY_DEFAULT_FOLDER, path );
                    editor.commit();
                    break;
                }
                case REQUEST_CODE_PICK_SHARE2:
                case REQUEST_CODE_PICK_SEARCH2:
                case REQUEST_CODE_PICK_MUSHROOM2:
                case REQUEST_CODE_PICK_VIEW2:
                {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(KEY_DIRECT_INTENT_INTENT2, data.toUri(0) );
                    editor.commit();
                    setSummary();
                    break;
                }
            }
        }else if ( resultCode == RESULT_FIRST_USER ){
            switch( requestCode ){
                case REQUEST_CODE_PICK_MUSHROOM:{
                    Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( getString( R.string.no_reciever_url) ));
                    try{
                        startActivity(intent);
                    }catch(Exception e){}
                }
            }
        }else  if ( resultCode == RESULT_CANCELED ){
            switch( requestCode )
            {
                case REQUEST_CODE_PICK_SHARE:
                case REQUEST_CODE_PICK_SEARCH:
                case REQUEST_CODE_PICK_MUSHROOM:
                case REQUEST_CODE_PICK_VIEW:
                {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(KEY_DIRECT_INTENT, "");
                    editor.putString(KEY_DIRECT_INTENT_INTENT, "" );
                    editor.commit();
                    setSummary();
                    break;
                }
                case REQUEST_CODE_DEFAULT_DIR:{
                    break;
                }
                case REQUEST_CODE_PICK_SHARE2:
                case REQUEST_CODE_PICK_SEARCH2:
                case REQUEST_CODE_PICK_MUSHROOM2:
                case REQUEST_CODE_PICK_VIEW2:
                {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(KEY_DIRECT_INTENT2, DI_INSERT);
                    editor.putString(KEY_DIRECT_INTENT_INTENT2, "" );
                    editor.commit();
                    mPrefInsert.setValueIndex(0);
                    setSummary();
                    break;
                }
            }
        }
    }


    private OnPreferenceClickListener mProcInit = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {

            new AlertDialog.Builder(SettingsActivity.this)
            .setMessage( getString( R.string.msg_init_setting) )
            .setTitle( R.string.label_init )
            .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putInt(KEY_LASTVERSION, 0 );
                    editor.commit();
                    isVersionUp(SettingsActivity.this);
                    finish();
               }
            })
            .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show();
            return false;
        }

    };
    private OnPreferenceClickListener mProcClearHisotry = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {

            SharedPreferences sp = getSharedPreferences(SettingsActivity.PREF_HISTORY , MODE_PRIVATE);
            sp.edit().clear().commit();

            Toast.makeText( SettingsActivity.this, R.string.toast_clear_history, Toast.LENGTH_LONG).show();
            return false;
        }

    };

    private OnPreferenceClickListener mProcHelp = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( getString( R.string.help_url) ));
            try{
                startActivity(intent);
            }catch(Exception e){}
            finish();

            return false;
        }

	};

    private OnPreferenceClickListener mProcMail = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent it = new Intent();
            it.setAction(Intent.ACTION_SENDTO );
            int mill = (int)(System.currentTimeMillis() / 1000 / 60 /60 );
            it.setData(Uri.parse("mailto:" + getString(R.string.label_mail_summary)
                    + "?subject=Jota Text Editor(" + mill
                    + ")"));
            try{
                startActivity(it);
            }catch(Exception e){}
            finish();
            return false;
        }

    };
    private OnPreferenceClickListener mProcTweet = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( getString( R.string.tweet_url) ));
            try{
                startActivity(intent);
            }catch(Exception e){}
            finish();
            return false;
        }
    };
    private OnPreferenceClickListener mProcDonate = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( SettingsActivity.this,DonateActivity.class);
            intent.putExtra( AboutActivity.EXTRA_URL ,  getString( R.string.url_donate ) );
            intent.putExtra( AboutActivity.EXTRA_TITLE ,  getString( R.string.label_donate ) );
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcAbout = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( SettingsActivity.this,AboutActivity.class);
            startActivity(intent);
            return true;
        }
    };

    private OnPreferenceClickListener mProcPrefSearch = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_SEARCH);
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcPrefFont = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_FONT);
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcPrefView = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_VIEW);
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcPrefInput = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_INPUT);
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcPrefFile = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_FILE);
            startActivity(intent);
            return true;
        }
    };
    private OnPreferenceClickListener mProcPrefMisc = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            intent.putExtra( SettingsActivity.EXTRA_CATEGORY, SettingsActivity.CAT_MISC);
            startActivity(intent);
            return true;
        }
    };



    private void showWrapWidthDialog( final String chrkey ,final String numkey , int title , int message , final int min, final int max)
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.wrapwidth, null);

        TextView msgText = (TextView)view.findViewById(R.id.message);
        msgText.setText( message );
        TextView msgNumber = (TextView)view.findViewById(R.id.numberlabel);
        msgNumber.setText( getString(R.string.label_number ,  min, max ));

        final EditText numInput = (EditText)view.findViewById(R.id.number);
        numInput.setText( ""+sp.getInt(numkey,0) );

        final EditText chrInput = (EditText)view.findViewById(R.id.character);
        chrInput.setText( sp.getString(chrkey,DEFAULT_WRAP_WIDTH_CHAR) );


        new AlertDialog.Builder(SettingsActivity.this)
        .setTitle(title)
        .setView( view )
        .setPositiveButton( R.string.label_ok , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor editor = sp.edit();

                String text = numInput.getText().toString();
                int val = 0;
                try {
                    val = Integer.parseInt(text);
                }catch(Exception e){}
                if ( val < min || val >max ){
                    val = min;
                    Toast.makeText(SettingsActivity.this, R.string.toast_error_wrap_width, Toast.LENGTH_LONG);
                }
                editor.putInt(numkey, val);

                text = chrInput.getText().toString();
                if ( text.length() != 1 ){
                    text = DEFAULT_WRAP_WIDTH_CHAR;
                    Toast.makeText(SettingsActivity.this, R.string.toast_error_wrap_width, Toast.LENGTH_LONG);
                }
                editor.putString(chrkey, text);

                editor.commit();
            }
        })
        .setNegativeButton( R.string.label_cancel , null )
        .show();
    }

    private void showLineSpaceDialog( final String key , int title , int message )
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.linespace, null);

        TextView msgText = (TextView)view.findViewById(R.id.message);
        msgText.setText( message );

        final SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekbar);
        seekBar.setProgress( sp.getInt(key,0) );


        new AlertDialog.Builder(SettingsActivity.this)
        .setTitle(title)
        .setView( view )
        .setPositiveButton( R.string.label_ok , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor editor = sp.edit();

                int val = seekBar.getProgress();
                editor.putInt(key, val);
                editor.commit();
            }
        })
        .setNegativeButton( R.string.label_cancel , null )
        .show();
    }

    private OnPreferenceClickListener mProcWrapWidthPortrait= new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            showWrapWidthDialog(KEY_WRAPCHAR_P,KEY_WRAPWIDTH_P,R.string.label_wrapwidth_p , R.string.comment_wrapwidth ,0,99);
            return true;
        }
    };

    private OnPreferenceClickListener mProcWrapWidthLandscape= new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            showWrapWidthDialog(KEY_WRAPCHAR_L,KEY_WRAPWIDTH_L,R.string.label_wrapwidth_l , R.string.comment_wrapwidth ,0,99);
            return true;
        }
    };

    private OnPreferenceClickListener mProcTabWidthLandscape= new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            showWrapWidthDialog(KEY_TAB_CHAR,KEY_TAB_WIDTH,R.string.label_tabwidth , R.string.comment_tabwidth ,1,99);
            return true;
        }
    };

    private OnPreferenceClickListener mProcLineSpace= new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            showLineSpaceDialog(KEY_LINE_SPACE,R.string.label_line_space , R.string.message_line_space);
            return true;
        }
    };

    private OnPreferenceChangeListener mProcDirectIntent = new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // lets launch app picker if the user selected to launch an app on gesture
            Intent mainIntent=null;
            int req = 0;
            if (newValue.equals( DI_SHARE ))
            {
                mainIntent = new Intent(Intent.ACTION_SEND, null);
                mainIntent.setType("text/plain");
                mainIntent.addCategory(Intent.CATEGORY_DEFAULT);

                req = REQUEST_CODE_PICK_SHARE;
            } else if (newValue.equals( DI_SEARCH )) {
                mainIntent = new Intent(Intent.ACTION_SEARCH, null);

                req = REQUEST_CODE_PICK_SEARCH;
            } else if (newValue.equals( DI_MUSHROOM )) {
                mainIntent = new Intent( "com.adamrocker.android.simeji.ACTION_INTERCEPT" );
                mainIntent.addCategory("com.adamrocker.android.simeji.REPLACE");

                req = REQUEST_CODE_PICK_MUSHROOM;
            } else if (newValue.equals( DI_VIEW )) {
                mainIntent = new Intent(Intent.ACTION_VIEW);
                mainIntent.setDataAndType(Uri.parse("file://"), "text/plain");

                req = REQUEST_CODE_PICK_VIEW;
            }
            if ( mainIntent != null ){
                Intent pickIntent = new Intent(SettingsActivity.this,ActivityPicker.class);
                pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
                startActivityForResult(pickIntent,req);
            }
            return true;
        }
    };

    private OnPreferenceChangeListener mProcDirectIntent2 = new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // lets launch app picker if the user selected to launch an app on gesture
            Intent mainIntent=null;
            int req = 0;
            if (newValue.equals( DI_SHARE ))
            {
                mainIntent = new Intent(Intent.ACTION_SEND, null);
                mainIntent.setType("text/plain");
                mainIntent.addCategory(Intent.CATEGORY_DEFAULT);

                req = REQUEST_CODE_PICK_SHARE2;
            } else if (newValue.equals( DI_SEARCH )) {
                mainIntent = new Intent(Intent.ACTION_SEARCH, null);

                req = REQUEST_CODE_PICK_SEARCH2;
            } else if (newValue.equals( DI_MUSHROOM )) {
                mainIntent = new Intent( "com.adamrocker.android.simeji.ACTION_INTERCEPT" );
                mainIntent.addCategory("com.adamrocker.android.simeji.REPLACE");

                req = REQUEST_CODE_PICK_MUSHROOM2;
            } else if (newValue.equals( DI_VIEW )) {
                mainIntent = new Intent(Intent.ACTION_VIEW);
                mainIntent.setDataAndType(Uri.parse("file://"), "text/plain");

                req = REQUEST_CODE_PICK_VIEW2;
            } else if (newValue.equals( DI_INSERT )) {
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                Editor editor = sp.edit();
                editor.putString(KEY_DIRECT_INTENT_INTENT2, "");
                editor.commit();
            }
            if ( mainIntent != null ){
                Intent pickIntent = new Intent(SettingsActivity.this,ActivityPicker.class);
                pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
                startActivityForResult(pickIntent,req);
            }
            return true;
        }
    };

    private OnPreferenceChangeListener mProcTheme = new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            Editor editor = sp.edit();

            if ( SettingsActivity.THEME_DEFAULT.equals(newValue) ){
                editor.putInt( KEY_TEXT_COLOR , COLOR_DEFAULT );
                editor.putInt( KEY_BACKGROUND, BACKGROUND_DEFAULT );
            }else if ( SettingsActivity.THEME_BLACK.equals(newValue) ){
                editor.putInt( KEY_TEXT_COLOR , COLOR_BLACK );
                editor.putInt( KEY_BACKGROUND, BACKGROUND_BLACK );
            }
            editor.putInt(KEY_HIGHLIGHT_COLOR, getTextColorHighlight(SettingsActivity.this) );
            editor.putInt( KEY_UNDERLINE_COLOR, UNDERLINE_COLOR );
            editor.commit();
            return true;
        }
    };

    abstract class ColorProc implements   OnPreferenceClickListener , ColorPickerDialog.OnColorChangedListener {}

    private ColorProc mProcTextColor = new ColorProc(){
        public boolean onPreferenceClick(Preference preference) {

            ColorPickerDialog cpd = new ColorPickerDialog(SettingsActivity.this ,this,
                    sSettings.textcolor,
                    sSettings.backgroundcolor,
                    false,
                    getString(R.string.label_text_color)) ;
            cpd.show();
            return true;
        }

        public void colorChanged(int fg, int bg) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            Editor editor = sp.edit();
            editor.putInt( KEY_TEXT_COLOR , fg );
            editor.commit();

        }
    };

    private ColorProc mProcHighlightColor  = new ColorProc(){
        public boolean onPreferenceClick(Preference preference) {

            ColorPickerDialog cpd = new ColorPickerDialog(SettingsActivity.this ,this,
                    sSettings.textcolor,
                    sSettings.highlightcolor,
                    true,
                    getString(R.string.label_highlight_color)) ;
            cpd.show();
            return true;
        }

        public void colorChanged(int fg, int bg) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            Editor editor = sp.edit();
            editor.putInt( KEY_HIGHLIGHT_COLOR, bg );
            editor.commit();

        }
    };

    private ColorProc mProcUnderlineColor  = new ColorProc(){
        public boolean onPreferenceClick(Preference preference) {

            ColorPickerDialog cpd = new ColorPickerDialog(SettingsActivity.this ,this,
                    sSettings.underlinecolor,
                    sSettings.backgroundcolor,
                    false,
                    getString(R.string.label_highlight_color)) ;
            cpd.show();
            return true;
        }

        public void colorChanged(int fg, int bg) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            Editor editor = sp.edit();
            editor.putInt( KEY_UNDERLINE_COLOR, fg );
            editor.commit();
        }
    };


    private OnPreferenceClickListener mProcDefaultDirectory = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( SettingsActivity.this , FileSelectorActivity.class );
            intent.putExtra(FileSelectorActivity.INTENT_MODE, FileSelectorActivity.MODE_DIR);
            intent.putExtra(FileSelectorActivity.INTENT_INIT_PATH, sSettings.defaultdirectory );

            startActivityForResult(intent, REQUEST_CODE_DEFAULT_DIR);

            return true;
        }

    };

    private OnPreferenceClickListener mProcShortcutSettings = new OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(SettingsActivity.this , SettingsShortcutActivity.class );
            startActivity(intent);
            return true;
        }
    };




    public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;
	}


	public static class Settings {
		boolean re;
		boolean ignorecase;
		Intent directintent;
		String intentname;
		Typeface fontface;
		int fontsize;
		String defaultdirectory;
        boolean shortcutaltleft;
        boolean shortcutaltright;
        boolean shortcutctrl;
        boolean shortcutctrlltn;
        boolean rememberlastfile;
        boolean wordwrap;
        String theme;
        int backgroundcolor;
        int textcolor;
        int highlightcolor;
        int underlinecolor;
        boolean underline;
        boolean createbackup;
        HashMap<Integer,ShortcutSettings> shortcuts;
        String CharsetOpen;
        String CharsetSave;
        int LinebreakSave;
        Intent directintent2;
        String intentname2;
        boolean useVolumeKey;
        int WrapWidthP;
        int WrapWidthL;
        String WrapCharP;
        String WrapCharL;
        int TabWidth;
        String TabChar;
        String TrackballButton;
        boolean showLineNumbers;
        boolean autosave;
        boolean autoIndent;
        int lineSpace;
        boolean showTab;
        String actionShare;
        int donateCounter;
        boolean specialkey_desirez;
        boolean blinkCursor;
	}

	public static class BootSettings {
        boolean hideTitleBar;
        boolean hideSoftkeyIS01;
        boolean viewerMode;
        boolean autoCapitalize;
        String screenOrientation;
	}

    private static Settings sSettings;
    private static BootSettings sBootSettings;

	public	static Settings readSettings(Context ctx)
	{
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		Settings ret = new Settings();

		ret.re = sp.getBoolean(KEY_RE, false);
		ret.ignorecase = sp.getBoolean(KEY_IGNORE_CASE, true);
		String di = sp.getString(KEY_DIRECT_INTENT_INTENT, "");
        ret.directintent = null;
        ret.intentname=null;
	    try {
	        if ( di.length() > 0 ){
	            ret.directintent  = Intent.parseUri( di, 0);
	            ret.intentname = ret.directintent.getExtras().getString( ActivityPicker.EXTRA_APPNAME );
	            ret.directintent.removeExtra(ActivityPicker.EXTRA_APPNAME);
	        }
        } catch (URISyntaxException e) {
        }
        String di2 = sp.getString(KEY_DIRECT_INTENT_INTENT2, "");
        ret.directintent2 = null;
        ret.intentname2=null;
        try {
            if ( di2.length() > 0 ){
                ret.directintent2  = Intent.parseUri( di2, 0);
                ret.intentname2 = ret.directintent2.getExtras().getString( ActivityPicker.EXTRA_APPNAME );
                ret.directintent2.removeExtra(ActivityPicker.EXTRA_APPNAME);
            }
        } catch (URISyntaxException e) {
        }
        ret.fontsize = Integer.parseInt( sp.getString( KEY_FONT_SIZE , "18") );
        CharSequence font = sp.getString(KEY_FONT, "NORMAL");
        if ( "NORMAL".equals(font) ){
            ret.fontface = Typeface.DEFAULT;
        }else if ("MONOSPACE".equals(font)) {
            ret.fontface = Typeface.MONOSPACE;
        }else{
            ret.fontface = Typeface.DEFAULT;
        }
        ret.defaultdirectory = sp.getString( KEY_DEFAULT_FOLDER , Environment.getExternalStorageDirectory().getPath() );
        ret.shortcutaltleft = sp.getBoolean( KEY_SHORTCUT_ALT_LEFT, false);
        ret.shortcutaltright = sp.getBoolean( KEY_SHORTCUT_ALT_RIGHT, false);
        ret.shortcutctrl = sp.getBoolean( KEY_SHORTCUT_CTRL, false);
        ret.shortcutctrlltn = sp.getBoolean( KEY_SHORTCUT_CTRL_LTN, false);
        ret.specialkey_desirez = sp.getBoolean( KEY_SPECIAL_KEY_DESIREZ, false);
        ret.rememberlastfile = sp.getBoolean( KEY_REMEMBER_LAST_FILE, false);
        ret.wordwrap = sp.getBoolean( KEY_WORD_WRAP, true);
        ret.theme = sp.getString(KEY_THEME, THEME_DEFAULT);
        ret.textcolor = sp.getInt(KEY_TEXT_COLOR,0);
        ret.highlightcolor = sp.getInt(KEY_HIGHLIGHT_COLOR,0);
        ret.backgroundcolor = sp.getInt(KEY_BACKGROUND, 0);
        ret.underlinecolor =  sp.getInt(KEY_UNDERLINE_COLOR, 0);
        ret.underline = sp.getBoolean(KEY_UNDERLINE, true);
        ret.createbackup = sp.getBoolean(KEY_CRETAE_BACKUP, true);
        ret.shortcuts = SettingsShortcutActivity.loadShortcuts(ctx);
        ret.CharsetOpen = sp.getString(KEY_CHARSET_OPEN, "");
        ret.CharsetSave = sp.getString(KEY_CHARSET_SAVE, "");
        ret.LinebreakSave = Integer.parseInt( sp.getString(KEY_LINEBREAK_SAVE, "-1") );
        ret.useVolumeKey = sp.getBoolean(KEY_USE_VOLUMEKEY, true);
        ret.WrapWidthP =  sp.getInt(KEY_WRAPWIDTH_P, 0);
        ret.WrapWidthL =  sp.getInt(KEY_WRAPWIDTH_L, 0);
        ret.WrapCharP =  sp.getString(KEY_WRAPCHAR_P, DEFAULT_WRAP_WIDTH_CHAR);
        ret.WrapCharL =  sp.getString(KEY_WRAPCHAR_L, DEFAULT_WRAP_WIDTH_CHAR);
        ret.TabWidth =  sp.getInt(KEY_TAB_WIDTH, 4);
        ret.TabChar =  sp.getString(KEY_TAB_CHAR, DEFAULT_WRAP_WIDTH_CHAR);
        ret.TrackballButton = sp.getString(KEY_TRACKBALL_BUTTON, TB_CENTERING);
        ret.showLineNumbers = sp.getBoolean( KEY_SHOW_LINENUMBERS, false);
        ret.autosave = sp.getBoolean( KEY_AUTO_SAVE, false);
        ret.autoIndent = sp.getBoolean( KEY_AUTO_INDENT, false);
        ret.lineSpace = sp.getInt( KEY_LINE_SPACE , 0);
        ret.showTab = sp.getBoolean( KEY_SHOW_TAB, false);
        ret.actionShare = sp.getString(KEY_ACTION_SHARE, AS_INSERT);
        ret.donateCounter = sp.getInt(DonateActivity.DONATION_COUNTER,0);
        ret.blinkCursor = sp.getBoolean(KEY_BLINK_CURSOR, true);
        sSettings = ret;
        return ret;
	}

    public  static BootSettings readBootSettings(Context ctx)
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        BootSettings ret = new BootSettings();

        ret.hideTitleBar = sp.getBoolean(KEY_HIDETITLEBAR , false);
        ret.hideSoftkeyIS01 = sp.getBoolean(KEY_HIDESOFTKEY_IS01 , false);
        ret.viewerMode = sp.getBoolean(KEY_VIEWER_MODE, false);
        ret.autoCapitalize = sp.getBoolean(KEY_AUTO_CAPITALIZE, false);
        ret.screenOrientation = sp.getString(KEY_ORIENTATION, ORI_AUTO);
        sBootSettings = ret;
        return ret;
    }

	public static boolean isVersionUp(Context ctx)
	{
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean ret = false;
		int lastversion = sp.getInt(KEY_LASTVERSION, 0 );
		int versioncode;
		try {
		    String pkgname = ctx.getApplicationInfo().packageName;
			versioncode = ctx.getPackageManager().getPackageInfo(pkgname, 0).versionCode;
			ret = (lastversion != versioncode);

			if ( ret ){
				Editor editor = sp.edit();
				editor.putInt(KEY_LASTVERSION, versioncode );

				// set default
				if ( lastversion < 1 ){
				    editor.putBoolean(KEY_RE, false);
                    editor.putBoolean(KEY_IGNORE_CASE, true);
                    editor.putString(KEY_DIRECT_INTENT, "");
                    editor.putString(KEY_DIRECT_INTENT_INTENT, "");
				}
                if ( lastversion < 2 ){
                    editor.putString(KEY_FONT, "NORMAL");
                    editor.putString(KEY_FONT_SIZE, "18");
                    editor.putString(KEY_DEFAULT_FOLDER, Environment.getExternalStorageDirectory().getPath());
				}
                if ( lastversion < 3 ){

                    editor.putBoolean(KEY_SHORTCUT_ALT_LEFT, false);
                    editor.putBoolean(KEY_SHORTCUT_ALT_RIGHT, false);
                    editor.putBoolean(KEY_SHORTCUT_CTRL, false);
                    editor.putBoolean(KEY_REMEMBER_LAST_FILE, false);
                    editor.putBoolean(KEY_WORD_WRAP, true);
                    editor.putString(KEY_THEME, THEME_DEFAULT);
                    editor.putInt(KEY_TEXT_COLOR, COLOR_DEFAULT);
                    editor.putInt(KEY_HIGHLIGHT_COLOR , getTextColorHighlight(ctx) );
                    editor.putInt( KEY_BACKGROUND , BACKGROUND_DEFAULT );
                    editor.putBoolean(KEY_UNDERLINE, true);
                    editor.putInt( KEY_UNDERLINE_COLOR, UNDERLINE_COLOR );
                }
                if ( lastversion < 5 ){
                    editor.putBoolean(KEY_CRETAE_BACKUP, true);
                }
                if ( lastversion < 6 ){
                    editor.putString(KEY_CHARSET_OPEN, "");
                    editor.putString(KEY_CHARSET_SAVE, "");
                    editor.putString(KEY_LINEBREAK_SAVE, "-1");
                    editor.putString(KEY_DIRECT_INTENT2, DI_INSERT);
                    editor.putString(KEY_DIRECT_INTENT_INTENT2, "");
                }
                if ( lastversion < 8 ){
                    editor.putBoolean(KEY_HIDETITLEBAR, false);
                    editor.putBoolean(KEY_HIDESOFTKEY_IS01, false);
                }
                if ( lastversion < 9 ){
                    editor.putBoolean(KEY_VIEWER_MODE, false);
                    editor.putBoolean(KEY_SHORTCUT_CTRL_LTN, false);
                }
                if ( lastversion < 10 ){
                    editor.putBoolean(KEY_USE_VOLUMEKEY, true);
                    editor.putInt(KEY_WRAPWIDTH_P, 0);
                    editor.putInt(KEY_WRAPWIDTH_L, 0);
                    editor.putInt(KEY_TAB_WIDTH, 4);
                    editor.putString(KEY_WRAPCHAR_P, DEFAULT_WRAP_WIDTH_CHAR);
                    editor.putString(KEY_WRAPCHAR_L, DEFAULT_WRAP_WIDTH_CHAR);
                    editor.putString(KEY_TAB_CHAR, DEFAULT_WRAP_WIDTH_CHAR);
                    editor.putString(KEY_TRACKBALL_BUTTON, TB_CENTERING);
                }
                if ( lastversion < 12 ){
                    editor.putBoolean(KEY_SHOW_LINENUMBERS, false );
                }
                if ( lastversion < 15 ){
                    editor.putBoolean(KEY_AUTO_INDENT, false );
                    editor.putBoolean(KEY_AUTO_SAVE, false );
                    editor.putInt(KEY_LINE_SPACE, 0 );
                }
                if ( lastversion < 17 ){
                    editor.putBoolean(KEY_SHOW_TAB, false );
                    editor.putString(KEY_ACTION_SHARE, AS_INSERT);
                }
                if ( lastversion < 29 ){
                    editor.putBoolean(KEY_SPECIAL_KEY_DESIREZ, false );
                }
                if ( lastversion < 31 ){
                    editor.putBoolean(KEY_BLINK_CURSOR, true );
                    editor.putString(KEY_ORIENTATION, ORI_AUTO );
                }
                editor.commit();
                SettingsShortcutActivity.writeDefaultShortcuts(ctx);
			}

		} catch (NameNotFoundException e) {
		}
		return ret;
	}

	private static int getTextColorHighlight(Context ctx)
	{
        TypedArray a =
            ctx.obtainStyledAttributes(
                null, android.R.styleable.TextView, android.R.attr.textViewStyle, 0);
        TypedArray appearance = null;
        int ap = a.getResourceId(android.R.styleable.TextView_textAppearance, -1);
        if (ap != -1) {
            appearance = ctx.obtainStyledAttributes(ap, android.R.styleable. TextAppearance);
        }
        return appearance.getColor(android.R.styleable.TextAppearance_textColorHighlight, 0);
	}

	private void setSummary()
	{
	    CharSequence entry;
	    String intentname;

	    sSettings = readSettings(this);

	    if ( mPrefDirectIntent != null ){
    	    if ( sSettings.directintent != null ){
        	    entry = mPrefDirectIntent.getEntry();
        	    intentname = sSettings.intentname;
        	    if ( entry != null ){
                    if ( intentname != null ){
                        mPrefDirectIntent.setSummary(entry+" : " +intentname);
                    }else{
                        mPrefDirectIntent.setSummary(entry );
                    }
        	    }
    	    }else{
    	        mPrefDirectIntent.setSummary(null);
    	    }
	    }
	    if ( mPrefInsert!= null ){
            entry = mPrefInsert.getEntry();
            intentname = sSettings.intentname2;
            if ( entry != null ){
                if ( intentname != null ){
                    mPrefInsert.setSummary(entry+" : " +intentname);
                }else{
                    mPrefInsert.setSummary(entry );
                }
            }
	    }
	    if ( mPrefCharsetOpen != null ){
            entry = mPrefCharsetOpen.getEntry();
            if ( entry != null ){
                mPrefCharsetOpen.setSummary(entry);
            }
	    }
	    if ( mPrefCharsetSave != null ){
	        entry = mPrefCharsetSave.getEntry();
	        if ( entry != null ){
	            mPrefCharsetSave.setSummary(entry);
	        }
	    }
        if ( mPrefLinebreakSave != null ){
            entry = mPrefLinebreakSave.getEntry();
            if ( entry != null ){
                mPrefLinebreakSave.setSummary(entry);
            }
        }
        if ( mPrefFont != null ){
            entry = mPrefFont.getEntry();
            if ( entry != null ){
                mPrefFont.setSummary(entry);
            }
        }
        if ( mPrefFontSize != null ){
            entry = mPrefFontSize.getEntry();
            if ( entry != null ){
                mPrefFontSize.setSummary(entry);
            }
        }
        if ( mPrefTrackball != null ){
            entry = mPrefTrackball.getEntry();
            if ( entry != null ){
                mPrefTrackball.setSummary(entry);
            }
        }
        if ( mPrefActionShare != null ){
            entry = mPrefActionShare.getEntry();
            if ( entry != null ){
                mPrefActionShare.setSummary(entry);
            }
        }
        if ( mPrefOrientation != null ){
            entry = mPrefOrientation.getEntry();
            if ( entry != null ){
                mPrefOrientation.setSummary(entry + "\n" + getString(R.string.summary_need_restart));
            }
        }
        if ( mPrefWrapWidthP != null ){
            mPrefWrapWidthP.setEnabled(sSettings.wordwrap);
        }
        if ( mPrefWrapWidthL != null ){
            mPrefWrapWidthL.setEnabled(sSettings.wordwrap);
        }
	}

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummary();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPs.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mPs.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}

