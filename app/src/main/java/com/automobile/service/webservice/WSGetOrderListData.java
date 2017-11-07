package com.automobile.service.webservice;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.automobile.service.model.Order.OrderListModel;
import com.automobile.service.util.WsConstants;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Skywevas Technologies on 23/05/17.
 * This class is makiing api call for get Order History list webservice
 */


public class WSGetOrderListData extends WSUtil {
    private Context context;
    private String message;
    private boolean success;
    private String listCount;


    public String getListCount() {
        return listCount;
    }

    public void setListCount(String listCount) {
        this.listCount = listCount;
    }


    public WSGetOrderListData(final Context context) {
        super(context);
        this.context = context;
    }


    public ArrayList<OrderListModel> executeService(Context context, final Bundle bundle) {


        this.context = context;
        return parseResponse(GET(buildURL(WsConstants.METHOD_ORDERLIST, bundle).toString()));
    }


    private ArrayList<OrderListModel> parseResponse(final String response) {

        if (response != null && response.trim().length() > 0) {

            try {
                final JSONObject jsonObject = new JSONObject(response);
                final WsConstants wsConstants = new WsConstants();

                success = jsonObject.optString(wsConstants.PARAMS_SUCCESS).equals("1") ? true : false;
                message = jsonObject.optString(wsConstants.PARAMS_MESSAGE);

                setSuccess(isSuccess());
                setMessage(message);

                if (success) {
                    String details = jsonObject.optString(wsConstants.PARAMS_DATA);
                    JSONArray jsonarray = new JSONArray(details);
                    Type listType = new TypeToken<ArrayList<OrderListModel>>() {
                    }.getType();
                    ArrayList<OrderListModel> productModelArrayList = new GsonBuilder().create().fromJson(jsonarray.toString(), listType);
                    Log.d("Product.size", "Product.size==" + productModelArrayList.size());
                    return productModelArrayList;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

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


}
