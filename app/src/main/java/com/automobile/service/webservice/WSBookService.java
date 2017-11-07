package com.automobile.service.webservice;

import android.content.Context;
import android.util.Log;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.util.WsConstants;

import org.json.JSONObject;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by Skywevas Technologies on 20/05/17.
 * This class is makiing api call for Book Service webservice
 */

public class WSBookService {

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


    public WSBookService(final Context context) {
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

    public void executeService(Context context, final String service_type, final String carname, final String service_date, final String service_time, final String service_instruction,final String c_name,final String c_phone,final String c_address) {

        final String url = WsConstants.BASE_URL + WsConstants.METHOD_BOOK_SERVICE;
        final String response = new WSUtil(context).callServiceHttpPost(context, url, generateLoginRequest(service_type, carname, service_date, service_time, service_instruction,c_name,c_phone,c_address));
        parseResponse(response);

    }

    private RequestBody generateLoginRequest(final String service_type, final String carname, final String service_date, final String service_time, final String service_instruction,final String c_name,final String c_phone,final String c_address) {

        final String userId = AutumobileAplication.getmInstance().getSharedPreferences().getString(context.getString((R.string.preferances_userID)), "");
        final FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add(ParamsConstans.PARAM_USERID, userId);
        formEncodingBuilder.add(ParamsConstans.PARAM_SERVICE_TYPE, service_type);
        formEncodingBuilder.add(ParamsConstans.PARAM_SERVICE_CARNAME, carname);
        formEncodingBuilder.add(ParamsConstans.PARAM_SERVICE_DATE, service_date);
        formEncodingBuilder.add(ParamsConstans.PARAM_SERVICE_TIME, service_time);
        formEncodingBuilder.add(ParamsConstans.PARAM_SERVICE_INSTRUCTION, service_instruction);
        formEncodingBuilder.add(ParamsConstans.PARAM_CUSTOMER_NAME, c_name);
        formEncodingBuilder.add(ParamsConstans.PARAM_CUSTOMER_PHONE, c_phone);
        formEncodingBuilder.add(ParamsConstans.PARAM_CUSTOMER_ADDRESS, c_address);
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

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
