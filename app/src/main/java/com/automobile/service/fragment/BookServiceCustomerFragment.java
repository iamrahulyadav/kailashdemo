package com.automobile.service.fragment;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.automobile.service.R;
import com.automobile.service.imagecrop.CropHelper;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSBookService;


public class BookServiceCustomerFragment extends BaseFragment {

    private LinearLayout llContainer;
    private EditText etName;
    private EditText etAddress;
    private EditText etPhone;
    private Button btnSubmit;

    private String customer_name = "";
    private String customer_phone = "";
    private String customer_address = "";
    private String carNAme = "";
    private String insruction = "";
    private String service_date = "";
    private String service_time = "";
    private String serviceType = "";
    private ProgressDialog progress;
    private Bundle bundle;
    private BookServiceAsyncTask bookServiceAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_bookservice_customer, container, false);
        initComponents(rootView);
        initToolbar();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        bundle = getArguments();

        if (bundle != null) {
            serviceType = bundle.getString(getString(R.string.bdl_service_type));
            carNAme = bundle.getString(getString(R.string.bdl_carname));
            service_date = bundle.getString(getString(R.string.bdl_service_date));
            service_time = bundle.getString(getString(R.string.bdl_service_time));
            insruction = bundle.getString(getString(R.string.bdl_service_instruction));
        }
    }

    @Override
    public void initComponents(View rootView) {

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_bookservice_llContainer);
        etAddress = (EditText) rootView.findViewById(R.id.fragment_bookservice_etAddress);
        etName = (EditText) rootView.findViewById(R.id.fragment_bookservice_etName);
        etPhone = (EditText) rootView.findViewById(R.id.fragment_bookservice_etPhone);
        btnSubmit = (Button) rootView.findViewById(R.id.fragment_bookservice_btnSubmit);

        btnSubmit.setOnClickListener(this);

    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_book_service), true);

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

        customer_name = etName.getText().toString().trim();
        customer_phone = etPhone.getText().toString().trim();
        customer_address = etAddress.getText().toString().trim();

        if (customer_name.isEmpty()) {
            validateField(llContainer, etName, getString(R.string.val_enter_customer_name));
        } else if (customer_phone.isEmpty()) {
            validateField(llContainer, etPhone, getString(R.string.val_enter_customer_phone));
        } else if (customer_address.isEmpty()) {
            validateField(llContainer, etAddress, getString(R.string.val_enter_customer_address));
        } else {

            boolServiceData();
        }
    }

    public void boolServiceData() {

        if (Utils.isOnline(getActivity(), true)) {
            if (bookServiceAsyncTask != null && bookServiceAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                bookServiceAsyncTask.execute();
            } else if (bookServiceAsyncTask == null || bookServiceAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                bookServiceAsyncTask = new BookServiceAsyncTask();
                bookServiceAsyncTask.execute();
            }

        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }


    private class BookServiceAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSBookService wsBookService;

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

                wsBookService = new WSBookService(getActivity());
                wsBookService.executeService(getActivity(), serviceType, carNAme, service_date, service_time, insruction, customer_name, customer_phone, customer_address);

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

            if (wsBookService.isSuccess()) {
                Utils.snackbar(llContainer, "" + wsBookService.getMessage(), true, getActivity());
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            } else {

                Utils.snackbar(llContainer, "" + wsBookService.getMessage(), true, getActivity());

            }

        }

    }


}
