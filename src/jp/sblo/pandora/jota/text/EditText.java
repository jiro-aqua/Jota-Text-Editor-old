package jp.sblo.pandora.jota.text;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import jp.sblo.pandora.jota.R;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class EditText extends TextView{

    public final static int FUNCTION_NONE=-1;
    public final static int FUNCTION_SELECT_ALL=0;
    public final static int FUNCTION_UNDO=1;
    public final static int FUNCTION_COPY=2;
    public final static int FUNCTION_CUT=3;
    public final static int FUNCTION_PASTE=4;
    public final static int FUNCTION_DIRECTINTENT=5;
    public final static int FUNCTION_SAVE=6;

    private JotaTextWatcher mTextWatcher;
    private WeakReference<ShortcutListener> mShortcutListener;
    private int mShortcutMetaKey = 0;
    private HashMap<Integer,ShortcutSettings> mShortcuts;;

    public EditText(Context context) {
        this(context, null);
        init(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        init(context);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        setFocusable(true);
        setFocusableInTouchMode(true);

        setFastScrollEnabled(true);

        // change width of the caret
        float caretThick = context.getResources().getDimension(R.dimen.caret_thick);
        setCaretThick( caretThick );

        // set my Editable
        setEditableFactory( JotaEditableFactory.getInstance() );

    }


    @Override
    protected boolean getDefaultEditable() {
        return true;
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ArrowKeyMovementMethod.getInstance();
    }

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }

    /**
     * Convenience for {@link Selection#setSelection(Spannable, int, int)}.
     */
    public void setSelection(int start, int stop) {
        Selection.setSelection(getText(), start, stop);
    }

    /**
     * Convenience for {@link Selection#setSelection(Spannable, int)}.
     */
    public void setSelection(int index) {
        Selection.setSelection(getText(), index);
    }

    /**
     * Convenience for {@link Selection#selectAll}.
     */
    public void selectAll() {
        Selection.selectAll(getText());
    }

    /**
     * Convenience for {@link Selection#extendSelection}.
     */
    public void extendSelection(int index) {
        Selection.extendSelection(getText(), index);
    }

    @Override
    public void setEllipsize(TextUtils.TruncateAt ellipsis) {
        if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
            throw new IllegalArgumentException("EditText cannot use the ellipsize mode "
                    + "TextUtils.TruncateAt.MARQUEE");
        }
        super.setEllipsize(ellipsis);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e( "keycode=","keycode="+keyCode );

        int keycode = event.getKeyCode();

        if ( event.getAction() == KeyEvent.ACTION_DOWN ){
            switch(keycode){
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    return centerCursor();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
//        Log.e( "dispatch=","keycode="+event.getKeyCode() );

        Editable cs = getText();

        int keycode = event.getKeyCode();
        // ALT + KEYDOWN
        int meta = (int)event.getMetaState();
//        int altstate = TextKeyListener.getMetaState(cs,KeyEvent.META_ALT_ON);
        boolean alt = (meta & mShortcutMetaKey)!=0 ; // || (altstate!=0);      // one of meta keies is pressed , or , Alt key is locked

        if ( alt && event.getAction() == KeyEvent.ACTION_DOWN ){
            if (doShortcut(keycode, event)){
//                if ( altstate == 1 ){
//                    TextKeyListener.clearMetaKeyState(cs,KeyEvent.META_ALT_ON);
//                }
                return true;
            }

        }
        return super.dispatchKeyEventPreIme(event);
    }

    public boolean doShortcut(int keycode, KeyEvent event) {

        ShortcutSettings ss = mShortcuts.get(keycode);

        if (ss != null && ss.enabled) {
            ShortcutListener sl = mShortcutListener.get();

            switch (ss.function) {
                case FUNCTION_SELECT_ALL:
                    return onKeyShortcut(KeyEvent.KEYCODE_A, event);

                case FUNCTION_CUT:
                    return onKeyShortcut(KeyEvent.KEYCODE_X, event);

                case FUNCTION_COPY:
                    return onKeyShortcut(KeyEvent.KEYCODE_C, event);

                case FUNCTION_UNDO:
                    return onKeyShortcut(KeyEvent.KEYCODE_Z, event);

                case FUNCTION_PASTE:
                    return onKeyShortcut(KeyEvent.KEYCODE_V, event);

                case FUNCTION_SAVE:
                    if (sl != null) {
                        return sl.onCommand(KeyEvent.KEYCODE_S);
                    }
                    break;

                case FUNCTION_DIRECTINTENT:
                    if (sl != null) {
                        return sl.onCommand(KeyEvent.KEYCODE_D);
                    }
                    break;

                case FUNCTION_NONE:
                    return false;
            }
        }
        return false;
    }

    public void setDocumentChangedListener( JotaDocumentWatcher watcher )
    {
        mTextWatcher = new JotaTextWatcher( watcher );
        // set text watcher
        addTextChangedListener(mTextWatcher);
    }
    public boolean isChanged()
    {
        if ( mTextWatcher != null ){
            return mTextWatcher.isChanged();
        }else{
            return false;
        }
    }
    public void setChanged( boolean changed ){
        if ( mTextWatcher != null ){
            mTextWatcher.setChanged( changed );
        }
        super.setChanged( changed );
    }

    public void setShortcutListener( ShortcutListener sl )
    {
        mShortcutListener = new WeakReference<ShortcutListener>(sl);
    }

    public interface ShortcutListener {
        boolean onCommand(int keycode);
    }

    public void setShortcutMetaKey(int metakey) {
        this.mShortcutMetaKey = metakey;
    }


    public static class ShortcutSettings {
        boolean enabled;
        int function;

        public ShortcutSettings( boolean e , int f){
            enabled=e;
            function=f;
        }
    };

    public void setShortcutSettings( HashMap<Integer,ShortcutSettings> s )
    {
        mShortcuts = s;
    }
}
