package jp.sblo.pandora.jota;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity
{

	final static String ABOUT_PAGE = "file:///android_asset/about.html";
	public final static String EXTRA_URL = "URL";
	public final static String EXTRA_TITLE = "TITLE";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		String url = ABOUT_PAGE;

		Intent it = getIntent();
		if ( it != null ){
			Bundle extras = it.getExtras();
			if ( extras !=null ){
				String iturl = extras.getString(EXTRA_URL);
				if ( iturl !=null ){
					url = iturl;
				}
				String ittitle = extras.getString(EXTRA_TITLE);
				if ( ittitle !=null ){
					setTitle( ittitle );
				}
			}else{
			    url = getString(R.string.about_url);
                setTitle( R.string.about_title );
			}
		}

		WebView webview = (WebView)findViewById(R.id.WebView01);
		webview.loadUrl( url );

		final JsCallbackObj jsobj = new JsCallbackObj();
		webview.addJavascriptInterface(jsobj, "jscallback");

		webview.getSettings().setJavaScriptEnabled(true);
		webview.setFocusable(true);
		webview.setFocusableInTouchMode(true);
	}

	public class JsCallbackObj
	{

		public JsCallbackObj()
		{
		}

		public String getAboutStrings(String key)
		{
			if (key.equals("version")) {

				String versionName = "-.-";
				int versionCode = 0;
				PackageManager pm = getPackageManager();
				try {
	                String pkgname = AboutActivity.this.getPackageName();
                    PackageInfo info = pm.getPackageInfo(pkgname, 0);
					versionName = info.versionName;
					versionCode = info.versionCode;
				} catch (NameNotFoundException e) {
				}
				return "Ver. " + String.format("%s (%d)", versionName, versionCode);

			} else {
				return "";
			}
		}


		public void throwIntentByUrl( String url , int requestcode )
		{
			if ( url!=null && url.length()>0 ){
				Intent it = new Intent( Intent.ACTION_VIEW , Uri.parse(url) );
				startActivityForResult(it, requestcode);
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK && requestCode==1000 ){		// DL rerquest
			setResult( RESULT_OK , data );
			finish();
		}
	}

}
