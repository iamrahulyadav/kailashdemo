package com.automobile.service.view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.automobile.service.R;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.webservice.WSForgotData;


public class ForgotActivity extends BaseActivity {


    private LinearLayout llContainer;
    private EditText etEmailId;
    private Button btnSubmit;
    private TextView tvLogin;
    private ImageView ivClose;

    private String emailId;
    private ForgotAsyncTask forgotAsyncTask;
    private ProgressDialog progress;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot);
        initComponents();

    }


    @Override
    public void initComponents() {

        llContainer = (LinearLayout) findViewById(R.id.activity_forgot_llContainer);
        etEmailId = (EditText) findViewById(R.id.activity_forgot_etEmailId);
        btnSubmit = (Button) findViewById(R.id.activity_forgot_btnDone);
        tvLogin = (TextView) findViewById(R.id.activity_forgot_tvSign);
        ivClose = (ImageView) findViewById(R.id.activity_forgot_ivClose);
        btnSubmit.setOnClickListener(this);
        ivClose.setOnClickListener(this);

    }


    /**
     * Validating form
     */

    private void submitForm() {

        emailId = etEmailId.getText().toString().trim();


        if (emailId.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_email), true, ForgotActivity.this);
        } else if (!Utils.isValidEmail(emailId)) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_valid_email), true, ForgotActivity.this);
        } else {

            forgotPsw();
        }

    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(ForgotActivity.this);

        /**
         * Logic to Prevent the Launch of the Fragment Twice if User makes
         * the Tap(Click) very Fast.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {

            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (v == btnSubmit) {
            submitForm();
        } else if (v == ivClose) {
            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        }


    }

    public void forgotPsw() {

        if (Utils.isOnline(ForgotActivity.this, true)) {
            if (forgotAsyncTask != null && forgotAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                forgotAsyncTask.execute();
            } else if (forgotAsyncTask == null || forgotAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                forgotAsyncTask = new ForgotAsyncTask();
                forgotAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, getString(R.string.check_connection), true, ForgotActivity.this);

        }
    }


    private class ForgotAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSForgotData wsForgotData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ForgotActivity.this);
            progress.setMessage(getString(R.string.please_wait));
            progress.setCancelable(false);
            progress.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                wsForgotData = new WSForgotData(ForgotActivity.this);
                wsForgotData.executeService(ForgotActivity.this, emailId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            if (wsForgotData.isSuccess()) {

                Utils.snackbar(llContainer, "" + wsForgotData.getMessage(), true, ForgotActivity.this);
                getFragmentManager().popBackStack();

            } else {

                Utils.snackbar(llContainer, "" + wsForgotData.getMessage(), true, ForgotActivity.this);

            }

        }

    }

    private Bundle getBundel() {

        Bundle bundle = new Bundle();
        bundle.putString(ParamsConstans.PARAM_EMAIL_ID, emailId);
        return bundle;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();


        if (forgotAsyncTask != null && forgotAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            forgotAsyncTask.cancel(true);
        }
    }
}
