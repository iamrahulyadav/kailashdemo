package com.automobile.service.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomEdittext;
import com.automobile.service.util.Utils;
import com.automobile.service.webservice.WSLoginData;
import com.automobile.service.webservice.WSSocialLogin;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class LoginActivity extends BaseActivity {

    private LinearLayout llContainer;
    private CustomEdittext etPassword;
    private CustomEdittext etEmail;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView ivForgotPsw;
    private ImageView ivFbLogin;

    private String password;
    private String email;
    private LoginAsyncTask loginAsyncTask;
    private CheckLoginAsyncTask checkLoginAsyncTask;
    private ProgressDialog progress;

    //Facebook Related components
    private CallbackManager callbackFbManager;

    private String authId;
    private String emailId;
    private String fname;
    private String lname;
    private String authType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        initComponents();
        generateKeyHash();
        initFacebook();

    }


    @Override
    public void initComponents() {

        llContainer = (LinearLayout) findViewById(R.id.activity_login_llContainer);
        etPassword = (CustomEdittext) findViewById(R.id.activity_login_etPsw);
        etEmail = (CustomEdittext) findViewById(R.id.activity_login_etEmail);
        btnLogin = (Button) findViewById(R.id.activity_login_btnDone);
        tvRegister = (TextView) findViewById(R.id.activity_login_tvReg);
        ivForgotPsw = (TextView) findViewById(R.id.activity_login_tvForgotPsw);
        ivFbLogin = (ImageView) findViewById(R.id.activity_login_ivFacebook);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        ivForgotPsw.setOnClickListener(this);
        ivFbLogin.setOnClickListener(this);


    }


    /**
     * Validating form
     */

    private void submitForm() {


        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_email), true, LoginActivity.this);
        } else if (!Utils.isValidEmail(email)) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_valid_email), true, LoginActivity.this);
        } else if (password.isEmpty()) {
            Utils.snackbar(llContainer, getString(R.string.val_enter_password), true, LoginActivity.this);
        } else {
            login();
        }

    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(LoginActivity.this);

        if (v == btnLogin) {

            submitForm();
        } else if (v == tvRegister) {


            Intent mMenuIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(mMenuIntent);
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        } else if (v == ivForgotPsw) {


            Intent mMenuIntent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(mMenuIntent);
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        } else if (v == ivFbLogin) {

            if (Utils.isOnline(LoginActivity.this, true)) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            } else {
                Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getApplicationContext());
            }
        }


    }


    private void initFacebook() {
       // FacebookSdk.sdkInitialize(getApplicationContext());
        callbackFbManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackFbManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject fbResObj, GraphResponse response) {
                        String id = null;

                        try
                        {

                            id = fbResObj.getString("id");
                            Log.d("onSuccess","onSuccess"+id);

                            String emailAddress = fbResObj.optString("phone");
                            String first_name = fbResObj.optString("first_name");
                            String last_name = fbResObj.optString("last_name");
                            if (id != null) {
                                Log.d("first_name","first_name=="+first_name);
                                socialLoginService(emailAddress, id, first_name, last_name, "facebook");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("onSuccess","onSuccess"+e.getMessage());
                        }

                    }
                });
                final Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

                Log.d("onError","onCancel");
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.d("onError","onError"+error.getMessage());

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackFbManager.onActivityResult(requestCode, resultCode, data);
    }


    private void generateKeyHash() {
        try {
            final PackageInfo info = getPackageManager().getPackageInfo("com.automobile.service", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:","=="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.automobile.service", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key====","=="+ something);
                Log.d("hash key====","=="+ something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }
    private void socialLoginService(final String emailAddress, final String id, final String fnam, final String lnam, final String facebook) {


        Log.d("socialLoginService","socialLoginService"+id);
        authId = id;
        emailId = emailAddress;
        authType = facebook;
        fname = fnam;
        lname = lnam;

        checkLogin();


    }


    public void login() {

        if (Utils.isOnline(LoginActivity.this, true)) {
            if (loginAsyncTask != null && loginAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                loginAsyncTask.execute();
            } else if (loginAsyncTask == null || loginAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, LoginActivity.this);

        }
    }


    private class LoginAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSLoginData wsLoginData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage(getString(R.string.please_wait));
            progress.setCancelable(false);
            progress.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                wsLoginData = new WSLoginData(LoginActivity.this);
                wsLoginData.executeService(LoginActivity.this, email, password);

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

            if (wsLoginData.isSuccess()) {
                AutumobileAplication.getmInstance().savePreferenceDataBoolean(getString(R.string.preferances_isNormallogin),true);
                Utils.snackbar(llContainer, "" + wsLoginData.getMessage(), true, LoginActivity.this);
                goToHome();

            } else {

                Utils.snackbar(llContainer, "" + wsLoginData.getMessage(), true, LoginActivity.this);

            }

        }

    }

    private void goToHome() {

        //openHomeActivity();

        if (Build.VERSION.SDK_INT < 23) {
            openHomeActivity();
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openHomeActivity();
            } else {
                Utils.checkPermitionCameraGaller(LoginActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openHomeActivity();
                } else {
                    // permission denied
                }
            }
        }
    }

    private void openHomeActivity() {

        AutumobileAplication.getmInstance().savePreferenceDataBoolean(getString(R.string.preferances_islogin), true);
        Intent intent = new Intent(getApplicationContext(), MenuBarActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        finish();
    }


    @Override
    protected void onStart() {

        super.onStart();
    }


    public void checkLogin() {

        if (Utils.isOnline(LoginActivity.this, true)) {

            if (checkLoginAsyncTask != null && checkLoginAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                checkLoginAsyncTask.execute();
            } else if (checkLoginAsyncTask == null || checkLoginAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                checkLoginAsyncTask = new CheckLoginAsyncTask();
                checkLoginAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, LoginActivity.this);
        }
    }


    private class CheckLoginAsyncTask extends AsyncTask<Void, Void, Void> {
        private WSSocialLogin mWSLogin;
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = Utils.showProgressDialog(LoginActivity.this, getString(R.string.please_wait), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {


                mWSLogin = new WSSocialLogin(getApplicationContext());
                mWSLogin.executeService(authId, authType,emailId,fname+" "+lname);

            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            Utils.dismissProgressDialog(mDialog);

            if (mWSLogin.isSuccess()) {

                AutumobileAplication.getmInstance().savePreferenceDataBoolean(getString(R.string.preferances_isNormallogin),false);
              goToHome();

            } else {

                Utils.snackbar(llContainer, "" + mWSLogin.getMessage(), true, LoginActivity.this);

            }

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (loginAsyncTask != null && loginAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            loginAsyncTask.cancel(true);
        }
    }
}
