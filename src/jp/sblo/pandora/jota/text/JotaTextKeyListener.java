package jp.sblo.pandora.jota.text;

import android.text.Editable;
import android.text.Selection;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;

public class JotaTextKeyListener extends TextKeyListener {
    private static TextKeyListener[] sInstance =
        new TextKeyListener[Capitalize.values().length * 2];

    private static boolean sAutoIndent=false;


    public JotaTextKeyListener(Capitalize cap, boolean autotext) {
        super(cap, autotext);
    }

    public static TextKeyListener getInstance(boolean autotext, Capitalize cap) {
        int off = cap.ordinal() * 2 + (autotext ? 1 : 0);

        if (sInstance[off] == null) {
            sInstance[off] = new JotaTextKeyListener(cap, autotext);
        }

        return sInstance[off];
    }

    @Override
    public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(view, content, keyCode, event);

        // auto indent
        if ( sAutoIndent && keyCode == KeyEvent.KEYCODE_ENTER ){

            int a = Selection.getSelectionStart(content);
            int b = Selection.getSelectionEnd(content);
            if ( a == b ){

                // search head of previous line
                int prev = a-2;
                while( prev >=0 && content.charAt(prev)!='\n' ){
                    prev--;
                }
                prev ++;
                int pos = prev;
                while(  content.charAt(pos)==' ' || content.charAt(pos)=='\t' || content.charAt(pos)=='\u3000'){
                    pos++;
                }
                int len = pos-prev;
                if ( len > 0  ){
                    char [] dest = new char[len];
                    content.getChars(prev, pos, dest, 0);

                    content.replace(a,b, new String(dest) );
                    Selection.setSelection(content, a+len);
                }
            }
        }
        return result;
    }
    static public void setAutoIndent( boolean autoIndent )
    {
        sAutoIndent = autoIndent;
    }

}
