package com.automobile.service.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.payumoney.PayMentGateWay;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSSubmitOrder;


public class ConfirmOrderFragment extends BaseFragment {


    //Declaration
    private LinearLayout llContainer;
    private RelativeLayout rlPayuMoney;
    private RelativeLayout rlCod;
    private RelativeLayout rlWallate;
    private EditText etAddress;
    private TextView tvTotalPrice;
    private TextView tvPriceItemCount;
    private TextView tvItemAmount;
    private TextView tvPayuMoney;
    private TextView tvCod;
    private TextView tvWallate;
    private Button btnContinue;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;

    private String address;
    private String paymentMethod = "1";   //1=payumoney,2=cod
    private String totalPrice;
    private String productArr;
    private String totalitems;
    private String paymentId = "";


    private Bundle bundle;
    private ProgressDialog progress;
    private int myWallate = 0;
    private ConfirmOrderServiceAsyncTask confirmOrderServiceAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        initToolbar();
        initComponents(rootView);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();

        if (bundle != null) {
            totalitems = bundle.getString("bdl_totalItem");
            totalPrice = bundle.getString("bdl_totalPrice");
            productArr = bundle.getString("bdl_product_arr");

            Log.d("onCreate", "totalitems==" + totalitems + "==totalPrice==" + totalPrice + "==productArr==" + productArr);


        }


    }

    @Override
    public void initComponents(View rootView) {


        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_confirm_order_llContainer);
        rlCod = (RelativeLayout) rootView.findViewById(R.id.fragment_confirm_order_rlCod);
        rlPayuMoney = (RelativeLayout) rootView.findViewById(R.id.fragment_confirm_order_rlPayuMoney);
        rlWallate = (RelativeLayout) rootView.findViewById(R.id.fragment_confirm_order_rlWallate);
        tvTotalPrice = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvTotalPrice);
        tvPriceItemCount = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvPriceItemCount);
        tvItemAmount = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvAmount);
        tvPayuMoney = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvPayuMoney);
        tvCod = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvCod);
        tvWallate = (TextView) rootView.findViewById(R.id.fragment_confirm_order_tvWallate);
        etAddress = (EditText) rootView.findViewById(R.id.fragment_confirm_order_etAddress);
        btnContinue = (Button) rootView.findViewById(R.id.fragment_confirm_order_btnContinue);


        btnContinue.setOnClickListener(this);
        rlPayuMoney.setOnClickListener(this);
        rlCod.setOnClickListener(this);


        tvPriceItemCount.setText("Price(" + totalitems + " item)");
        tvItemAmount.setText("" + totalPrice);
        tvTotalPrice.setText("" + totalPrice);

        address = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_useAddress)), "");
        etAddress.setText("" + address);

        tvCod.setBackgroundResource(R.drawable.radio_btn);
        tvWallate.setBackgroundResource(R.drawable.radio_btn);
        tvPayuMoney.setBackgroundResource(R.drawable.radio_btn_selected);


        final String previesAmount = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_wallate)), "");

        if (previesAmount != null && !previesAmount.isEmpty()) {
            int mywallatePre = Integer.parseInt(previesAmount);

            if (mywallatePre > 0) {
                tvWallate.setBackgroundResource(R.drawable.radio_btn_selected);
                myWallate = mywallatePre;
                setCheckMark(false);
                paymentMethod = "2";
                paymentId = "0";

            }

        }


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_confirm_order), true);

    }


    @Override
    public void onClick(View v) {


        Utils.hideKeyboard(getActivity());


        if (v == btnContinue) {

            /**
             * Logic to Prevent the Launch of the Fragment Twice if User makes
             * the Tap(Click) very Fast.
             */
            if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            address = etAddress.getText().toString().trim();

            if (address.isEmpty()) {
                validateField(llContainer, etAddress, getString(R.string.val_enter_address));
            } else {
                confirmOrder();
            }

        } else if (v == rlPayuMoney) {
            paymentMethod = "1";
            setCheckMark(true);

        } else if (v == rlCod) {
            paymentMethod = "2";
            paymentId = "0";
            setCheckMark(false);

        }

    }

    private void setCheckMark(final boolean isPayu) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isPayu) {
                    tvCod.setBackgroundResource(R.drawable.radio_btn);
                    tvPayuMoney.setBackgroundResource(R.drawable.radio_btn_selected);
                } else {
                    tvCod.setBackgroundResource(R.drawable.radio_btn_selected);
                    tvPayuMoney.setBackgroundResource(R.drawable.radio_btn);
                }
            }
        });


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

                    if (confirmOrderServiceAsyncTask != null && confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                        confirmOrderServiceAsyncTask.execute();
                    } else if (confirmOrderServiceAsyncTask == null || confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                        confirmOrderServiceAsyncTask = new ConfirmOrderServiceAsyncTask();
                        confirmOrderServiceAsyncTask.execute();
                    }

                } else {

                    Utils.snackbar(llContainer, "Something is wrong please try again", true, getActivity());
                }
            }
        } else {
            Utils.snackbar(llContainer, "Something is wrong please try again.", true, getActivity());
        }
    }


    public void confirmOrder() {

        if (Utils.isOnline(getActivity(), true)) {

            int totalPrs = Integer.parseInt(totalPrice.substring(1, totalPrice.length()));

            if (totalPrs > myWallate) {

                if (myWallate > 0) {
                    totalPrs = totalPrs - myWallate;
                }

                if (paymentMethod.equals("1")) {


                    final String userName = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userName)), "");
                    final String email = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userEmail)), "");
                    final String phone = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userPhone)), "");


                    Intent bundle = new Intent(getActivity(), PayMentGateWay.class);
                    bundle.putExtra("FIRST_NAME", "" + userName);
                    bundle.putExtra("EMAIL_ADDRESS", "" + email);
                    bundle.putExtra("PHONE_NUMBER", phone);
                    bundle.putExtra("AMOUNT", "" + totalPrs);
                    startActivityForResult(bundle, 100);

                } else {


                    if (confirmOrderServiceAsyncTask != null && confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                        confirmOrderServiceAsyncTask.execute();
                    } else if (confirmOrderServiceAsyncTask == null || confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                        confirmOrderServiceAsyncTask = new ConfirmOrderServiceAsyncTask();
                        confirmOrderServiceAsyncTask.execute();
                    }

                }
            } else {


                myWallate = myWallate - totalPrs;


                if (confirmOrderServiceAsyncTask != null && confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                    confirmOrderServiceAsyncTask.execute();
                } else if (confirmOrderServiceAsyncTask == null || confirmOrderServiceAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                    confirmOrderServiceAsyncTask = new ConfirmOrderServiceAsyncTask();
                    confirmOrderServiceAsyncTask.execute();
                }
            }


        } else {
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }


    private class ConfirmOrderServiceAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSSubmitOrder wsSubmitOrder;

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

                wsSubmitOrder = new WSSubmitOrder(getActivity());
                if (totalPrice.contains("₹")) {
                    totalPrice = totalPrice.substring(1, totalPrice.length());
                }

                wsSubmitOrder.executeService(getActivity(), totalPrice, totalitems, address, productArr, paymentId, paymentMethod, "" + myWallate);

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

            if (wsSubmitOrder.isSuccess()) {
                Utils.snackbar(llContainer, "" + wsSubmitOrder.getMessage(), true, getActivity());


                ThankYouFragment thankYouFragment = new ThankYouFragment();
                Bundle bundle = new Bundle();
                thankYouFragment.setArguments(bundle);
                Utils.addNextFragment(getActivity(), thankYouFragment, ConfirmOrderFragment.this, false);


            } else {
                Utils.snackbar(llContainer, "" + wsSubmitOrder.getMessage(), true, getActivity());

            }
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();


    }

    /**
     * Called when coming back from next screen
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setHasOptionsMenu(true);
            initToolbar();

        }
    }


}
