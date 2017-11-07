package com.automobile.service.webservice;

import android.content.Context;
import android.os.Bundle;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.util.WsConstants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Skywevas Technologies on 16/05/17.
 * This class is makiing api call for Forgot password webservice
 */


public class WSForgotData extends WSUtil
{

    private Context context;
    private String message;
    private boolean success;
    private String userId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public WSForgotData(final Context context) {
        super(context);
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void executeService(Context context, final String email)
    {
        this.context=context;
       // parseResponse(GET(buildURL(WsConstants.METHOD_FORGOT_PASSWORD, bundle).toString()));
        final String url = WsConstants.BASE_URL + WsConstants.METHOD_FORGOT_PASSWORD;
        final String response = new WSUtil(context).callServiceHttpPost(context, url, generateLoginRequest(email));
        parseResponse(response);
    }

    private RequestBody generateLoginRequest(final String email) {

        // final String uniqId = ImliveAplication.getmInstance().getSharedPreferences().getString(context.getString((R.string.preferances_token)), "");

        final String uniqId = Utils.getDeviceID(context);
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        //formEncodingBuilder.add(ParamsConstans.PARAM_DEVICE_TOKEN, uniqId);
        formEncodingBuilder.add(ParamsConstans.PARAM_EMAIL_ID, email);
        return formEncodingBuilder.build();
    }

    private void parseResponse(final String response) {

        if (response != null && response.trim().length() > 0) {

            try {
                final JSONObject jsonObject = new JSONObject(response);
                final WsConstants wsConstants = new WsConstants();

                success = jsonObject.optString(wsConstants.PARAMS_SUCCESS).equals("1") ? true : false;
                message = jsonObject.optString(wsConstants.PARAMS_MESSAGE);

                setMessage(message);
                setSuccess(success);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
