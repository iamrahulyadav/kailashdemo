package com.automobile.service.webservice;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.automobile.service.R;
import com.automobile.service.util.Utils;
import com.automobile.service.util.WsConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Web Service utility class to call web urls. And returns response.
 */

public class WSUtilOld
{
    private Context mContext;
    private OkHttpClient client;


    private final int TIMEOUT_SECONDS = 30;
    private final int TIMEOUT_SECONDS_LONG = 30 * 2 * 2;

    public WSUtilOld(Context context) {

        mContext = context;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

    }

    public String callServiceHttpPost(final Context mContext, final String url, final RequestBody requestBody) {
        this.mContext = mContext;

        String responseString;

        try {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS).build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            Utils.writeLog(mContext, String.format("Request String : %s", request.toString()));

            final Response response = okHttpClient.newCall(request).execute();
            responseString = response.body().string();
            Utils.writeLog(mContext, "Response String : " + responseString);

            if (TextUtils.isEmpty(responseString) || !isJSONValid(responseString)) {
                responseString = getNetWorkError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseString = getNetWorkError();
        }
        return responseString;

    }


    /**
     * Builds url with parameters
     *
     * @param url    url
     * @param bundle parameters
     * @return
     */
    protected String buildURL(String url, Bundle bundle) {

        HttpUrl.Builder builder = getBaseUrl().addPathSegment(url);
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (bundle.get(key) != null && !TextUtils.isEmpty(bundle.get(key).toString())) {
                    builder.addQueryParameter(key, bundle.get(key).toString());
                }
            }
        }

        final String fullUrl = Uri.decode(builder.build().toString());
        Log.d("builder", "builder==" + fullUrl);

        return fullUrl;
    }

    @NonNull
    protected HttpUrl.Builder getBaseUrl() {

        //http://php54.indianic.com/centuary_mattress/WS/

        return new HttpUrl.Builder()
                .scheme(mContext.getString(R.string.ws_scheme_http))
                .host(mContext.getString(R.string.ws_host))
                .addPathSegment(mContext.getString(R.string.ws_pathsegment));

    }

    /**
     * GET network request
     *
     * @param url url
     * @return
     */
    protected String GET(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();

            if (TextUtils.isEmpty(res) || !isJSONValid(res)) {
                res = getNetWorkError();
            }

            return res;
        } catch (Exception e) {
            try {
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    /**
     * Builds request body
     *
     * @param bundle parameters
     * @return
     */
    protected RequestBody generateRequest(Bundle bundle) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (bundle.get(key) != null && !TextUtils.isEmpty(bundle.get(key).toString())) {
                    formBodyBuilder.add(key, bundle.get(key).toString());
                }
            }
        }
        return formBodyBuilder.build();
    }

    public String callServiceHttpPost(final Context mContext, final String url, final RequestBody requestBody, String token) {
        this.mContext = mContext;
        //Utils.writeLog(mContext, String.format("Request String : %s", requestBody.toString()));
        String responseString;

        try {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .header("access_token", token)
                    .url(url)
                    .post(requestBody)
                    .build();
            final Response response = okHttpClient.newCall(request).execute();
            responseString = response.body().string();
            Utils.writeLog(mContext, "Response String : " + responseString);

            if (TextUtils.isEmpty(responseString) || !isJSONValid(responseString)) {
                responseString = getNetWorkError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseString = getNetWorkError();
        }
        return responseString;

    }

    public String callServiceHttpGet(final Context mContext, final String url, final String token) {
        this.mContext = mContext;
        String responseString;
        try {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            final Response response = okHttpClient.newCall(request).execute();
            responseString = response.body().string();
            //Utils.writeLog(mContext, "Response String : " + responseString);
            if (TextUtils.isEmpty(responseString) || !isJSONValid(responseString)) {
                responseString = getNetWorkError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseString = getNetWorkError();
        }
        return responseString;
    }

    public String callServiceHttpGet(final Context mContext, final String url) {
        this.mContext = mContext;
        String responseString;
        try {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(WsConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            WsConstants wsConstants = new WsConstants();
            Request request = new Request.Builder()
//                    .header("access_token", token)
                    .url(url)
                    .get()
                    .build();

            final Response response = okHttpClient.newCall(request).execute();
            responseString = response.body().string();
            //Utils.writeLog(mContext, "Response String : " + responseString);
            if (TextUtils.isEmpty(responseString) || !isJSONValid(responseString)) {
                responseString = getNetWorkError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseString = getNetWorkError();
        }
        return responseString;
    }

    private String getNetWorkError() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final WsConstants wsConstants = new WsConstants();


            final JSONObject jsonObjectSetting = new JSONObject();
            jsonObjectSetting.put(wsConstants.PARAMS_SUCCESS, "0");
            jsonObjectSetting.put(wsConstants.PARAMS_MESSAGE, mContext.getString(R.string.check_connection));

            jsonObject.put(wsConstants.PARAMS_SETTING, jsonObjectSetting);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
