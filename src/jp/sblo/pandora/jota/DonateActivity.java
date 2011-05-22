package jp.sblo.pandora.jota;

import java.net.URLEncoder;

import jp.sblo.pandora.billing.BillingService;
import jp.sblo.pandora.billing.Consts;
import jp.sblo.pandora.billing.PurchaseObserver;
import jp.sblo.pandora.billing.ResponseHandler;
import jp.sblo.pandora.billing.BillingService.RequestPurchase;
import jp.sblo.pandora.billing.BillingService.RestoreTransactions;
import jp.sblo.pandora.billing.Consts.PurchaseState;
import jp.sblo.pandora.billing.Consts.ResponseCode;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DonateActivity extends AboutActivity  {

    private static final String TAG = "Donate";

    public static final String DONATION_COUNTER = "donationcounter";
    private DonatePurchaseObserver mDonatePurchaseObserver;
    private Handler mHandler;

    private BillingService mBillingService;
//    private Button mBuyButton;

    private static final int DIALOG_CANNOT_CONNECT_ID = 1;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;
    private ProgressDialog mProgressDialog;

    /** An array of product list entries for the products that can be purchased. */
    private static final String [] CATALOG = new String[] {
        "jotatexteditordonation",
        "android.test.purchased",
        "android.test.canceled",
        "android.test.refunded",
        "android.test.item_unavailable",
    };

    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class DonatePurchaseObserver extends PurchaseObserver {
        public DonatePurchaseObserver(Handler handler) {
            super(DonateActivity.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
            if (Consts.DEBUG) {
                Log.i(TAG, "supported: " + supported);
            }
            if (supported) {
//                mBuyButton.setEnabled(true);
            } else {
                showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
            }
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                final String orderId, long purchaseTime, String developerPayload) {
            if (Consts.DEBUG) {
                Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            }
            try{
                mProgressDialog.dismiss();
            }catch(Exception e){}
            mProgressDialog = null;

            if (purchaseState == PurchaseState.PURCHASED) {
                // purchased
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DonateActivity.this);
                int count = prefs.getInt(DONATION_COUNTER,0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt(DONATION_COUNTER, count+1);
                edit.commit();

                View view = getLayoutInflater().inflate(R.layout.subscribe, null);
                final EditText edtnickname = (EditText)view.findViewById(R.id.nickname);

                new AlertDialog.Builder(DonateActivity.this)
                .setView( view )
                .setIcon(R.drawable.icon)
                .setTitle(R.string.app_name)
//                .setMessage(R.string.label_thankyou)
                .setCancelable(true)
                .setPositiveButton(R.string.label_subscribe, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = edtnickname.getText().toString();

                        if ( nickname.length()>0 ){

                            Intent it = new Intent();
                            it.setAction(Intent.ACTION_SENDTO );
                            int mill = (int)(System.currentTimeMillis() / 1000 / 60 /60 );
                            try{
                                it.setData(Uri.parse("mailto:" + getString(R.string.label_mail_summary)
                                        + "?subject=Subcribe Jota Text Editor(" + orderId + ")"
                                        + "&body=nickname:%20" + URLEncoder.encode(nickname, "utf-8")
                                        ));
                                startActivity(it);
                                Toast.makeText(DonateActivity.this , R.string.toast_send_mail, Toast.LENGTH_LONG).show();

                            }catch(Exception e){}
                            finish();
                        }
                    }
                })
                .setNegativeButton(R.string.label_close, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();

            }
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request, ResponseCode responseCode) {
            if (Consts.DEBUG) {
                Log.d(TAG, request.mProductId + ": " + responseCode);
            }
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase was successfully sent to server");
                }
                mProgressDialog = new ProgressDialog(DonateActivity.this);
                mProgressDialog.setMessage(getString(R.string.spinner_message));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "user canceled purchase");
                }
            } else {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase failed");
                }
            }
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.d(TAG, "completed RestoreTransactions request");
                }
            } else {
                if (Consts.DEBUG) {
                    Log.d(TAG, "RestoreTransactions error: " + responseCode);
                }
            }
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int index;
        if ( isDebuggable() ){
            index = 1;
        }else{
            index = 0;
        }

        mjsobj.mProcBilling = new Runnable() {
            @Override
            public void run() {
                if (!mBillingService.requestPurchase(CATALOG[index], null)) {
                    showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
                }
            }
        };

        mHandler = new Handler();
        mDonatePurchaseObserver = new DonatePurchaseObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);

        // Check if billing is supported.
        if (!mBillingService.checkBillingSupported()) {
            showDialog(DIALOG_CANNOT_CONNECT_ID);
        }
    }

    /**
     * Called when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mDonatePurchaseObserver);
    }

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mDonatePurchaseObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingService.unbind();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CANNOT_CONNECT_ID:
            return createDialog(R.string.cannot_connect_title,
                    R.string.cannot_connect_message);
        case DIALOG_BILLING_NOT_SUPPORTED_ID:
            return createDialog(R.string.billing_not_supported_title,
                    R.string.billing_not_supported_message);
        default:
            return null;
        }
    }

    private Dialog createDialog(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        return builder.create();
    }

    public boolean isDebuggable() {
        PackageManager manager = getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = manager.getApplicationInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE)
            return true;
        return false;
    }

}
