package jp.sblo.pandora.jota;

import java.net.URISyntaxException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String KEY_FONT                    = "FONT";
    private static final String KEY_FONT_SIZE               = "FONT_SIZE";
    private static final String KEY_TEXT_COLOR              = "TEXT_COLOR";
    private static final String KEY_HIGHLIGHT_COLOR         = "HIGHLIGHT_COLOR";
    private static final String KEY_BACKGROUND              = "BACKGROUND";
    private static final String KEY_BACKGROUND_WHITE        = "BACKGROUND_WHITE";
    private static final String KEY_BACKGROUND_BLACK        = "BACKGROUND_BLACK";
    private static final String KEY_RE                      = "RE";
    private static final String KEY_IGNORE_CASE             = "IGNORE_CASE";
    private static final String KEY_DIRECT_INTENT           = "DIRECT_INTENT";
    private static final String KEY_DIRECT_INTENT_INTENT    = "DIRECT_INTENT_INTENT";

	public static final String KEY_LASTVERSION = "LastVersion";

	public static final String  DI_SHARE = "share";
	public static final String  DI_SEARCH = "search";
	public static final String  DI_MUSHROOM = "mushroom";

    private static final int REQUEST_CODE_PICK_SHARE = 1;
    private static final int REQUEST_CODE_PICK_SEARCH = 2;
    private static final int REQUEST_CODE_PICK_MUSHROOM = 3;

	private PreferenceScreen mPs = null;
	private PreferenceManager mPm = getPreferenceManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPm = getPreferenceManager();

		createDictionaryPreference();

	}


    private void createDictionaryPreference() {
        // new PreferenceScreen
        mPs = mPm.createPreferenceScreen(this);

        {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            {
                // Search Category
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

            {
                // Direct Intent Category
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
                    });

                    final String[] values = new String[] {
                            DI_SHARE,
                            DI_SEARCH,
                            DI_MUSHROOM,
                    };
                    pr.setEntryValues(values);

                    pr.setOnPreferenceChangeListener( mProcDirectIntent );
                    category.addPreference(pr);
                }
            }

            {
                // Help Category
                final PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(R.string.label_help);

                mPs.addPreference(category);
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_init);
                    pr.setOnPreferenceClickListener(mProcInit);
                    category.addPreference(pr);
                }
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
                {
                    final Preference pr = new Preference(this);
                    pr.setTitle(R.string.label_about);
                    pr.setOnPreferenceClickListener(mProcAbout);
                    category.addPreference(pr);
                }
            }
        }
        setPreferenceScreen(mPs);
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
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    Editor editor = sp.edit();
                    editor.putString(KEY_DIRECT_INTENT_INTENT, data.toUri(0) );
                    editor.commit();
                    break;
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
                    + ")&amp;body=(write your question here)(in English or in Japanese"));
            try{
                startActivity(it);
            }catch(Exception e){}
            finish();
            return false;
        }

    };
    private OnPreferenceClickListener mProcTweet = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent it = new Intent();
            it.setAction( Intent.ACTION_SEND );
            it.setType("text/plain");
            it.putExtra(Intent.EXTRA_TEXT, getString(R.string.label_tweet_summary) + " #JotaTextEditor " );
            try{
                startActivity(it);
            }catch(Exception e){}
            finish();
            return false;
        }

    };
    private OnPreferenceClickListener mProcAbout = new OnPreferenceClickListener(){
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent( SettingsActivity.this,AboutActivity.class);
            startActivity(intent);
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
            }
            if ( mainIntent != null ){
                Intent pickIntent = new Intent(SettingsActivity.this,ActivityPicker.class);
                pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
                startActivityForResult(pickIntent,req);
            }
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
	}

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
				editor.commit();
			}

		} catch (NameNotFoundException e) {
		}
		return ret;
	}

}

