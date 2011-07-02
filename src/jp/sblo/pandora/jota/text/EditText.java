package jp.sblo.pandora.jota.text;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;

import jp.sblo.pandora.jota.R;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class EditText extends TextView{

    public final static int FUNCTION_NONE=-1;
    public final static int FUNCTION_SELECT_ALL=0;
    public final static int FUNCTION_UNDO=1;
    public final static int FUNCTION_COPY=2;
    public final static int FUNCTION_CUT=3;
    public final static int FUNCTION_PASTE=4;
    public final static int FUNCTION_DIRECTINTENT=5;
    public final static int FUNCTION_SAVE=6;
    public final static int FUNCTION_ENTER=7;
    public final static int FUNCTION_TAB=8;
    public final static int FUNCTION_DEL=9;
    public final static int FUNCTION_CENTERING=10;
    public final static int FUNCTION_SEARCH=11;
    public final static int FUNCTION_OPEN=12;
    public final static int FUNCTION_NEWFILE=13;
    public final static int FUNCTION_REDO=14;
    public final static int FUNCTION_CONTEXTMENU=15;
    public final static int FUNCTION_JUMP=16;
    public final static int FUNCTION_FORWARD_DEL=17;


    private JotaTextWatcher mTextWatcher;
    private WeakReference<ShortcutListener> mShortcutListener;
    private int mShortcutMetaKey = 0;
    private HashMap<Integer,ShortcutSettings> mShortcuts;;
    private int mDpadCenterFunction = FUNCTION_CENTERING;


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
        setCaretThick( context.getResources().getDimension(R.dimen.caret_thick) );

        // set my Editable
        setEditableFactory( JotaEditableFactory.getInstance() );

        // set IME options
        setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_FULLSCREEN|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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
                    return doFunction(mDpadCenterFunction);
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
//        Log.e( "dispatch=","keycode="+event.getKeyCode() );

        int keycode = event.getKeyCode();
        // ALT + KEYDOWN
        int meta = (int)event.getMetaState();
//        int altstate = TextKeyListener.getMetaState(cs,KeyEvent.META_ALT_ON);
        boolean alt = (meta & mShortcutMetaKey)!=0 ; // || (altstate!=0);      // one of meta keies is pressed , or , Alt key is locked

        if ( alt && event.getAction() == KeyEvent.ACTION_DOWN ){
            if (doShortcut(keycode)){
//                if ( altstate == 1 ){
//                    TextKeyListener.clearMetaKeyState(cs,KeyEvent.META_ALT_ON);
//                }
                if ( (meta & KeyEvent.META_ALT_LEFT_ON )!=0 || (meta & KeyEvent.META_ALT_RIGHT_ON )!=0 )
                {
                    InputMethodManager imm = InputMethodManager.peekInstance();
                    if (imm != null){
                        // for IS01 w/iWnn
                        // iWnn eats ALT key so we needs to reset ime.
                        try {
                            Class<?> c = imm.getClass();
                            Field f = c.getDeclaredField("mCurId");
                            f.setAccessible(true);
                            String immId = (String)f.get(imm);
                            if ( "jp.co.omronsoft.iwnnime/.iWnnIME".equals(immId) ){
                                imm.restartInput(this);
                            }
                        } catch (Exception e) {
                        }
                    }
                    TextKeyListener.resetMetaState((Spannable)getEditableText());
                }
                return true;
            }

        }
        return super.dispatchKeyEventPreIme(event);
    }

    public boolean doFunction( int function ){
        boolean result = doFunction_( function );
        return result;
    }


    public boolean doFunction_( int function ){
        ShortcutListener sl = mShortcutListener.get();

        switch ( function) {
            case FUNCTION_SELECT_ALL:
                return onKeyShortcut(KeyEvent.KEYCODE_A, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_A));

            case FUNCTION_CUT:
                return onKeyShortcut(KeyEvent.KEYCODE_X, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_X));

            case FUNCTION_COPY:
                return onKeyShortcut(KeyEvent.KEYCODE_C, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_C));

            case FUNCTION_UNDO:
                return onKeyShortcut(KeyEvent.KEYCODE_Z, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_Z));

            case FUNCTION_REDO:
                return onKeyShortcut(KeyEvent.KEYCODE_Y, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_Y));

            case FUNCTION_PASTE:
                return onKeyShortcut(KeyEvent.KEYCODE_V, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_V));


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

            case FUNCTION_ENTER:
                return onKeyDown(KeyEvent.KEYCODE_ENTER ,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
            case FUNCTION_TAB:
                return onKeyDown(KeyEvent.KEYCODE_TAB ,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_TAB));
            case FUNCTION_DEL:
                return onKeyDown(KeyEvent.KEYCODE_DEL ,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL));
            case FUNCTION_FORWARD_DEL:
            {
                int key = JotaTextKeyListener.getForwardDelKeycode();
                return onKeyDown( key ,new KeyEvent(KeyEvent.ACTION_DOWN,key));
            }
            case FUNCTION_CENTERING:
                return centerCursor();

            case FUNCTION_OPEN:
                if (sl != null) {
                    return sl.onCommand(KeyEvent.KEYCODE_O);
                }
                break;

            case FUNCTION_NEWFILE:
                if (sl != null) {
                    return sl.onCommand(KeyEvent.KEYCODE_N);
                }
                break;

            case FUNCTION_SEARCH:
                if (sl != null) {
                    return sl.onCommand(KeyEvent.KEYCODE_F);
                }
                break;

            case FUNCTION_JUMP:
                if (sl != null) {
                    return sl.onCommand(KeyEvent.KEYCODE_J);
                }
                break;

            case FUNCTION_CONTEXTMENU:
                showContextMenu();
                return true;
            case FUNCTION_NONE:
                return false;
        }
        return false;
    }


    public boolean doShortcut(int keycode) {

        ShortcutSettings ss = mShortcuts.get(keycode);

        if (ss != null && ss.enabled) {
            return doFunction( ss.function );
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

    public void setUseVolumeKey( boolean useVolumeKey )
    {
        ArrowKeyMovementMethod.setUseVolumeKey(useVolumeKey);
    }

    public void setDpadCenterFunction( int function )
    {
        mDpadCenterFunction = function;
    }

    public void setAutoIndent( boolean autoIndent )
    {
        JotaTextKeyListener.setAutoIndent(autoIndent);
    }
    public void setForwardDelKeycode( int keycode )
    {
        JotaTextKeyListener.setForwardDelKeycode(keycode);
    }

}
