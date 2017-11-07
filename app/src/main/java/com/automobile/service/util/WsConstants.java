package com.automobile.service.util;

public class WsConstants {


    public final static String BASE_URL = "http://sambhavlabs.com/app_automobile/api/";
    public final static int CONNECTION_TIMEOUT = 30;


    /**
     * API name for web service implementation
     */

    public final static String METHOD_LOGIN = "users/login/";
    public final static String METHOD_REGISTER = "users/register/";
    public final static String METHOD_PROFILE = "users/profile_update/";
    public final static String METHOD_FORGOT_PASSWORD = "users/forget_password/";
    public final static String METHOD_CHANGE_PSW = "users/password_change/";
    public final static String METHOD_PRODUCT_LIST = "activity/product_list/";
    public final static String METHOD_CART_LIST = "activity/my_cart/";
    public final static String METHOD_PRODUCT_DETAILS = "activity/product_detail/";
    public final static String METHOD_BOOK_SERVICE = "activity/service_book/";
    public final static String METHOD_BOOK_SERVICE_LIST = "activity/service_list/";
    public final static String METHOD_NOTIFICATION_LIST = "activity/notification_list/";
    public final static String METHOD_ORDERLIST = "activity/order_list/";
    public final static String METHOD_SUBMIT_ORDER = "activity/order_create/";
    public final static String METHOD_SOCIAL_LOGIN = "users/social_login/";
    public final static String METHOD_ADD_WALLATE = "users/my_wallet/";




    // Base params
    public final String PARAMS_SETTING = "settings";
    public final String PARAMS_DATA = "details";
    public final String PARAMS_SUCCESS = "status";
    public final String PARAMS_MESSAGE = "msg";


    public WsConstants() {
    }


}