package com.automobile.service.view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomEdittext;
import com.automobile.service.util.Utils;
import com.automobile.service.webservice.WSRegister;


public class RegisterActivity extends BaseActivity {

    private LinearLayout llContainer;
    private CustomEdittext etEmail;
    private CustomEdittext etPassword;
    private EditText etUsername;
    private CustomEdittext etPhone;
    private Button btnLogin;
    private TextView tvReg;
    private ImageView ivClose;

    private String emailAdd;
    private String password;
    private String phone;
    private String username;
    private RegisterAsyncTask registerAsyncTask;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        initComponents();


    }


    @Override
    public void initComponents() {

        llContainer = (LinearLayout) findViewById(R.id.activity_register_llContainer);
        etEmail = (CustomEdittext) findViewById(R.id.activity_register_etEmail);
        etPassword = (CustomEdittext) findViewById(R.id.activity_register_etPsw);
        etPhone = (CustomEdittext) findViewById(R.id.activity_register_etPhone);
        etUsername = (EditText) findViewById(R.id.activity_register_etUsername);
        btnLogin = (Button) findViewById(R.id.activity_register_btnDone);
        tvReg = (TextView) findViewById(R.id.activity_register_tvReg);
        ivClose=(ImageView) findViewById(R.id.activity_register_ivClose) ;

        btnLogin.setOnClickListener(this);
        tvReg.setOnClickListener(this);
        ivClose.setOnClickListener(this);


    }


    /**
     * Validating form
     */

    private void submitForm() {

        username = etUsername.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        emailAdd = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_username), true, RegisterActivity.this);
        } else if (emailAdd.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_email), true, RegisterActivity.this);
        } else if (!Utils.isValidEmail(emailAdd)) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_valid_email), true, RegisterActivity.this);
        } else if (password.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_password), true, RegisterActivity.this);
        } else if (phone.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_phone), true, RegisterActivity.this);
        } else {
            register();
        }

    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(RegisterActivity.this);

        if (v == btnLogin) {

            submitForm();
        } else if (v == tvReg) {

            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
            Utils.hideKeyboard(RegisterActivity.this);

        }
        else if (v == ivClose) {

            finish();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
            Utils.hideKeyboard(RegisterActivity.this);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        Utils.hideKeyboard(RegisterActivity.this);
    }

    public void register() {

        if (Utils.isOnline(RegisterActivity.this, true)) {
            if (registerAsyncTask != null && registerAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                registerAsyncTask.execute();
            } else if (registerAsyncTask == null || registerAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                registerAsyncTask = new RegisterAsyncTask();
                registerAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, RegisterActivity.this);

        }
    }


    private class RegisterAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSRegister wsRegister;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(RegisterActivity.this);
            progress.setMessage(getString(R.string.please_wait));
            progress.setCancelable(false);
            progress.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                wsRegister = new WSRegister(RegisterActivity.this);
                wsRegister.executeService(RegisterActivity.this, username, emailAdd, password, phone);

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

            if (wsRegister.isSuccess()) {
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                finish();


            } else {

                Utils.snackbar(llContainer, "" + wsRegister.getMessage(), true, RegisterActivity.this);

            }

        }

    }


    @Override
    protected void onStart() {

        super.onStart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (registerAsyncTask != null && registerAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            registerAsyncTask.cancel(true);
        }
    }
}
