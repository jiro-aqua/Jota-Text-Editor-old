package jp.sblo.pandora.jota;

import java.util.HashMap;

import jp.sblo.pandora.jota.text.EditText;
import jp.sblo.pandora.jota.text.EditText.ShortcutSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;

public class SettingsShortcutActivity extends PreferenceActivity  {

    private static final String KEY_SHORTCUT                    = "SHORTCUT_";

    static class DefineShortcut {
        int key;
        String name;
        boolean show;
        int function;

        public DefineShortcut( int k, String n ,boolean s , int f){
            key = k;
            name = n;
            show = s;
            function = f;
        }
    };


    private final static int[] TBL_SUMMARY = {
        R.string.selectAll,
        R.string.menu_edit_undo,
        R.string.copy,
        R.string.cut,
        R.string.paste,
        R.string.menu_direct,
        R.string.menu_file_save,
        R.string.enter,
        R.string.tab,
        R.string.backspace,
        R.string.trackball_centering,
        R.string.label_search,
        R.string.label_open_file,
        R.string.label_new_file,
        R.string.label_redo,
        R.string.trackball_contextmenu,
        R.string.menu_edit_jump,
        R.string.label_forward_del,
    };

    public String getFunctionName(int func)
    {
        if ( 0<= func && func < TBL_SUMMARY.length ){
            return getString( TBL_SUMMARY[func] );
        }
        return "";
    }

    private static final DefineShortcut[] TBL_SHORTCUT = new DefineShortcut[]{
        new DefineShortcut( KeyEvent.KEYCODE_A ,"A" , true  , EditText.FUNCTION_SELECT_ALL ),
        new DefineShortcut( KeyEvent.KEYCODE_B ,"B" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_C ,"C" , true  , EditText.FUNCTION_COPY ),
        new DefineShortcut( KeyEvent.KEYCODE_D ,"D" , true  , EditText.FUNCTION_DIRECTINTENT ),
        new DefineShortcut( KeyEvent.KEYCODE_E ,"E" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_F ,"F" , true  , EditText.FUNCTION_SEARCH ),
        new DefineShortcut( KeyEvent.KEYCODE_G ,"G" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_H ,"H" , true  , EditText.FUNCTION_DEL ),
        new DefineShortcut( KeyEvent.KEYCODE_I ,"I" , true  , EditText.FUNCTION_TAB ),
        new DefineShortcut( KeyEvent.KEYCODE_J ,"J" , true  , EditText.FUNCTION_JUMP ),
        new DefineShortcut( KeyEvent.KEYCODE_K ,"K" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_L ,"L" , true  , EditText.FUNCTION_CENTERING ),
        new DefineShortcut( KeyEvent.KEYCODE_M ,"M" , true  , EditText.FUNCTION_ENTER ),
        new DefineShortcut( KeyEvent.KEYCODE_N ,"N" , true  , EditText.FUNCTION_NEWFILE ),
        new DefineShortcut( KeyEvent.KEYCODE_O ,"O" , true  , EditText.FUNCTION_OPEN ),
        new DefineShortcut( KeyEvent.KEYCODE_P ,"P" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_Q ,"Q" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_R ,"R" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_S ,"S" , true  , EditText.FUNCTION_SAVE ),
        new DefineShortcut( KeyEvent.KEYCODE_T ,"T" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_U ,"U" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_V ,"V" , true  , EditText.FUNCTION_PASTE ),
        new DefineShortcut( KeyEvent.KEYCODE_W ,"W" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_X ,"X" , true  , EditText.FUNCTION_CUT ),
        new DefineShortcut( KeyEvent.KEYCODE_Y ,"Y" , true  , EditText.FUNCTION_REDO ),
        new DefineShortcut( KeyEvent.KEYCODE_Z ,"Z" , true  , EditText.FUNCTION_UNDO ),
        new DefineShortcut( KeyEvent.KEYCODE_1 ,"1" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_2 ,"2" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_3 ,"3" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_4 ,"4" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_5 ,"5" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_6 ,"6" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_7 ,"7" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_8 ,"8" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_9 ,"9" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_0 ,"0" , false , EditText.FUNCTION_NONE ),
        new DefineShortcut( KeyEvent.KEYCODE_DEL ,"Del" , true , EditText.FUNCTION_FORWARD_DEL ),
    };

    private PreferenceScreen mPs = null;
    private PreferenceManager mPm = getPreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPm = getPreferenceManager();

        mPs = mPm.createPreferenceScreen(this);
        // Category
        final PreferenceCategory category = new PreferenceCategory(this);
        category.setTitle(R.string.label_customize_shortcut);

        mPs.addPreference(category);

        for( DefineShortcut sd : TBL_SHORTCUT )
        {
            if ( sd.show ){
                final CheckBoxPreference pr = new CheckBoxPreference(this);
                pr.setKey(KEY_SHORTCUT + sd.key );
                pr.setTitle( sd.name );
                pr.setSummary(getFunctionName(sd.function));
                category.addPreference(pr);
            }
        }
        setPreferenceScreen(mPs);

    }

    static public HashMap<Integer,ShortcutSettings> loadShortcuts(Context context)
    {
        HashMap<Integer,ShortcutSettings> result = new HashMap<Integer,ShortcutSettings>();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        for( DefineShortcut sd : TBL_SHORTCUT )
        {
            if ( sd.show ){
                String key = KEY_SHORTCUT + sd.key;
                boolean enabled = sp.getBoolean(key, true);
                int function = sd.function;
                result.put(sd.key, new ShortcutSettings(enabled, function));
            }
        }
        return result;
    }

    static public void writeDefaultShortcuts(Context context)
    {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        for( DefineShortcut sd : TBL_SHORTCUT )
        {
            if ( sd.show ){
                String key = KEY_SHORTCUT + sd.key;
                if ( !sp.contains(key) ){
                    editor.putBoolean(key, true);
                }
            }
            editor.commit();
        }
    }

}
