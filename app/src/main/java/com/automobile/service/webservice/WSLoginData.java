package com.automobile.service.webservice;

import android.content.Context;


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
 * This class is makiing api call for Login webservice
 */

public class WSLoginData {

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


    public WSLoginData(final Context context) {
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

    public void executeService(Context context,final String email, final String password ) {

        final String url = WsConstants.BASE_URL + WsConstants.METHOD_LOGIN;
        final String response = new WSUtil(context).callServiceHttpPost(context, url, generateLoginRequest(password, email));
        parseResponse(response);

    }

    private RequestBody generateLoginRequest(final String password, final String email) {

       // final String uniqId = ImliveAplication.getmInstance().getSharedPreferences().getString(context.getString((R.string.preferances_token)), "");

        final String uniqId = Utils.getDeviceID(context);
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add(ParamsConstans.PARAM_PASSWORD, password);
        formEncodingBuilder.add(ParamsConstans.PARAM_DEVICE_TOKEN, uniqId);
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

                if (success)
                {
                    final JSONObject jsonObjectData = jsonObject.optJSONObject(wsConstants.PARAMS_DATA);
                    setUserId(jsonObjectData.getString("user_id"));

                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_userID), jsonObjectData.getString("user_id"));
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_userName), jsonObjectData.getString("user_name"));
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_userEmail), jsonObjectData.getString("user_email"));
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_userPhone), jsonObjectData.getString("user_phone"));
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_userProfilePic), jsonObjectData.getString("user_profile"));
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_wallate), jsonObjectData.getString("user_wallet_amount"));


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
