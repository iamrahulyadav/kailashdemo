package com.automobile.service.payumoney;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.automobile.service.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Skywevas Technologies
 */


public class PayMentGateWay extends Activity {

    private Context context;
    private WebView webView;
    private Handler mHandler = new Handler();
    private ProgressDialog progressDialog;
    final Activity activity = this;



    // TEST
    private String merchant_key = "kYz2vV"; // test
    private String salt = "zhoXe53j"; // test
    //    private String base_url = "https://test.payu.in";//test
   private String base_url = "https://test.payu.in/_payment";//test new


    private String post_Data = "";
    private String tag = "PayMentGateWay";
    private String hash, hashSequence;
    private String txnid = "";
    private String action1 = "";
    private String hashString = "";
    private int error = 0;
    private Map<String, String> mapParams;

    private ArrayList<String> post_val = new ArrayList<String>();
    private Map<String, String> params;
    private String SUCCESS_URL = "https://www.payumoney.com/mobileapp/payumoney/success.php"; // failed
    private String FAILED_URL = "https://www.payumoney.com/mobileapp/payumoney/failure.php";

    private String getFirstName, getNumber, getEmailAddress, getAmount;


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(activity);


        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        webView = new WebView(this);
        setContentView(webView);

        Intent oIntent = getIntent();

        getFirstName = oIntent.getExtras().getString("FIRST_NAME");
        getNumber = oIntent.getExtras().getString("PHONE_NUMBER");
        getEmailAddress = oIntent.getExtras().getString("EMAIL_ADDRESS");
        getAmount = oIntent.getExtras().getString("AMOUNT");

        if (getNumber.isEmpty()) {
            getNumber = "0123456789";
        }
        if (getEmailAddress.isEmpty()) {
            getEmailAddress = "abc@gmail.com";
        }


        Log.d("onCreate", "onCreate==" + getFirstName + "==" + getNumber + "==" + getEmailAddress + "==" + getAmount);


        params = new HashMap<String, String>();
        params.put("key", merchant_key);

        params.put("amount", getAmount);
        params.put("firstname", getFirstName);
        params.put("email", getEmailAddress);
        params.put("phone", getNumber);
        params.put("productinfo", "shopping accessories");
        params.put("surl", SUCCESS_URL);
        params.put("furl", FAILED_URL);
        params.put("service_provider", "payu_paisa");
        params.put("lastname", "");
        params.put("address1", "");
        params.put("address2", "");
        params.put("city", "");
        params.put("state", "");
        params.put("country", "");
        params.put("zipcode", "");
        params.put("udf1", "");
        params.put("udf2", "");
        params.put("udf3", "");
        params.put("udf4", "");
        params.put("udf5", "");
        params.put("pg", "");


        if (empty(params.get("txnid"))) {
            Random rand = new Random();
            String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
            txnid = hashCal("SHA-256", rndm).substring(0, 20);
            params.put("txnid", txnid);
        } else
            txnid = params.get("txnid");
        //String udf2 = txnid;
        String txn = "abcd";
        hash = "";
        String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
        if (empty(params.get("hash")) && params.size() > 0) {
            if (empty(params.get("key"))
                    || empty(params.get("txnid"))
                    || empty(params.get("amount"))
                    || empty(params.get("firstname"))
                    || empty(params.get("email"))
                    || empty(params.get("phone"))
                    || empty(params.get("productinfo"))
                    || empty(params.get("surl"))
                    || empty(params.get("furl"))
                    || empty(params.get("service_provider"))

                    ) {
                error = 1;
            } else {
                String[] hashVarSeq = hashSequence.split("\\|");

                for (String part : hashVarSeq) {
                    hashString = (empty(params.get(part))) ? hashString.concat("") : hashString.concat(params.get(part));
                    hashString = hashString.concat("|");
                }
                hashString = hashString.concat(salt);


                hash = hashCal("SHA-512", hashString);
                action1 = base_url.concat("/_payment");
            }
        } else if (!empty(params.get("hash"))) {
            hash = params.get("hash");
            action1 = base_url.concat("/_payment");
        }

        webView.setWebViewClient(new MyWebViewClient() {

            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //make sure dialog is showing
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {

                final AlertDialog.Builder builder = new AlertDialog.Builder(PayMentGateWay.this);
                builder.setMessage(""+description);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent data = getIntent();
                        data.putExtra("success", "0");
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent data = getIntent();
                        data.putExtra("success", "0");
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();


            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(PayMentGateWay.this);
                builder.setMessage("SSL Certificate invalid");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();

                        Intent data = getIntent();
                        data.putExtra("success", "0");
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();

                        Intent data = getIntent();
                        data.putExtra("success", "0");
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();


            }


        });


        webView.setVisibility(0);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(2);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);

        //webView.addJavascriptInterface(new PayUJavaScriptInterface(getApplicationContext()), "PayUMoney");
        webView.addJavascriptInterface(new PayUJavaScriptInterface(), "PayUMoney");
        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("key", merchant_key);
        mapParams.put("hash", PayMentGateWay.this.hash);
        mapParams.put("txnid", (empty(PayMentGateWay.this.params.get("txnid"))) ? "" : PayMentGateWay.this.params.get("txnid"));
        Log.d(tag, "txnid: " + PayMentGateWay.this.params.get("txnid"));
        mapParams.put("service_provider", "payu_paisa");

        mapParams.put("amount", (empty(PayMentGateWay.this.params.get("amount"))) ? "" : PayMentGateWay.this.params.get("amount"));
        mapParams.put("firstname", (empty(PayMentGateWay.this.params.get("firstname"))) ? "" : PayMentGateWay.this.params.get("firstname"));
        mapParams.put("email", (empty(PayMentGateWay.this.params.get("email"))) ? "" : PayMentGateWay.this.params.get("email"));
        mapParams.put("phone", (empty(PayMentGateWay.this.params.get("phone"))) ? "" : PayMentGateWay.this.params.get("phone"));

        mapParams.put("productinfo", (empty(PayMentGateWay.this.params.get("productinfo"))) ? "" : PayMentGateWay.this.params.get("productinfo"));
        mapParams.put("surl", (empty(PayMentGateWay.this.params.get("surl"))) ? "" : PayMentGateWay.this.params.get("surl"));
        mapParams.put("furl", (empty(PayMentGateWay.this.params.get("furl"))) ? "" : PayMentGateWay.this.params.get("furl"));
        mapParams.put("lastname", (empty(PayMentGateWay.this.params.get("lastname"))) ? "" : PayMentGateWay.this.params.get("lastname"));

        mapParams.put("address1", (empty(PayMentGateWay.this.params.get("address1"))) ? "" : PayMentGateWay.this.params.get("address1"));
        mapParams.put("address2", (empty(PayMentGateWay.this.params.get("address2"))) ? "" : PayMentGateWay.this.params.get("address2"));
        mapParams.put("city", (empty(PayMentGateWay.this.params.get("city"))) ? "" : PayMentGateWay.this.params.get("city"));
        mapParams.put("state", (empty(PayMentGateWay.this.params.get("state"))) ? "" : PayMentGateWay.this.params.get("state"));

        mapParams.put("country", (empty(PayMentGateWay.this.params.get("country"))) ? "" : PayMentGateWay.this.params.get("country"));
        mapParams.put("zipcode", (empty(PayMentGateWay.this.params.get("zipcode"))) ? "" : PayMentGateWay.this.params.get("zipcode"));
        mapParams.put("udf1", (empty(PayMentGateWay.this.params.get("udf1"))) ? "" : PayMentGateWay.this.params.get("udf1"));
        mapParams.put("udf2", (empty(PayMentGateWay.this.params.get("udf2"))) ? "" : PayMentGateWay.this.params.get("udf2"));

        mapParams.put("udf3", (empty(PayMentGateWay.this.params.get("udf3"))) ? "" : PayMentGateWay.this.params.get("udf3"));
        mapParams.put("udf4", (empty(PayMentGateWay.this.params.get("udf4"))) ? "" : PayMentGateWay.this.params.get("udf4"));
        mapParams.put("udf5", (empty(PayMentGateWay.this.params.get("udf5"))) ? "" : PayMentGateWay.this.params.get("udf5"));
        mapParams.put("pg", (empty(PayMentGateWay.this.params.get("pg"))) ? "" : PayMentGateWay.this.params.get("pg"));
        webview_ClientPost(webView, action1, mapParams.entrySet());

    }


    private final class PayUJavaScriptInterface {

        PayUJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void success(final long id, final String paymentId) {
            mHandler.post(new Runnable() {
                public void run() {
                    mHandler = null;

	                    /*Intent intent = new Intent();
                        intent.putExtra(Constants.RESULT, "success");
	                    intent.putExtra(Constants.PAYMENT_ID, paymentId);
	                    setResult(RESULT_OK, intent);
	                    finish();*/
                    // new PostRechargeData().execute();


                    Log.d("paymentId", "paymentId===" + paymentId);
                    Log.d("paymentId", "id===" + id);

                    Intent data = getIntent();
                    data.putExtra("success", "1");
                    data.putExtra("paymentId", paymentId);
                    setResult(RESULT_OK, data);
                    finish();

                    Toast.makeText(getApplicationContext(), "Successfully payment.", Toast.LENGTH_LONG).show();

                }
            });
        }

        @JavascriptInterface
        public void failure(final String id, final String error) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //cancelPayment();
                    Toast.makeText(getApplicationContext(), "Cancel payment" + error, Toast.LENGTH_LONG).show();

                    Intent data = getIntent();
                    data.putExtra("success", "0");
                    setResult(RESULT_OK, data);
                    finish();

                }
            });
        }

        @JavascriptInterface
        public void failure() {
            failure("");
        }

        @JavascriptInterface
        public void failure(final String params) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

	                    /*Intent intent = new Intent();
                        intent.putExtra(Constants.RESULT, params);
	                    setResult(RESULT_CANCELED, intent);
	                    finish();*/
                    Toast.makeText(getApplicationContext(), "Failed payment" + error, Toast.LENGTH_LONG).show();
                    Intent data = getIntent();
                    data.putExtra("success", "0");
                    setResult(RESULT_OK, data);
                    finish();

                }
            });
        }

    }


    public void webview_ClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {


        try {
            StringBuilder sb = new StringBuilder();

            sb.append("<html><head></head>");
            sb.append("<body onload='form1.submit()'>");
            sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
            for (Map.Entry<String, String> item : postData) {
                sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
            }
            sb.append("</form></body></html>");
            Log.d(tag, "webview_ClientPost called");

            //setup and load the progress bar
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading. Please wait...");
            webView.loadData(sb.toString(), "text/html", "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public boolean empty(String s) {
        if (s == null || s.trim().equals(""))
            return true;
        else
            return false;
    }

    public String hashCal(String type, String str) {
        byte[] hashseq = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();


            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) hexString.append("0");
                hexString.append(hex);
            }

        } catch (NoSuchAlgorithmException nsae) {
        }

        return hexString.toString();


    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("http")) {
                progressDialog.show();
                view.loadUrl(url);
                System.out.println("myresult " + url);
            } else {
                return false;
            }

            return true;
        }
    }


}