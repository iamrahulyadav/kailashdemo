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
 * Created by AutoPlus
 * This class is makiing api call for user WSSocialLogin webservice
 */


public class WSSocialLogin
{

    private Context context;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private boolean success;

    private long uId;

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }


    public WSSocialLogin(final Context context) {
        this.context = context;
    }

    public void executeService(final String authId, final String type,final String emailId,final String name) {

        final String url;
        url = WsConstants.BASE_URL + WsConstants.METHOD_SOCIAL_LOGIN;
        final String response = new WSUtil().callServiceHttpPost(context, url, generateLoginRequest(authId,type,emailId,name));
         parseResponse(response);
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


    private RequestBody generateLoginRequest(final String authId, final String type,final String emailId,final String name) {

        final String uniqId = Utils.getDeviceID(context);
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add(ParamsConstans.PARAM_SOCIAL_ID, authId);
       // formEncodingBuilder.add(ParamsConstans.PARAM_SOCIAL_TYPE, type);
        formEncodingBuilder.add(ParamsConstans.PARAM_DEVICE_TOKEN, uniqId);
        formEncodingBuilder.add(ParamsConstans.PARAM_EMAIL_ID, emailId);
        formEncodingBuilder.add(ParamsConstans.PARAM_USERNAME, name);
        return formEncodingBuilder.build();
    }


}
