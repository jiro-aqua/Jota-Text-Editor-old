package jp.sblo.pandora.jota;

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
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;

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

	public static final String KEY_LASTVERSION = "LastVersion";


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

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;
	}


	public static class Settings {
		boolean re;
		boolean ignorecase;
	}

	public	static Settings readSettings(Context ctx)
	{
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		Settings ret = new Settings();

		ret.re = sp.getBoolean(KEY_RE, false);
		ret.ignorecase = sp.getBoolean(KEY_IGNORE_CASE, true);
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
				}
				editor.commit();
			}

		} catch (NameNotFoundException e) {
		}
		return ret;
	}

}

