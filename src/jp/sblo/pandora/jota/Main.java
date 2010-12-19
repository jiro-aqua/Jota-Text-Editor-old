package jp.sblo.pandora.jota;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.sblo.pandora.jota.text.EditText;
import jp.sblo.pandora.jota.text.JotaDocumentWatcher;
import jp.sblo.pandora.jota.text.style.BackgroundColorSpan;

import org.mozilla.universalchardet.UniversalDetector;
import org.mozilla.universalchardet.UniversalDetector.DetectorException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;

public class Main extends Activity implements JotaDocumentWatcher {
    static private final int CR   = 0;
    static private final int LF   = 1;
    static private final int CRLF = 2;
    private static final String TAG = "JotaTextEditor";

    private static final String PREF_HISTORY = "history.xml";


    private EditText mEditor;
    private TextLoadTask mTask;
    private String mSearchWord;
    private int mLine;
    private int mSelStart;
    private int mSelEnd;


    private InstanceState   mInstanceState = new InstanceState();

    class InstanceState {
        String  filename;
        String  charset;
        String  text;
        int     linebreak;
        int     selstart;
        int     selend;
        boolean changed;
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textviewer);

        mEditor = (EditText)findViewById(R.id.textedit);
        mEditor.setDocumentChangedListener(Main.this);
        mEditor.setChanged(false);

        if (savedInstanceState==null){
            Intent it = getIntent();
            if (it!=null && Intent.ACTION_VIEW.equals(it.getAction())){
                Uri data = it.getData();
                String path = Uri.decode( data.getSchemeSpecificPart().substring(2) );      // skip "//"

                mSearchWord = null;
                mLine = -1;

                Bundle extra = it.getExtras();
                if ( extra!=null ){
                    mSearchWord = extra.getString("query");
                    mLine = extra.getInt("line");
                }

                SharedPreferences sp = getSharedPreferences(PREF_HISTORY,MODE_PRIVATE);
                String sel = sp.getString(path, "-1,-1");

                mSelStart = -1;
                mSelEnd = -1;
                if ( sel != null ){
                    String [] sels = sel.split(",");
                    if ( sels.length == 2 ){
                        try{
                            mSelStart = Integer.parseInt(sels[0]);
                            mSelEnd = Integer.parseInt(sels[1]);
                        }catch(Exception e){
                            mSelStart = -1;
                            mSelEnd = -1;
                        }
                    }
                }

                mTask = new TextLoadTask();
                mTask.execute(path);
            }
        }else{
            mInstanceState.filename = savedInstanceState.getString("filename" );
            mInstanceState.charset = savedInstanceState.getString("charset"  );
            mInstanceState.text = savedInstanceState.getString("text"  );
            mInstanceState.linebreak = savedInstanceState.getInt("linebreak" );
            mInstanceState.selstart = savedInstanceState.getInt("selstart"  );
            mInstanceState.selend = savedInstanceState.getInt("selend" );
            mInstanceState.changed = savedInstanceState.getBoolean("changed" );

            mEditor.setText(mInstanceState.text);
            mEditor.setSelection(mInstanceState.selstart, mInstanceState.selend);
            mEditor.setChanged(mInstanceState.changed);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        mInstanceState.text = mEditor.getText().toString();
        mInstanceState.selstart = mEditor.getSelectionStart();
        mInstanceState.selend = mEditor.getSelectionEnd();

        outState.putString("filename" , mInstanceState.filename );
        outState.putString("charset" , mInstanceState.charset );
        outState.putString("text" , mInstanceState.text );
        outState.putInt("linebreak" , mInstanceState.linebreak );
        outState.putInt("selstart" , mInstanceState.selstart );
        outState.putInt("selend" , mInstanceState.selend );
        outState.putBoolean("changed" , mInstanceState.changed );

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();

        int selstart = mEditor.getSelectionStart();
        int selend = mEditor.getSelectionEnd();

        SharedPreferences sp = getSharedPreferences(PREF_HISTORY,MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(mInstanceState.filename, String.format("%d,%d", selstart , selend));
        editor.commit();
    }

    class TextLoadTask extends AsyncTask<String, Integer, String>{
        private int mLineToChar=-1;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder result=new StringBuilder();
            File f = new File(params[0]);

            mInstanceState.filename = params[0];

            if ( f.exists() ){

                InputStream is;
                try {
                    is = new BufferedInputStream( new FileInputStream( f ) , 65536 );
                    is.mark(65536);

                    // preread leading 64KB
                    int nread;
                    byte[] buff = new byte[64*1024];
                    nread = is.read(buff);

                    if ( nread == 0 ){
                        return "";
                    }

                    // Detect charset
                    UniversalDetector detector;
                    String encode = null;
                    try {
                        detector = new UniversalDetector();
                        detector.handleData(buff, 0, nread);
                        detector.dataEnd();
                        encode = detector.getCharset();
                        detector.destroy();
                    } catch (DetectorException e1) {
                    }
                    is.reset();

                    // detect linbreak code
                    Charset charset = null;
                    if ( encode != null && encode.length() > 0 ){
                        charset = Charset.forName(encode);
                    }else{
                        charset = Charset.forName("UTF-8");
                    }

                    byte[] cr = new byte[] { '\r' , };
                    byte[] lf = new byte[] { '\n' , };
                    if ( charset != null ){
                        ByteBuffer bb;
                        bb = charset.encode("\r");
                        cr = new byte[bb.limit()];
                        bb.get(cr);
                        bb = charset.encode("\n");
                        lf = new byte[bb.limit()];
                        bb.get(lf);
                    }

                    int linebreak = LF;
                    if ( cr.length == 1 ){
                        for( int i=0;i<nread-1 ;i++ ){
                            if ( buff[i] == lf[0] ){
                                linebreak = LF;
                                break;
                            } else if ( buff[i] == cr[0] ){
                                if ( buff[i+1] == lf[0] ){
                                    linebreak = CRLF;
                                }else{
                                    linebreak = CR;
                                }
                                break;
                            }
                       }
                    }else{      // cr.length == 2 // we dont think in the case cr.length>2
                        for( int i=0;i<nread-2 ;i+=2 ){
                            if ( buff[i] == lf[0] && buff[i+1]==lf[1] ){
                                linebreak = LF;
                                break;
                            } else if ( buff[i] == cr[0] && buff[i+1]==cr[1] ){
                                if ( buff[i+2] == lf[0] && buff[i+3]==lf[1] ){
                                    linebreak = CRLF;
                                }else{
                                    linebreak = CR;
                                }
                                break;
                            }
                       }
                    }
                    if ( encode != null ){
                        Log.e( TAG , "CharSet="+encode+"Linebreak=" + new String[]{"CR","LF","CRLF"}[linebreak]);
                    }else{
                        Log.e( TAG , "CharSet="+"--"+"Linebreak=" + new String[]{"CR","LF","CRLF"}[linebreak]);
                    }
                    mInstanceState.charset = encode;
                    mInstanceState.linebreak = linebreak;

                    BufferedReader br=null;
                    try {
                        if ( encode != null ){
                            br = new BufferedReader( new InputStreamReader( is , encode ) , 8192 );
                        }else{
                            br = new BufferedReader( new InputStreamReader( is ) , 8192 );
                        }

                        int line=0;
                        String text;
                        while(  ( text = br.readLine() )!=null ){
                            line++;
                            if ( line== mLine ){
                                mLineToChar = result.length();
                            }
                            result.append( text );
                            result.append( '\n' );
                        }
                        br.close();
                        is.close();
                        return result.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if ( result != null ){
                SpannableString ss = new SpannableString( result );

                if ( mSearchWord != null && mSearchWord.length() > 0 ){
                    Pattern p = Pattern.compile(mSearchWord);

                    Matcher m;

                    m = p.matcher(result);

                    int start = 0;
                    int end = 0;
                    while ( m.find(start) ){
                        start = m.start();
                        end = m.end();

                        BackgroundColorSpan span = new BackgroundColorSpan( 0xFF00FFFF );
                        ss.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        start = end;
                    }
                }
                mEditor.setText(ss);
                mEditor.setChanged(false);
                if ( mLineToChar > 0 ){
                    mEditor.setSelection(mLineToChar);
                }

                if ( mLine != -1 ){
                    final Rect rect = new Rect();
                    mEditor.getLineBounds(mLine, rect);

//                mScrollView.post(new Runnable() {
//                    public void run() {
//                        mScrollView.scrollTo(0 , rect.bottom );
//                    }
//                });
                }else{
                    if ( mSelStart >=0 && mSelEnd >= 0 ){
                        int len = mEditor.length();
                        if ( mSelStart >= len ){
                            mSelStart = len-1;
                        }
                        if ( mSelEnd >= len ){
                            mSelEnd = len-1;
                        }
                        mEditor.setSelection(mSelStart,mSelEnd);
                        mEditor.centerCursor();
                    }
                }
            }
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
            if ( mEditor.isChanged() ){
                new AlertDialog.Builder(this)
                .setTitle(R.string.confirmation)
                .setMessage( getString(R.string.confirmation_message, mInstanceState.filename) )
                .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String filename = mInstanceState.filename;
                        String charset = mInstanceState.charset;
                        int linebreak = mInstanceState.linebreak;
                        String text = mEditor.getText().toString();
                        String lb = "\n";
                        if (linebreak == CR) {
                            lb = "'\r";
                        } else if (linebreak == CRLF) {
                            lb = "\r\n";
                        }

                        new TextSaveTask( null , new Runnable(){
                            public void run()
                            {
                                finish();
                            }
                        })
                        .execute(filename , charset , lb , text );
                    }
                })
                .setNeutralButton(R.string.label_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}