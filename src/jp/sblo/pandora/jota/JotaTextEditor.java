package jp.sblo.pandora.jota;

import android.app.Application;


public class JotaTextEditor extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SettingsActivity.isVersionUp(this);
        IS01FullScreen.createInstance();
    }

}
