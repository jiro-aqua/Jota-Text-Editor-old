package jp.sblo.pandora.jota;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import jp.sblo.pandora.jota.Search.OnSearchFinishedListener;
import jp.sblo.pandora.jota.Search.Record;
import jp.sblo.pandora.jota.TextLoadTask.OnFileLoadListener;
import jp.sblo.pandora.jota.text.JotaDocumentWatcher;
import jp.sblo.pandora.jota.text.SpannableStringBuilder;
import jp.sblo.pandora.jota.text.EditText.ShortcutListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Main
        extends Activity
        implements JotaDocumentWatcher,ShortcutListener , OnFileLoadListener  {
    private static final String TAG = "JotaTextEditor";

    private static final String PREF_HISTORY = "history";   // .xml

    private static final int    REQUESTCODE_OPEN = 0;
    private static final int    REQUESTCODE_SAVEAS = 1;
    private static final int    REQUESTCODE_MUSHROOM =2;
    private static final int    REQUESTCODE_SEARCHBYINTENT =3;
    private static final int    REQUESTCODE_APPCHOOSER = 4;

    private static final String DEF_CHARSET = "utf-8";
    private static final int    DEF_LINEBREAK = LineBreak.LF;

    private static final int    PREF_MODE = MODE_WORLD_READABLE;

    private jp.sblo.pandora.jota.text.EditText mEditor;
    private LinearLayout mLlSearch;
    private EditText mEdtSearchWord;
    private ImageButton mBtnForward;
    private ImageButton mBtnBackward;
    private CheckBox mChkReplace;
    private ImageButton mBtnClose;
    private LinearLayout mLlReplace;
    private EditText mEdtReplaceWord;
    private Button mBtnSkip;
    private Button mBtnReplaceAll;
    private String mNewFilename;

    private TextLoadTask mTask;
//    private String mSearchWord;
//    private int mLine;

    private Intent mReservedIntent;
    private int mReservedRequestCode;


    private Runnable mProcAfterSaveConfirm = null;

    private InstanceState   mInstanceState = new InstanceState();

    private ArrayList<Search.Record> mSearchResult;
    private boolean mSearchForward;

    private SettingsActivity.Settings mSettings;

    class InstanceState {
        String  filename;
        String  charset;
//        String  text;
        int     linebreak;
//        int     selstart;
//        int     selend;
        boolean changed;
    };



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textviewer);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        mSettings = SettingsActivity.readSettings(this);

        mEditor = (jp.sblo.pandora.jota.text.EditText)findViewById(R.id.textedit);
        mEditor.setDocumentChangedListener(this);
        mEditor.setShortcutListener(this);
        mEditor.setChanged(false);

        mLlSearch = (LinearLayout )  findViewById(R.id.search);
        mLlReplace = (LinearLayout ) findViewById(R.id.replace);

        mEdtSearchWord = (EditText ) findViewById(R.id.edtSearchWord);
        mBtnForward = (ImageButton )      findViewById(R.id.btnForward);
        mBtnBackward = (ImageButton )     findViewById(R.id.btnBackward);
        mChkReplace = (CheckBox )    findViewById(R.id.chkReplace);
        mBtnClose = (ImageButton )        findViewById(R.id.btnClose);
        mEdtReplaceWord = (EditText )    findViewById(R.id.edtReplaceWord);
        mBtnSkip = (Button )     findViewById(R.id.btnSkip);
        mBtnReplaceAll = (Button )   findViewById(R.id.btnReplaceAll);



        mEdtSearchWord.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enabled = ( s.length() > 0 );
                mBtnForward.setEnabled(enabled);
                mBtnBackward.setEnabled(enabled);
                mSearchResult = null;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });

        mEdtSearchWord.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ( (keyCode == KeyEvent.KEYCODE_ENTER  || keyCode == KeyEvent.KEYCODE_DPAD_CENTER )
                        && event.getAction() == KeyEvent.ACTION_UP ) {
                    if ( mBtnForward.isEnabled() ){
                        mBtnForward.performClick();
                    }
                }
                return false;
            }
        });

        mBtnForward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String searchword = mEdtSearchWord.getText().toString();
                mSearchForward = true;
                doSearch( searchword );
            }
        });
        mBtnBackward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String searchword = mEdtSearchWord.getText().toString();
                mSearchForward = false;
                doSearch( searchword );
            }
        });
        mChkReplace.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( mLlSearch.getVisibility()==View.VISIBLE && isChecked ){
                    mLlReplace.setVisibility(View.VISIBLE);
                }else{
                    mLlReplace.setVisibility(View.GONE);
                }
            }
        });
        mBtnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mLlSearch.setVisibility(View.GONE);
                mLlReplace.setVisibility(View.GONE);
                mChkReplace.setChecked(false);
            }
        });
//        edtReplaceWord
//        btnSkip
//        btnReplaceAll



        mProcNew.run();

        if (savedInstanceState==null){
            Intent it = getIntent();
            if (it!=null && Intent.ACTION_VIEW.equals(it.getAction())){
                Uri data = it.getData();
                String path = Uri.decode( data.getSchemeSpecificPart().substring(2) );      // skip "//"

//                mSearchWord = null;
//                mLine = -1;
//
//                Bundle extra = it.getExtras();
//                if ( extra!=null ){
//                    mSearchWord = extra.getString("query");
//                    mLine = extra.getInt("line");
//                }

                mTask = new TextLoadTask( this , this );
                mTask.execute(path);
            }else if (it!=null && Intent.ACTION_SEND.equals(it.getAction())){
                Bundle extras = it.getExtras();
                String text = extras.getString(Intent.EXTRA_TEXT);
                if ( text != null ){
                    mEditor.setText(text);
                }
            }

        }else{
            mInstanceState.filename = savedInstanceState.getString("filename" );
            mInstanceState.charset = savedInstanceState.getString("charset"  );
//            mInstanceState.text = savedInstanceState.getString("text"  );
            mInstanceState.linebreak = savedInstanceState.getInt("linebreak" );
//            mInstanceState.selstart = savedInstanceState.getInt("selstart"  );
//            mInstanceState.selend = savedInstanceState.getInt("selend" );
            mInstanceState.changed = savedInstanceState.getBoolean("changed" );

//            mEditor.setText(mInstanceState.text);
//            mEditor.setSelection(mInstanceState.selstart, mInstanceState.selend);
            mEditor.setChanged(mInstanceState.changed);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    public void onPreFileLoad() {
    }
    public void onFileLoaded(SpannableStringBuilder result , String filename, String charset, int linebreak) {
        mTask = null;
        if ( result != null ){
            mInstanceState.filename = filename;
            mInstanceState.charset = charset;
            mInstanceState.linebreak = linebreak;


            SpannableStringBuilder ss =  result ;
            mEditor.setText(ss);
            mEditor.setChanged(false);

            SharedPreferences sp = getSharedPreferences(PREF_HISTORY,PREF_MODE);
            String sel = sp.getString(filename, "-1,-1");

            int selStart = -1;
            int selEnd = -1;
            if ( sel != null ){
                String [] sels = sel.split(",");
                if ( sels.length >= 2 ){
                    try{
                        selStart = Integer.parseInt(sels[0]);
                        selEnd = Integer.parseInt(sels[1]);

                        if ( selStart >=0 && selEnd >= 0 ){
                            int len = mEditor.length();
                            if ( selStart >= len ){
                                selStart = len-1;
                            }
                            if ( selEnd >= len ){
                                selEnd = len-1;
                            }
                            mEditor.setSelection(selStart,selEnd);
                            mEditor.centerCursor();
                        }

                    }catch(Exception e){
                        selStart = -1;
                        selEnd = -1;
                    }
                }
            }
            saveHistory();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.e(TAG,"onSaveInstanceState=========================================================>");
//        mInstanceState.text = mEditor.getText().toString();
//        mInstanceState.selstart = mEditor.getSelectionStart();
//        mInstanceState.selend = mEditor.getSelectionEnd();

        outState.putString("filename" , mInstanceState.filename );
        outState.putString("charset" , mInstanceState.charset );
//        outState.putString("text" , mInstanceState.text );
        outState.putInt("linebreak" , mInstanceState.linebreak );
//        outState.putInt("selstart" , mInstanceState.selstart );
//        outState.putInt("selend" , mInstanceState.selend );
        outState.putBoolean("changed" , mEditor.isChanged() );

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mInstanceState.filename = savedInstanceState.getString("filename" );
        mInstanceState.charset = savedInstanceState.getString("charset"  );
//        mInstanceState.text = savedInstanceState.getString("text"  );
        mInstanceState.linebreak = savedInstanceState.getInt("linebreak" );
//        mInstanceState.selstart = savedInstanceState.getInt("selstart"  );
//        mInstanceState.selend = savedInstanceState.getInt("selend" );
        mInstanceState.changed = savedInstanceState.getBoolean("changed" );

//        mEditor.setText(mInstanceState.text);
//        mEditor.setSelection(mInstanceState.selstart, mInstanceState.selend);
        mEditor.setChanged(mInstanceState.changed);

    }

    @Override
    public void onLowMemory() {
        Log.e(TAG,"onLowMemory()");
        super.onLowMemory();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent!=null && Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri data = intent.getData();
            mNewFilename = Uri.decode( data.getSchemeSpecificPart().substring(2) );      // skip "//"

            if ( !mNewFilename.equals(mInstanceState.filename)){
                confirmSave(mProcReopen);
            }

        }else if (intent!=null && Intent.ACTION_SEND.equals(intent.getAction())){
            Bundle extras = intent.getExtras();
            String inserttext = extras.getString(Intent.EXTRA_TEXT);
            if ( inserttext != null ){
                Editable text = mEditor.getText();
                int startsel = mEditor.getSelectionStart();
                int endsel = mEditor.getSelectionEnd();
                if ( endsel < startsel ){
                    int temp = startsel ;
                    startsel = endsel;
                    endsel = temp;
                }
                text.replace(startsel, endsel, inserttext);
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        saveHistory();
    }

    private void saveHistory()
    {
        if (mInstanceState.filename!=null ){
            int selstart = mEditor.getSelectionStart();
            int selend = mEditor.getSelectionEnd();

            SharedPreferences sp = getSharedPreferences(PREF_HISTORY,PREF_MODE);
            Editor editor = sp.edit();
            editor.putString(mInstanceState.filename, String.format("%d,%d,%d", selstart , selend , System.currentTimeMillis() ));
            editor.commit();
        }
    }




    //    @Override JotaDocumentWatcher#onChanged()
    public void onChanged()
    {
        boolean changed = mEditor.isChanged();

        String name = getString(R.string.hint_message );
        if ( mInstanceState.filename != null ){
            File f = new File(mInstanceState.filename );
            name = f.getName();
        }
        if ( changed ){
            name += "*";
        }
        this.setTitle( name );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ( mLlSearch.getVisibility() == View.VISIBLE ){
                mBtnClose.performClick();
                return true;
            }
            if ( confirmSave(mProcQuit) ){
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_SEARCH ) {
            mProcSearch.run();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean confirmSave( Runnable procAfterSaveConfirm )
    {
        mProcAfterSaveConfirm = null;
        if ( mEditor.isChanged() ){
            mProcAfterSaveConfirm = procAfterSaveConfirm;

            String msg;
            if ( mInstanceState.filename == null ){
                msg = getString(R.string.confirmation_message_null);
            }else{
                msg = getString(R.string.confirmation_message, mInstanceState.filename);
            }

            new AlertDialog.Builder(this)
            .setTitle(R.string.confirmation)
            .setMessage( msg  )
            .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    save();
               }
            })
            .setNeutralButton(R.string.label_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if ( mProcAfterSaveConfirm!=null ){
                        mProcAfterSaveConfirm.run();
                        mProcAfterSaveConfirm = null;
                    }
                }
            })
            .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show();


            return true;
        }else{
            procAfterSaveConfirm.run();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    private void save( )
    {
        String filename = mInstanceState.filename;
        if ( filename != null ){
            String charset = mInstanceState.charset;
            int linebreak = mInstanceState.linebreak;
            String text = mEditor.getText().toString();
            String lb = "\n";
            if (linebreak == LineBreak.CR) {
                lb = "'\r";
            } else if (linebreak == LineBreak.CRLF) {
                lb = "\r\n";
            }

            new TextSaveTask( this ,  null , new Runnable(){
                public void run()
                {
                    saveHistory();
                    if ( mProcAfterSaveConfirm!=null ){
                        mProcAfterSaveConfirm.run();
                        mProcAfterSaveConfirm = null;
                    }
                    mEditor.setChanged(false);
                    onChanged();
                }
            })
            .execute(filename , charset , lb , text );
        }else{
            saveAs();
        }

    }

    private void saveAs()
    {
        Intent intent = new Intent( this , FileList.class );
        intent.putExtra(FileList.INTENT_MODE, FileList.MODE_SAVE);
        startActivityForResult(intent, REQUESTCODE_SAVEAS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK ){
            switch( requestCode ){
                case REQUESTCODE_OPEN:{
                    Bundle extras = data.getExtras();
                    String path = extras.getString(FileList.INTENT_FILEPATH);
                    mTask = new TextLoadTask( this , this );
                    mTask.execute(path);
                }
                break;
                case REQUESTCODE_SAVEAS:{
                    Bundle extras = data.getExtras();
                    mInstanceState.filename = extras.getString(FileList.INTENT_FILEPATH);
                    mInstanceState.charset = DEF_CHARSET;
                    mInstanceState.linebreak =DEF_LINEBREAK;;
                    save();
                }
                break;
                case REQUESTCODE_MUSHROOM:{
                    Bundle extras = data.getExtras();
                    String insertstr = extras.getString("replace_key");
                    if ( insertstr != null ){
                        Editable text = mEditor.getText();
                        int startsel = mEditor.getSelectionStart();
                        int endsel = mEditor.getSelectionEnd();
                        if ( endsel < startsel ){
                            int temp = startsel ;
                            startsel = endsel;
                            endsel = temp;
                        }
                        text.replace(startsel, endsel, insertstr);
                    }
                }
                break;
                case REQUESTCODE_APPCHOOSER:{
                    Intent intent = mReservedIntent;
                    int request = mReservedRequestCode;

                    ComponentName component = data.getComponent();
                    intent.setComponent(component);

                    try{
                        if ( request ==  REQUESTCODE_MUSHROOM ){
                            startActivityForResult(intent, request);
                        }else{
                            startActivity(intent);
                        }
                    }
                    catch(Exception e)
                    {
                    }
                }
                break;

            }
        }
        mReservedIntent = null;
        mReservedRequestCode = 0;
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_insert: {
                mProcInsert.run();
            }
            return true;
            case R.id.menu_search: {
                mProcSearch.run();
            }
            return true;
            case R.id.menu_preferences: {
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.menu_file_history:{
                confirmSave(mProcHistory);
            }
            return true;
            case R.id.menu_file_save:{
                save();
            }
            return true;
            case R.id.menu_file_open:{
                confirmSave(mProcOpen);
            }
            return true;
            case R.id.menu_file_saveas:{
                saveAs();
            }
            return true;
            case R.id.menu_file_new:{
                confirmSave(mProcNew);
            }
            return true;
            case R.id.menu_file_charcode:{
                mProcCharSet.run();
            }
            return true;
            case R.id.menu_file_lbcode:{
                mProcLineBreak.run();
            }
            return true;
            case R.id.menu_file_quit:{
                confirmSave(mProcQuit);
            }
            return true;
            case R.id.menu_edit_undo:{
                mEditor.onKeyShortcut( KeyEvent.KEYCODE_Z , new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z) );
            }
            return true;
            case R.id.menu_edit_cut:{
                mEditor.onKeyShortcut( KeyEvent.KEYCODE_X , new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_X) );
            }
            return true;
            case R.id.menu_edit_copy:{
                mEditor.onKeyShortcut( KeyEvent.KEYCODE_C , new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C) );
            }
            return true;
            case R.id.menu_edit_paste:{
                mEditor.onKeyShortcut( KeyEvent.KEYCODE_V , new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V) );
            }
            return true;
            case R.id.menu_search_byintent:{
                mProcSearchByIntent.run();
            }
            return true;
            case R.id.menu_share:{
                mProcShare.run();
            }
            return true;

        }
////            Intent intent = new Intent();
////            intent.setClassName("jp.sblo.pandora.adice", "jp.sblo.pandora.adice.SettingsActivity");
////            startActivity(intent);
//            return true;
//        }
//        if (id == R.id.help) {
//            Intent intent = new Intent();
//            intent.setClassName("jp.sblo.pandora.adice", "jp.sblo.pandora.adice.AboutActivity");
//            startActivity(intent);
//            return true;
//        }

        return super.onMenuItemSelected(featureId, item);
    }



    //    @Override
    public boolean onCommand(int keycode) {
        switch( keycode ){
            case KeyEvent.KEYCODE_S:
                save();
                return true;
            case KeyEvent.KEYCODE_R:
                mProcSearchByIntent.run();
                return true;
        }
        return false;
    }


    private Runnable mProcQuit =  new Runnable() {
        public void run() {
            Main.this.finish();
        }
    };

    private Runnable mProcOpen =  new Runnable() {
        public void run() {
            Intent intent = new Intent( Main.this , FileList.class );
            intent.putExtra(FileList.INTENT_MODE, FileList.MODE_OPEN);
            startActivityForResult(intent, REQUESTCODE_OPEN);
        }
    };

    private Runnable mProcNew =  new Runnable() {
        public void run() {
            mInstanceState.filename = null;
            mInstanceState.charset = DEF_CHARSET;
            mInstanceState.linebreak = DEF_LINEBREAK;
            mEditor.setText("");
            mEditor.setChanged(false);
            mEditor.setSelection(0,0);

            mLlSearch.setVisibility(View.GONE);
            mLlReplace.setVisibility(View.GONE);

            mEdtSearchWord.setText("");
            mBtnForward.setEnabled(false);
            mBtnBackward.setEnabled(false);
            mChkReplace.setChecked(false);
            mEdtReplaceWord.setText("");
            mBtnSkip.setEnabled(false);
            mBtnReplaceAll.setEnabled(false);


        }
    };

    private Runnable mProcReopen =  new Runnable() {
        public void run() {
            mTask = new TextLoadTask( Main.this , Main.this );
            mTask.execute(mNewFilename);
            mNewFilename = null;
        }
    };




    abstract class PostProcess implements Runnable , DialogInterface.OnClickListener {
    }

    private PostProcess mProcHistory =  new PostProcess() {
        // get history
        class FileInfo{
            String path;
            long lastaccess;
        }

        ArrayList<FileInfo> fl = new ArrayList<FileInfo>();

        public void run() {
            SharedPreferences sp = getSharedPreferences(PREF_HISTORY,PREF_MODE);

            fl.removeAll(fl);
            Map<String,?> map = sp.getAll();

            // enumerate all of history
            for( Entry<String,?> entry : map.entrySet() ){
                Object val = entry.getValue();
                if ( val instanceof String){
                    String[] vals = ((String)val).split(",");
                    if ( vals.length>=3 ){
                        try{
                            FileInfo fi = new FileInfo();
                            fi.path = entry.getKey();
                            fi.lastaccess = Long.parseLong(vals[2]);
                            fl.add(fi);
                        }
                        catch(Exception e)
                        {
                        }
                    }
                }
            }

            if ( fl.size() == 0 ){
                return;
            }

            Collections.sort(fl, new Comparator<FileInfo>(){
                public int compare(FileInfo object1, FileInfo object2) {
                    return (int)(object2.lastaccess - object1.lastaccess);
                }
            });

            int historymax = fl.size();
            if ( historymax > 20 ){
                historymax = 20;
            }
            CharSequence [] items = new CharSequence[historymax];
            int max = fl.size();
            for( int i=0;i<max;i++){
                if ( i< historymax ){
                    File f = new File(fl.get(i).path);
                    items[i] = f.getName();
                }else{
                    // remove a record over 20 counts
                    sp.edit().remove(fl.get(i).path);
                }
            }
            sp.edit().commit();
            new AlertDialog.Builder(Main.this).setTitle(R.string.history).setItems(items, this).show();

        }

        public void onClick(DialogInterface dialog, int which) {
            String path = fl.get(which).path;
            mTask = new TextLoadTask( Main.this,Main.this );
            mTask.execute(path);
        }
    };


    private PostProcess mProcCharSet =  new PostProcess() {
        String[] items;
        public void run() {
            items = getResources().getStringArray(R.array.CharcterSet);

            if ( mInstanceState.charset != null ){
                int max = items.length;
                for(int i=0;i<max;i++){
                    if ( mInstanceState.charset.equalsIgnoreCase(items[i])){
                        items[i] = "*" + items[i];
                    }
                }
            }
            new AlertDialog.Builder(Main.this).setTitle(R.string.charset).setItems(items, this).show();
        }

        public void onClick(DialogInterface dialog, int which) {
            mInstanceState.charset = items[which].replace("*","");
        }
    };

    private PostProcess mProcLineBreak =  new PostProcess() {
        String[] items;

        public void run() {
            items = getResources().getStringArray(R.array.LineBreak);

            int lb = mInstanceState.linebreak;
            items[lb] = "*" + items[lb];

            new AlertDialog.Builder(Main.this).setTitle(R.string.linebreak).setItems(items, this).show();
        }

        public void onClick(DialogInterface dialog, int which) {
            mInstanceState.linebreak = which;
        }
    };

    private Runnable mProcInsert =  new Runnable() {
        public void run() {
            Intent intent = new Intent( "com.adamrocker.android.simeji.ACTION_INTERCEPT" );
            intent.addCategory("com.adamrocker.android.simeji.REPLACE");

            int startsel = mEditor.getSelectionStart();
            int endsel = mEditor.getSelectionEnd();
            Editable text = mEditor.getText();

            String substr = "";
            if ( startsel != endsel ){
                if ( endsel < startsel ){
                    int temp = startsel ;
                    startsel = endsel;
                    endsel = temp;
                }
                substr = text.subSequence(startsel, endsel).toString();
            }
            intent.putExtra("replace_key", substr);

            try{
                Intent pickIntent = new Intent(Main.this,ActivityPicker.class);
                pickIntent.putExtra(Intent.EXTRA_INTENT, intent);
                mReservedIntent = intent;
                mReservedRequestCode = REQUESTCODE_MUSHROOM;
                startActivityForResult(pickIntent,REQUESTCODE_APPCHOOSER);
            }
            catch(Exception e)
            {
            }
        }
    };
    private Runnable mProcSearchByIntent =  new Runnable() {

        public void run() {
            int startsel = mEditor.getSelectionStart();
            int endsel = mEditor.getSelectionEnd();
            Editable text = mEditor.getText();

            String substr = "";
            if ( startsel != endsel ){
                if ( endsel < startsel ){
                    int temp = startsel ;
                    startsel = endsel;
                    endsel = temp;
                }
                substr = text.subSequence(startsel, endsel).toString();
                searchWord( substr );
            }else{
                final EditText mInput;
                final View layout = View.inflate(Main.this, R.layout.input_search_word, null);
                mInput = (EditText) layout.findViewById(R.id.search_word);


                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setTitle(getString(R.string.menu_search_byintent));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.label_ok),new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String substr = mInput.getText().toString();
                        searchWord( substr );
                    }
                });
                builder.setView(layout);

                final AlertDialog dialog = builder.create();

                mInput.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event)
                    {
                        if (keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == KeyEvent.ACTION_UP ) {
                            String substr = mInput.getText().toString();
                            searchWord( substr );
                            dialog.cancel();
                        }
                        return false;
                    }
                });

                dialog.show();

            }
        }

        private void searchWord( String substr ){
            Intent intent = new Intent( Intent.ACTION_SEARCH );
            intent.putExtra(SearchManager.QUERY, substr);

            try{
                Intent pickIntent = new Intent(Main.this,ActivityPicker.class);
                pickIntent.putExtra(Intent.EXTRA_INTENT, intent);
                mReservedIntent = intent;
                mReservedRequestCode = REQUESTCODE_SEARCHBYINTENT;
                startActivityForResult(pickIntent,REQUESTCODE_APPCHOOSER);
            }
            catch(Exception e)
            {
            }
        }
    };


    private Runnable mProcShare =  new Runnable() {
        public void run() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            int startsel = mEditor.getSelectionStart();
            int endsel = mEditor.getSelectionEnd();
            Editable text = mEditor.getText();

            String substr = text.toString();
            if ( startsel != endsel ){
                if ( endsel < startsel ){
                    int temp = startsel ;
                    startsel = endsel;
                    endsel = temp;
                }
                substr = text.subSequence(startsel, endsel).toString();
            }
            intent.putExtra(Intent.EXTRA_TEXT, substr );

            try{
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }
        }
    };

    private Runnable mProcSearch =  new Runnable() {
        public void run() {
            mLlSearch.setVisibility(View.VISIBLE);
            mChkReplace.setChecked(false);
            mEdtSearchWord.requestFocus();
       }
    };

    private Runnable mProcUndo =  new Runnable() {
        public void run() {
            // TODO:

        }
    };


    private void doSearch( String searchword ) {
        if ( mSearchResult != null ){
            mProcOnSearchResult.search();
            return;
        }
        new Search(this, searchword, mEditor.getText(), mSettings.re , mSettings.ignorecase, mProcOnSearchResult);
    }

    abstract class PostSearchProcess implements  OnSearchFinishedListener {
        abstract public void search();
    }

    private PostSearchProcess mProcOnSearchResult =  new PostSearchProcess() {
        @Override
        public void search() {
            int cursor = mEditor.getSelectionStart();

            Record cursorRecord  = new Record();
            cursorRecord.start = cursor;

            int cursorpos = Collections.binarySearch(mSearchResult , cursorRecord , new Comparator<Record>(){
                public int compare(Record object1, Record object2) {
                    return object1.start - object2.start;
                }
            });

            if ( cursorpos >= 0 ){       // on the word

                if ( mSearchForward ){
                    cursorpos ++;
                    if ( cursorpos < mSearchResult.size() ){
                        // found
                        highlight( cursorpos );
                    }
                }else{
                    cursorpos --;
                    if ( cursorpos >= 0) {
                        // found
                        highlight( cursorpos );
                    }
                }

            }else{                      // not on the word
                cursorpos = -1 - cursorpos;
                if ( mSearchForward ){
                    if ( cursorpos < mSearchResult.size() ){
                        // found
                        highlight( cursorpos );
                    }
                }else{
                    cursorpos --;
                    if ( cursorpos >= 0) {
                        // found
                        highlight( cursorpos );
                    }
                }
            }

            Log.e(TAG , "found="+cursorpos);
            for( Record record : mSearchResult ){
                Log.e(TAG , ""+record.start + "," + record.end );
            }
        }
        public void onSearchFinished(ArrayList<Record> data) {
            mSearchResult = data;
            search();
        }

        private void highlight( int pos )
        {
//            Editable text = mEditor.getText();
            Record r = mSearchResult.get(pos);
            mEditor.setSelection(r.start , r.end);
            mEditor.requestFocus();
//            text.setSpan(bgspan, r.start, r.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            text.setSpan(fgspan, r.start, r.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

    };

    private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener =  new OnSharedPreferenceChangeListener(){
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            mSettings = SettingsActivity.readSettings(Main.this);
        }
    };

}