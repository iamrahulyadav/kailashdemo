package com.automobile.service.webservice;

import android.content.Context;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.WsConstants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * Created by Skywevas Technologies on 16/05/17.
 * This class is makiing api call for Login webservice
 */

public class WSAddWallate {

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


    public WSAddWallate(final Context context) {
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

    public void executeService(Context context, final String payment_id, final String wallet_amount, final String wallet_action) {

        final String url = WsConstants.BASE_URL + WsConstants.METHOD_ADD_WALLATE;
        final String response = new WSUtil(context).callServiceHttpPost(context, url, generateLoginRequest(payment_id, wallet_amount, wallet_action));
        parseResponse(response);

    }

    private RequestBody generateLoginRequest(final String payment_id, final String wallet_amount, final String wallet_action) {

        final String userId = AutumobileAplication.getmInstance().getSharedPreferences().getString(context.getString((R.string.preferances_userID)), "");
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add(ParamsConstans.PARAM_USERID, userId);
        formEncodingBuilder.add(ParamsConstans.PARAM_PAYMENT_ID, payment_id);
        formEncodingBuilder.add(ParamsConstans.PARAM_PAYMENT_ACTION, wallet_action);
        formEncodingBuilder.add(ParamsConstans.PARAM_WALLATE_AMOUNT, wallet_amount);
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

                if (success) {
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_wallate), jsonObject.getString("user_wallet_amount"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
