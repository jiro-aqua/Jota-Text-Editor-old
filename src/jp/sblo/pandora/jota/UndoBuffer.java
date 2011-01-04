package jp.sblo.pandora.jota;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class UndoBuffer implements Parcelable {

    static class TextChange {
        int start;
        CharSequence oldtext;
        CharSequence newtext;
    };
    ArrayList<TextChange> mBuffer;


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        for( TextChange item : mBuffer ){
            out.writeInt(item.start);
            String [] strs = new String[]{item.oldtext.toString(),item.newtext.toString()};
            out.writeStringArray(strs);
        }
    }

    public static final Parcelable.Creator<UndoBuffer> CREATOR
            = new Parcelable.Creator<UndoBuffer>() {
        public UndoBuffer createFromParcel(Parcel in) {
            return new UndoBuffer(in);
        }

        public UndoBuffer[] newArray(int size) {
            return new UndoBuffer[size];
        }
    };

    private UndoBuffer(Parcel in) {
        this();
        while( in.dataAvail() > 0 ){
            TextChange item = new TextChange();
            item.start = in.readInt();
            String[] strs = in.readStringArray();
            item.oldtext = strs[0];
            item.newtext = strs[1];
            mBuffer.add(item);
        }
    }

    public UndoBuffer() {
        mBuffer = new ArrayList<TextChange>();

    }

    public TextChange pop()
    {
        int size = mBuffer.size();
        if ( size > 0 ){
            TextChange item = mBuffer.get(size-1);
            mBuffer.remove(size-1);
            return item;
        }else{
            return null;
        }
    }

    public void push( TextChange item )
    {
        mBuffer.add(item);
    }

    public void removeAll()
    {
        mBuffer.removeAll(mBuffer);
    }
}
