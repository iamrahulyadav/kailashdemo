package com.automobile.service.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.adapter.CartListAdapter;
import com.automobile.service.model.product.ProductModel;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSGetCartList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class CartListFragment extends BaseFragment implements CartListAdapter.OnItemClickListener {


    //Declaration
    private LinearLayout llContainer;
    private LinearLayout llProgress;
    private RecyclerView rvSleeptipsList;
    private RelativeLayout rlEmpty;
    private TextView tvTotalPrice;
    private Button btnOrder;

    private GetCartListAsyncTask getCartListAsyncTask;
    private ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
    private CartListAdapter cartListAdapter;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;
    private String productId;
    private String totalPrice;
    private String totalItem;
    private String productArr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_carttlist, container, false);
        initToolbar();
        initComponents(rootView);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initComponents(View rootView) {

        productId = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_cardlist_llContainer);
        rvSleeptipsList = (RecyclerView) rootView.findViewById(R.id.fragment_cardlist_rvProductList);
        rlEmpty = (RelativeLayout) rootView.findViewById(R.id.fragment_rlEmpty);
        llProgress = (LinearLayout) rootView.findViewById(R.id.fragment_cardlist_llProgress);
        tvTotalPrice = (TextView) rootView.findViewById(R.id.fragment_cardlist_tvPriceName);
        btnOrder = (Button) rootView.findViewById(R.id.fragment_cardlist_btnConfirmOrder);
        btnOrder.setOnClickListener(this);
        rvSleeptipsList.setHasFixedSize(true);

        setUpAdapater(productModelArrayList);
        getProductListData(false);


    }


    public void initToolbar() {

        if (getTargetFragment() != null) {
            ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_may_cart), true);
        } else {
            ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_may_cart), false);
        }


    }


    public void getProductListData(final boolean isLoadmore) {

        if (Utils.isOnline(getActivity(), true)) {
            if (getCartListAsyncTask != null && getCartListAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                getCartListAsyncTask.execute();
            } else if (getCartListAsyncTask == null || getCartListAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                getCartListAsyncTask = new GetCartListAsyncTask(isLoadmore);
                getCartListAsyncTask.execute();
            }

        } else {
            llProgress.setVisibility(View.GONE);
            emptyView();
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }

    @Override
    public void onItemClick(View view, ProductModel viewModel) {

        /**
         * Logic to Prevent the Launch of the Fragment Twice if User makes
         * the Tap(Click) very Fast.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }


    private void emptyView() {

        rlEmpty.setVisibility(View.VISIBLE);
        rvSleeptipsList.setVisibility(View.GONE);
    }

    private void setUpAdapater(final ArrayList<ProductModel> guidelineModelArrayList) {

        rlEmpty.setVisibility(View.GONE);
        rvSleeptipsList.setVisibility(View.VISIBLE);

        rvSleeptipsList.removeAllViews();
        rvSleeptipsList.setHasFixedSize(true);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvSleeptipsList.setLayoutManager(mLayoutManager);

        cartListAdapter = new CartListAdapter(CartListFragment.this, getActivity(), guidelineModelArrayList);
        cartListAdapter.setOnItemClickListener(this);
        rvSleeptipsList.setAdapter(cartListAdapter);


    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity());

        /**
         * Logic to Prevent the Launch of the Fragment Twice if User makes
         * the Tap(Click) very Fast.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        totalItem = String.valueOf(productModelArrayList.size());
        totalPrice = tvTotalPrice.getText().toString();
        productArr = getSelectContactsArr(productModelArrayList).toString();


        if (v == btnOrder) {
            ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment();
            Bundle bundle = new Bundle();
            bundle.putString("bdl_totalItem", totalItem);
            bundle.putString("bdl_totalPrice", totalPrice);
            bundle.putString("bdl_product_arr", productArr);
            confirmOrderFragment.setArguments(bundle);
            Utils.addNextFragment(getActivity(), confirmOrderFragment, CartListFragment.this, false);

        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (getCartListAsyncTask != null && getCartListAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            getCartListAsyncTask.cancel(true);
        }

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


    private class GetCartListAsyncTask extends AsyncTask<Void, Void, Void> {
        private WSGetCartList wsGetProductData;

        private boolean isLoadmore;

        public GetCartListAsyncTask(boolean loadMore) {
            isLoadmore = loadMore;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productModelArrayList = new ArrayList<>();

            llProgress.setVisibility(isLoadmore ? View.GONE : View.VISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {


                wsGetProductData = new WSGetCartList(getActivity());
                productModelArrayList = wsGetProductData.executeService(getActivity(), getBundel());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            if (wsGetProductData.isSuccess()) {

                if (productModelArrayList != null && productModelArrayList.size() > 0) {


                    setTotalPrice(productModelArrayList);

                    if (cartListAdapter != null) {
                        cartListAdapter.addRecord(productModelArrayList);
                        cartListAdapter.notifyDataSetChanged();
                    } else {
                        setUpAdapater(productModelArrayList);
                    }
                    llProgress.setVisibility(View.GONE);
                    rlEmpty.setVisibility(View.GONE);
                    rvSleeptipsList.setVisibility(View.VISIBLE);


                } else {

                    if (productModelArrayList.size() < 0) {
                        llProgress.setVisibility(View.GONE);
                        Utils.snackbar(llContainer, "" + getString(R.string.no_servicelist_found), true, getActivity());
                        emptyView();
                    }
                }
            } else {
                llProgress.setVisibility(View.GONE);
                Utils.snackbar(llContainer, "" + wsGetProductData.getMessage(), true, getActivity());
                emptyView();
            }
        }
    }

    private void setTotalPrice(ArrayList<ProductModel> productModelArrayList) {


        int totalPrice = 0;

        for (int i = 0; i < productModelArrayList.size(); i++) {
            totalPrice = totalPrice + Integer.parseInt(productModelArrayList.get(i).getProductPrice());

        }

        tvTotalPrice.setText("₹" + totalPrice);
    }


    public void addQty(int position, final String qty) {
        int totalPrice = Integer.parseInt(tvTotalPrice.getText().toString().substring(1, tvTotalPrice.getText().length()));
        int price = Integer.parseInt(productModelArrayList.get(position).getProductPrice());
        totalPrice = totalPrice + price;
        tvTotalPrice.setText("₹" + totalPrice);

        int tQty = Integer.parseInt(qty) + 1;
        productModelArrayList.get(position).setProductQty(tQty);
        cartListAdapter.addRecord(productModelArrayList);


    }


    public void minesQty(final int position, final String qty) {
        int tQty = Integer.parseInt(qty);

        if (tQty > 1) {
            int totalPrice = Integer.parseInt(tvTotalPrice.getText().toString().substring(1, tvTotalPrice.getText().length()));
            int price = Integer.parseInt(productModelArrayList.get(position).getProductPrice());
            totalPrice = totalPrice - price;
            tvTotalPrice.setText("₹" + totalPrice);


            tQty = tQty - 1;
            productModelArrayList.get(position).setProductQty(tQty);
            cartListAdapter.addRecord(productModelArrayList);
        }


    }

    public void removeListViwItem(int lastPosition) {
        //String removePId = productModelArrayList.get(lastPosition).getProductId();
        productModelArrayList.remove(lastPosition);
        cartListAdapter.notifyDataSetChanged();


        String cartPID = "";

        if (productModelArrayList.size() > 0) {
            for (int i = 0; i < productModelArrayList.size(); i++) {

                if (cartPID.isEmpty()) {
                    cartPID = productModelArrayList.get(i).getProductId();
                } else {

                    cartPID = cartPID + "," + productModelArrayList.get(i).getProductId();
                }
            }
        } else {
            emptyView();
        }

        AutumobileAplication.getmInstance().savePreferenceDataString(getString(R.string.preferances_cartids), cartPID);


        int totalPrice = 0;
        int qty = 0;

        for (int i = 0; i < productModelArrayList.size(); i++) {

            qty = productModelArrayList.get(i).getProductQty();
            int tPrice = Integer.parseInt(productModelArrayList.get(i).getProductPrice()) * qty;
            totalPrice = totalPrice + tPrice;

        }

        tvTotalPrice.setText("₹" + totalPrice);

    }


    private Bundle getBundel() {

        Bundle bundle = new Bundle();
        bundle.putString(ParamsConstans.PARAM_PRODUCT_ID, productId);
        return bundle;
    }


    private JSONArray getSelectContactsArr(ArrayList<ProductModel> productModels) {
        JSONArray selcetArr = new JSONArray();


        for (int i = 0; i < productModels.size(); i++) {

            JSONObject jsonObjectReq = new JSONObject();

            try {

                jsonObjectReq.put("productId", productModels.get(i).getProductId());
                jsonObjectReq.put("qty", productModels.get(i).getProductQty());

                selcetArr.put(jsonObjectReq);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return selcetArr;
    }


}
