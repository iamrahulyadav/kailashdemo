package com.automobile.service;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * *************************************************************************
 * AutumobileAplication
 *
 * @CreatedDate:
 * @ModifiedBy: not yet
 * @ModifiedDate: not yet
 * @purpose:This application class to set application level variable and method which
 * used through-out application
 * <p/>
 * *************************************************************************
 */

public class AutumobileAplication extends Application {


    private static AutumobileAplication mInstance;
    private SharedPreferences sharedPreferences;

    private String currentLatLong;

    public static AutumobileAplication getmInstance() {
        return mInstance;
    }

    public static void setmInstance(AutumobileAplication mInstance) {
        AutumobileAplication.mInstance = mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        sharedPreferences = getSharedPreferences(getString(R.string.app_name_new), Context.MODE_PRIVATE);


    }


    public String getCurrentLatLong() {
        return currentLatLong;
    }

    public void setCurrentLatLong(String currentLatLong) {
        this.currentLatLong = currentLatLong;
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void savePreferenceDataString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferenceDataBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void savePreferenceDataLong(String key, Long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void savePreferenceDataInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * Call when application is close
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void clearePreferenceData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


}
