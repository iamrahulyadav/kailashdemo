package com.automobile.service.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.imagecrop.CropHelper;
import com.automobile.service.payumoney.PayMentGateWay;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSAddWallate;


public class AddWallateFragment extends BaseFragment {

    private LinearLayout llContainer;
    private EditText etAmount;
    private TextView tvMyWallate;
    private Button btnSubmit;


    private ProgressDialog progress;
    private AddWallateAsyncTask addWallateAsyncTask;
    private String wallateAmount = "";
    private String paymentId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_aa_wallate, container, false);
        initComponents(rootView);
        initToolbar();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initComponents(View rootView) {

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_add_wallate_llContainer);
        etAmount = (EditText) rootView.findViewById(R.id.fragment_add_wallate_etAmount);
        tvMyWallate = (TextView) rootView.findViewById(R.id.fragment_add_wallate_tvMyWallate);
        btnSubmit = (Button) rootView.findViewById(R.id.fragment_add_wallate_btnSubmit);


        String previesAmount="0";

        int prevesTempWallate = AutumobileAplication.getmInstance().getSharedPreferences().getInt(getString((R.string.preferances_temp_wallate)), 0);
        previesAmount = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_wallate)), "");

        if (prevesTempWallate > 0)
        {

            prevesTempWallate = prevesTempWallate + Integer.parseInt(previesAmount);
            AutumobileAplication.getmInstance().savePreferenceDataInt(getString(R.string.preferances_wallate), prevesTempWallate);
            AutumobileAplication.getmInstance().savePreferenceDataInt(getString(R.string.preferances_temp_wallate), 0);
            tvMyWallate.setText("₹" + prevesTempWallate);
        }
        else
        {
            tvMyWallate.setText("₹" + previesAmount);
        }





        btnSubmit.setOnClickListener(this);

    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_add_wallate), false);

    }


    @Override
    public void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        CropHelper.clearCacheDir();
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity());

        if (v == btnSubmit) {

            submitForm();
        }
    }

    /**
     * Validating form
     */

    private void submitForm() {

        wallateAmount = etAmount.getText().toString().trim();


        if (wallateAmount.isEmpty()) {
            validateField(llContainer, etAmount, getString(R.string.val_enter_amount));
        } else {

            payMentIntegratio();
        }
    }

    private void payMentIntegratio() {

        final String userName = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userName)), "");
        final String email = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userEmail)), "");
        final String phone = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userPhone)), "");


        Intent bundle = new Intent(getActivity(), PayMentGateWay.class);
        bundle.putExtra("FIRST_NAME", "" + userName);
        bundle.putExtra("EMAIL_ADDRESS", "" + email);
        bundle.putExtra("PHONE_NUMBER", phone);
        bundle.putExtra("AMOUNT", wallateAmount);
        startActivityForResult(bundle, 100);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult", "requestCode==" + requestCode + "==resultCode=" + resultCode);

        if (requestCode == 100 && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                final String success = bundle.getString("success");

                if (success.equals("1")) {

                    paymentId = bundle.getString("paymentId");

                    if (paymentId != null && !paymentId.isEmpty())
                    {
                        int prevesWallate = AutumobileAplication.getmInstance().getSharedPreferences().getInt(getString((R.string.preferances_temp_wallate)), 0);

                        if (prevesWallate > 0) {
                            prevesWallate = prevesWallate + Integer.parseInt(wallateAmount);
                            AutumobileAplication.getmInstance().savePreferenceDataInt(getString(R.string.preferances_temp_wallate), prevesWallate);
                        } else {
                            AutumobileAplication.getmInstance().savePreferenceDataInt(getString(R.string.preferances_temp_wallate), Integer.parseInt(wallateAmount));
                        }


                        addWallate();
                    }


                } else {

                    Utils.snackbar(llContainer, "Something is wrong please try again", true, getActivity());
                }
            }
        } else {
            Utils.snackbar(llContainer, "Something is wrong please try again.", true, getActivity());
        }
    }

    public void addWallate() {

        if (Utils.isOnline(getActivity(), true)) {
            if (addWallateAsyncTask != null && addWallateAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                addWallateAsyncTask.execute();
            } else if (addWallateAsyncTask == null || addWallateAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                addWallateAsyncTask = new AddWallateAsyncTask();
                addWallateAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }


    private class AddWallateAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSAddWallate wsBookService;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setMessage(getString(R.string.please_wait));
            progress.setCancelable(false);
            progress.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                wsBookService = new WSAddWallate(getActivity());
                wsBookService.executeService(getActivity(), paymentId, wallateAmount, "CREDITED");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            if (wsBookService.isSuccess())
            {
                Utils.snackbar(llContainer, "" + wsBookService.getMessage(), true, getActivity());
                AutumobileAplication.getmInstance().savePreferenceDataInt(getString(R.string.preferances_temp_wallate), 0);
                String previesAmount = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_wallate)), "");
                tvMyWallate.setText("₹" + previesAmount);
                etAmount.setText("");

            } else {

                Utils.snackbar(llContainer, "" + wsBookService.getMessage(), true, getActivity());

            }

        }

    }


}
