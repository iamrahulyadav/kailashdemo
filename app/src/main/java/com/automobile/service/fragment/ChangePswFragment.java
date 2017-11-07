package com.automobile.service.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.automobile.service.R;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSChangePsw;


public class ChangePswFragment extends BaseFragment {
    private LinearLayout llContainer;
    private EditText etOldPsw;
    private EditText etNewPsw;
    private EditText etConfirmPsw;
    private Button btnSubmit;


    private String oldPsw;
    private String newPsw;
    private String confirmPsw;

    private MenuItem item;
    private ChangePswUpdateAsyncTask changePswUpdateAsyncTask;
    private ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_change_psw, container, false);
        initComponents(rootView);
        initToolbar();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void initComponents(View rootView) {


        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_changepsw__llContainer);
        etOldPsw = (EditText) rootView.findViewById(R.id.fragment_changepsw_etOldPsw);
        etNewPsw = (EditText) rootView.findViewById(R.id.fragment_changepsw_etNewPsw);
        etConfirmPsw = (EditText) rootView.findViewById(R.id.fragment_changepsw_etConfirmPsw);
        btnSubmit = (Button) rootView.findViewById(R.id.fragment_changepsw__btnChangePsw);

        btnSubmit.setOnClickListener(this);


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_change_psw), true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        item = menu.findItem(R.id.menu_left);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {

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


        oldPsw = etOldPsw.getText().toString().trim();
        newPsw = etNewPsw.getText().toString().trim();
        confirmPsw = etConfirmPsw.getText().toString().trim();


        if (oldPsw.isEmpty()) {
            validateField(llContainer, etOldPsw, getString(R.string.val_enter_old_password));
        } else if (newPsw.isEmpty()) {
            validateField(llContainer, etNewPsw, getString(R.string.val_enter_new_password));
        } else if (confirmPsw.isEmpty()) {
            validateField(llContainer, etConfirmPsw, getString(R.string.val_enter_confirm_password));
        } else if (!newPsw.equals(confirmPsw)) {
            validateField(llContainer, etNewPsw, getString(R.string.val_enter_new_confirm_password_not_match));
        } else {
            updateChangePsw();
        }
    }


    public void updateChangePsw() {

        if (Utils.isOnline(getActivity(), true)) {
            if (changePswUpdateAsyncTask != null && changePswUpdateAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                changePswUpdateAsyncTask.execute();
            } else if (changePswUpdateAsyncTask == null || changePswUpdateAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                changePswUpdateAsyncTask = new ChangePswUpdateAsyncTask();
                changePswUpdateAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }


    private class ChangePswUpdateAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSChangePsw wsProfileUpdate;

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

                wsProfileUpdate = new WSChangePsw(getActivity());
                wsProfileUpdate.executeService(getActivity(), oldPsw, newPsw);

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

            if (wsProfileUpdate.isSuccess()) {
                Utils.snackbar(llContainer, "" + wsProfileUpdate.getMessage(), true, getActivity());


            } else {

                Utils.snackbar(llContainer, "" + wsProfileUpdate.getMessage(), true, getActivity());

            }

        }

    }


}
