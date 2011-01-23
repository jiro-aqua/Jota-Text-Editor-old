package jp.sblo.pandora.jota;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import jp.sblo.pandora.jota.text.SpannableStringBuilder;

import org.mozilla.universalchardet.UniversalDetector;
import org.mozilla.universalchardet.UniversalDetector.DetectorException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public     class TextLoadTask extends AsyncTask<String, Integer, SpannableStringBuilder>{

    OnFileLoadListener mFileLoadListener=null;
//    private int mLineToChar=-1;

    private String mFilename;
    private String mCharset;
    private int mLinebreak;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;

    public interface OnFileLoadListener
    {
        void onPreFileLoad();
        void onFileLoaded( SpannableStringBuilder result , String filename , String charset , int linebreak );
    }


    public TextLoadTask( Activity activity , OnFileLoadListener postProc)
    {
        mFileLoadListener = postProc;
        mActivity = activity;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if ( mFileLoadListener!= null ){
            mFileLoadListener.onPreFileLoad();
        }
        mProgressDialog = new ProgressDialog(mActivity);
//        mProgressDialog.setTitle(R.string.spinner_message);
        mProgressDialog.setMessage(mActivity.getString(R.string.spinner_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mActivity = null;
    }
    @Override
    protected SpannableStringBuilder doInBackground(String... params)
    {
        SpannableStringBuilder result=new SpannableStringBuilder();
        File f = new File(params[0]);

        mFilename = params[0];

        if ( f.exists() ){

            InputStream is;
            try {
                is = new BufferedInputStream( new FileInputStream( f ) , 65536 );
                is.mark(65536);

                // preread leading 64KB
                int nread;
                byte[] buff = new byte[64*1024];
                nread = is.read(buff);

                if ( nread <= 0 ){
                    return new SpannableStringBuilder("");
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

                int linebreak = LineBreak.LF;
                if ( cr.length == 1 ){
                    for( int i=0;i<nread-1 ;i++ ){
                        if ( buff[i] == lf[0] ){
                            linebreak = LineBreak.LF;
                            break;
                        } else if ( buff[i] == cr[0] ){
                            if ( buff[i+1] == lf[0] ){
                                linebreak = LineBreak.CRLF;
                            }else{
                                linebreak = LineBreak.CR;
                            }
                            break;
                        }
                   }
                }else{      // cr.length == 2 // we dont think in the case cr.length>2
                    for( int i=0;i<nread-2 ;i+=2 ){
                        if ( buff[i] == lf[0] && buff[i+1]==lf[1] ){
                            linebreak = LineBreak.LF;
                            break;
                        } else if ( buff[i] == cr[0] && buff[i+1]==cr[1] ){
                            if ( buff[i+2] == lf[0] && buff[i+3]==lf[1] ){
                                linebreak = LineBreak.CRLF;
                            }else{
                                linebreak = LineBreak.CR;
                            }
                            break;
                        }
                   }
                }
//                if ( encode != null ){
//                    Log.e( TAG , "CharSet="+encode+"Linebreak=" + new String[]{"CR","LF","CRLF"}[linebreak]);
//                }else{
//                    Log.e( TAG , "CharSet="+"--"+"Linebreak=" + new String[]{"CR","LF","CRLF"}[linebreak]);
//                }
                if ( encode == null ){
                    encode = "utf-8";
                }
                mCharset = encode;
                mLinebreak = linebreak;

                BufferedReader br=null;
                try {
                    br = new BufferedReader( new InputStreamReader( is , encode ) , 8192 );

                    int line=0;
                    String text;
                    while(  ( text = br.readLine() )!=null ){
                        line++;
//                        if ( line== mLine ){
//                            mLineToChar = result.length();
//                        }
                        result.append( text );
                        result.append( '\n' );
                    }
                    br.close();
                    is.close();
                    return result;
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
    protected void onPostExecute(SpannableStringBuilder result)
    {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        if ( mFileLoadListener!= null ){
            mFileLoadListener.onFileLoaded( result, mFilename, mCharset, mLinebreak);
        }
    }
}

//class TextLoadTask extends AsyncTask<String, Integer, String>{
//
//    @Override
//    protected String doInBackground(String... params)
//    {
//    }
//
//    @Override
//    protected void onPostExecute(String result)
//    {
//        if ( result != null ){
//            SpannableString ss = new SpannableString( result );
//
//            if ( mSearchWord != null && mSearchWord.length() > 0 ){
//                Pattern p = Pattern.compile(mSearchWord);
//
//                Matcher m;
//
//                m = p.matcher(result);
//
//                int start = 0;
//                int end = 0;
//                while ( m.find(start) ){
//                    start = m.start();
//                    end = m.end();
//
//                    BackgroundColorSpan span = new BackgroundColorSpan( 0xFF00FFFF );
//                    ss.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                    start = end;
//                }
//            }
//            mEditor.setText(ss);
//            mEditor.setChanged(false);
//            if ( mLineToChar > 0 ){
//                mEditor.setSelection(mLineToChar);
//            }
//
//            if ( mLine != -1 ){
//                final Rect rect = new Rect();
//                mEditor.getLineBounds(mLine, rect);
//
////            mScrollView.post(new Runnable() {
////                public void run() {
////                    mScrollView.scrollTo(0 , rect.bottom );
////                }
////            });
//            }else{
//                if ( mSelStart >=0 && mSelEnd >= 0 ){
//                    int len = mEditor.length();
//                    if ( mSelStart >= len ){
//                        mSelStart = len-1;
//                    }
//                    if ( mSelEnd >= len ){
//                        mSelEnd = len-1;
//                    }
//                    mEditor.setSelection(mSelStart,mSelEnd);
//                    mEditor.centerCursor();
//                }
//            }
//        }
//    }
//}

