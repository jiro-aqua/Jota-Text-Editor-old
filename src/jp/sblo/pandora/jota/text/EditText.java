package jp.sblo.pandora.jota.text;

import jp.sblo.pandora.jota.R;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

public class EditText extends TextView{


    private JotaTextWatcher mTextWatcher;

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
        Log.e( "keycode=","keycode="+keyCode );

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
        Log.e( "dispatch=","keycode="+event.getKeyCode() );

        int keycode = event.getKeyCode();
        // ALT + KEYDOWN
        if ( event.isAltPressed() && event.getAction() == KeyEvent.ACTION_DOWN ){
            switch(keycode){
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_X:
            case KeyEvent.KEYCODE_C:
            case KeyEvent.KEYCODE_V:
                return onKeyShortcut( keycode , event );
            }
        }
        return super.dispatchKeyEventPreIme(event);
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
    }

}
