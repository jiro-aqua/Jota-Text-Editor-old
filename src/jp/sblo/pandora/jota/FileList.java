package jp.sblo.pandora.jota;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileList extends ListActivity {

	public static final String INTENT_DIRPATH = "DIRPATH";
	public static final String INTENT_FILENAME = "FILENAME";
	public static final String INTENT_FILEPATH = "FILEPATH";
	public static final String INTENT_MODE = "MODE";
	public static final String MODE_OPEN = "OPEN";
    public static final String MODE_SAVE = "SAVE";
    public static final String MODE_DIR = "DIR";
    public static final String INTENT_TITLE = "TITLE";


	private String mMode=null;
	private String m_strDirPath;
	private List<String> items = null;
    private Button mBtnOK;
    private EditText mEdtFileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);

        mBtnOK = (Button)findViewById(R.id.btnOK);
        mEdtFileName = (EditText)findViewById(R.id.edtFileName);

        mEdtFileName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( MODE_SAVE.equals(mMode) ) {
                    if ( s.toString().indexOf('/')<0 ){
                        String strFilePath;
                        if( m_strDirPath.equals("/")) {
                            strFilePath = "/" + s;
                        } else {
                            strFilePath = m_strDirPath + "/" + s;
                        }

                        File f = new File(strFilePath);
                        mBtnOK.setEnabled( !f.isDirectory() );
                    }else{
                        mBtnOK.setEnabled( false );
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });
        mEdtFileName.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ( (keyCode == KeyEvent.KEYCODE_ENTER  || keyCode == KeyEvent.KEYCODE_DPAD_CENTER )
                        && event.getAction() == KeyEvent.ACTION_UP ) {
                    if ( mBtnOK.isEnabled() ){
                        mBtnOK.performClick();
                    }
                }
                return false;
            }
        });
        mEdtFileName.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus && mEdtFileName.isEnabled() ){
                    String strFileName = mEdtFileName.getText().toString();
                    if ( strFileName.indexOf('/') >= 0 ){
                        mEdtFileName.setText("");
                    }
                }
            }
        });

        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);

        Bundle extras = intent.getExtras();
		if (extras != null) {
		    mMode = extras.getString(INTENT_MODE);
		}

		if ( mMode == null ){
		    Log.e("JotaFileSelector", "No MODE parameter specified");
		    finish();
		    return;
		}

        if( MODE_SAVE.equals(mMode) ) {

            setTitle(R.string.fs_title_save);
            mEdtFileName.setEnabled(true);

        } else if ( MODE_OPEN.equals(mMode)  ){

            setTitle(R.string.fs_title_open);
            mBtnOK.setVisibility(View.GONE);
            mEdtFileName.setEnabled(false);

        } else if ( MODE_DIR.equals(mMode)){

            setTitle(R.string.fs_title_dir);
            mEdtFileName.setEnabled(false);

        }else{
            Log.e("JotaFileSelector", "MODE parameter must be OPEN , SAVE or DIR.");
            finish();
            return;
        }

        mBtnOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();

                String strFileName = mEdtFileName.getText().toString();
                intent.putExtra(INTENT_FILENAME, strFileName );
                intent.putExtra(INTENT_DIRPATH, m_strDirPath );

                String strFilePath;
                if( m_strDirPath.equals("/")) {
                    strFilePath = "/" + strFileName;
                } else {
                    strFilePath = m_strDirPath + "/" + strFileName;
                }
                intent.putExtra(INTENT_FILEPATH, strFilePath );
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        m_strDirPath = "/sdcard";
        fillList();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		String strItem = (String)getListAdapter().getItem(position);

		if( strItem.equals("..") ) {
			// up to parent direcotry
			if( m_strDirPath.lastIndexOf("/") <= 0 ) {
				// root
				m_strDirPath = m_strDirPath.substring(0, m_strDirPath.lastIndexOf("/") + 1 );
			} else {
				// sub directory
				m_strDirPath = m_strDirPath.substring(0, m_strDirPath.lastIndexOf("/"));
			}
			fillList();
		} else if( strItem.substring(strItem.length() - 1 ).equals("/") ) {
			// into directory
			if( m_strDirPath.equals("/") ) {
				// root
				m_strDirPath += strItem;
			} else {
				// sub directory
				m_strDirPath = m_strDirPath + "/" + strItem;
			}
			m_strDirPath = m_strDirPath.substring(0, m_strDirPath.length() - 1 );
			fillList();
		} else {
            // file
		    if ( MODE_OPEN.equals(mMode)) {
    		    Intent intent = getIntent();
                String strFilePath;
                if( m_strDirPath.equals("/")) {
                    strFilePath = "/" + strItem;
                } else {
                    strFilePath = m_strDirPath + "/" + strItem;
                }
                intent.putExtra(INTENT_FILEPATH, strFilePath );
                setResult(RESULT_OK, intent);
                finish();
		    }else if ( MODE_SAVE.equals(mMode)){
    			mEdtFileName.setText(strItem);
		    }else if ( MODE_DIR.equals(mMode)){
		        // nop
		    }
		}

	}

	private void fillList()
	{
		File[] files = new File(m_strDirPath).listFiles();
		if( files == null ) {
			Toast.makeText(FileList.this, getString(R.string.fs_access_denied),
				       Toast.LENGTH_SHORT).show();
			return;
		}

		TextView txtDirName = (TextView)findViewById(R.id.edtFileName);
		txtDirName.setText(m_strDirPath);


		if( items != null ) {
			items.clear();
		}
		items = new ArrayList<String>();

		if( !m_strDirPath.equals("/") ) {
			items.add("..");
		}

		for( File file : files ) {
			if( file.isDirectory() ) {
				items.add(file.getName() + "/" );
			} else {
				items.add(file.getName());
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_row, items);
		setListAdapter(fileList);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // BACK key short press => move to parent directory. if root finish the activity.
        // BACK key long  press => finish the activity.
        if ( keyCode == KeyEvent.KEYCODE_BACK ){
            if (event.getRepeatCount() == 0 && !m_strDirPath.equals("/") ) {
                if( m_strDirPath.lastIndexOf("/") <= 0 ) {
                    m_strDirPath = m_strDirPath.substring(0, m_strDirPath.lastIndexOf("/") + 1 );
                } else {
                    m_strDirPath = m_strDirPath.substring(0, m_strDirPath.lastIndexOf("/"));
                }
                fillList();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}