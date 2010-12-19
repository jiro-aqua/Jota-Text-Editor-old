package jp.sblo.pandora.jota;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import android.os.AsyncTask;
import android.util.Log;

public     class TextSaveTask extends AsyncTask<String, Integer, String>{

    Runnable mPreProc=null;
    Runnable mPostProc=null;

    public TextSaveTask( Runnable preProc , Runnable postProc)
    {
        mPreProc = preProc;
        mPostProc = postProc;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if ( mPreProc!= null ){
            mPreProc.run();
        }
    }
    @Override
    protected String doInBackground(String... params)
    {
        String filename = params[0] ;
        String charset = params[1] ;
        String lb = params[2] ;
        String text = params[3];

        File f = new File(filename);

        if ( f.exists() ){
            File backup = new File( filename + "~" );
            if ( backup.exists()) {
                backup.delete();
            }
            f.renameTo(backup);
        }

        try{
            BufferedWriter bw=null;
            bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( f ) , Charset.forName(charset) ) , 65536 );

            int pos0 = 0;
            int len = text.length();
            while( pos0<len ){
                int pos1 = text.indexOf('\n',pos0) ;
                if ( pos1 ==  -1 ){
                    pos1 = len;
                }
                if ( pos0!=pos1 ){
                    bw.write(text, pos0, pos1-pos0);
                }
                bw.write(lb);
                pos0 = pos1 + 1;
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if ( mPostProc!= null ){
            mPostProc.run();
        }
    }
}
