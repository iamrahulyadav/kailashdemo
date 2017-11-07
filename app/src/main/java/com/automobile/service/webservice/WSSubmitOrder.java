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
 * Created by Skywevas Technologies on 06/06/17.
 * This class is makiing api call for Submit order
 */

public class WSSubmitOrder {

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


    public WSSubmitOrder(final Context context) {
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

    public void executeService(Context context, final String totalPrice, final String totalItem, final String address, final String productArr, final String paymentId, final String paymentMethod,final String wallateAmount) {

        final String url = WsConstants.BASE_URL + WsConstants.METHOD_SUBMIT_ORDER;
        final String response = new WSUtil(context).callServiceHttpPost(context, url, generateLoginRequest(totalPrice, totalItem, address, productArr, paymentId, paymentMethod,wallateAmount));
        parseResponse(response);

    }

    private RequestBody generateLoginRequest(final String totalPrice, final String totalItem, final String address, final String productArr, final String paymentId, final String paymentMethod,final String wallateAmount) {

        final String userId = AutumobileAplication.getmInstance().getSharedPreferences().getString(context.getString((R.string.preferances_userID)), "");
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add(ParamsConstans.PARAM_USERID, userId);
        formEncodingBuilder.add(ParamsConstans.PARAM_PRODUCT_ID, productArr);
        formEncodingBuilder.add(ParamsConstans.PARAM_ADDRESS, address);
        formEncodingBuilder.add(ParamsConstans.PARAM_ORDER_TOTAL_AMOUNT, totalPrice);
        formEncodingBuilder.add(ParamsConstans.PARAM_PAYMENT_ID, paymentId);
        formEncodingBuilder.add(ParamsConstans.PARAM_PAYMENT_METHOD, paymentMethod);
        formEncodingBuilder.add(ParamsConstans.PARAM_WALLATE_AMOUNT, wallateAmount);

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


                if (isSuccess()) {
                    AutumobileAplication.getmInstance().savePreferenceDataString(context.getString(R.string.preferances_wallate), jsonObject.optString("user_wallet_amount"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
