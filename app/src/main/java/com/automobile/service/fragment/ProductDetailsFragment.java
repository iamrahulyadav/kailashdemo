package com.automobile.service.fragment;

import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.adapter.ProductDetailsMultiImagePagerAdapter;
import com.automobile.service.comman.Utils2;
import com.automobile.service.model.product.MultipleImageProductModel;
import com.automobile.service.model.product.ProductDetailsModels;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSGetProductDetails;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import eu.fiskur.simpleviewpager.ImageURLLoader;


public class ProductDetailsFragment extends BaseFragment {

    private LinearLayout llContainer;
    private LinearLayout llInfo;
    private eu.fiskur.simpleviewpager.SimpleViewPager vpProductImages;
    private TextView tvProductName;
    private TextView tvPrice;
    private TextView tvDes;
    private Button btnAddCart;
    private Button btnBuyNow;

    private LinearLayout llProgress;
    private RelativeLayout rlEmpty;


    private String productId;
    private Bundle bundle;
    private int currentImgPage = 0;
    private Handler handlerMultiImg;


    private ProductDetailsModels productDetailsModels;
    private ProductDetailsAsyncTask productDetailsAsyncTask;
    private ProductDetailsMultiImagePagerAdapter productDetailsMultiImagePagerAdapter;

    private static int MAX_CLICK_INTERVAL = 3000;
    private long mLastClickTime = 0;
    private String cart_p_id = "";
    private Menu menu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);
        initComponents(rootView);
        initToolbar();
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getArguments();

        if (bundle != null) {
            productId = bundle.getString(getString(R.string.bdl_product_id));

        } else {
            getFragmentManager().popBackStack();
        }


    }

    @Override
    public void initComponents(View rootView) {

        cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");


        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_product_detail_llContainer);
        llInfo = (LinearLayout) rootView.findViewById(R.id.fragment_product_detail_llInfo);
        rlEmpty = (RelativeLayout) rootView.findViewById(R.id.fragment_rlEmpty);
        vpProductImages = (eu.fiskur.simpleviewpager.SimpleViewPager) rootView.findViewById(R.id.fragment_product_detail_vpProductImages);
        tvProductName = (TextView) rootView.findViewById(R.id.fragment_product_detail_tvProductName);
        tvPrice = (TextView) rootView.findViewById(R.id.fragment_product_detail_tvPriceName);
        tvDes = (TextView) rootView.findViewById(R.id.fragment_product_detail_tvDescription);

        llProgress = (LinearLayout) rootView.findViewById(R.id.fragment_product_llProgress);
        btnAddCart = (Button) rootView.findViewById(R.id.fragment_product_detail_btnAddCart);
        btnBuyNow = (Button) rootView.findViewById(R.id.fragment_product_detail_btnBuyNow);


        btnAddCart.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);


        if (cart_p_id.contains(productId)) {
            btnAddCart.setText(R.string.go_to_cart);
        } else {
            btnAddCart.setText(getString(R.string.add_to_cart));
        }


        getProductDetails();


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_product_details), true);

    }


    /**
     * Option menu for Searchview
     *
     * @param menu
     * @param inflater
     */

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        this.menu = menu;
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(false);

        updateBadgeInActionbar();


    }

    private void updateBadgeInActionbar() {

        String cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

        if (!cart_p_id.isEmpty()) {
            List<String> elephantList = Arrays.asList(cart_p_id.split(","));

            if (elephantList.size() > 0) {
                MenuItem item = menu.findItem(R.id.menu_cart);
                LayerDrawable icon = (LayerDrawable) item.getIcon();
                Utils2.setBadgeCount(getActivity(), icon, elephantList.size());
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cart) {

            Utils.hideKeyboard(getActivity());
            CartListFragment addClaimFragment = new CartListFragment();
            Utils.addNextFragment(getActivity(), addClaimFragment, ProductDetailsFragment.this, false);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getProductDetails() {

        if (Utils.isOnline(getActivity(), true)) {
            if (productDetailsAsyncTask != null && productDetailsAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                productDetailsAsyncTask.execute();
            } else if (productDetailsAsyncTask == null || productDetailsAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                productDetailsAsyncTask = new ProductDetailsAsyncTask();
                productDetailsAsyncTask.execute();
            }

        } else {

            rlEmpty.setVisibility(View.VISIBLE);
            llProgress.setVisibility(View.GONE);
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }




    private class ProductDetailsAsyncTask extends AsyncTask<Void, Void, Void> {


        private WSGetProductDetails wsGetProductDetailsData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            llProgress.setVisibility(View.VISIBLE);
            productDetailsModels = new ProductDetailsModels();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                wsGetProductDetailsData = new WSGetProductDetails(getActivity());
                productDetailsModels = wsGetProductDetailsData.executeService(getActivity(), getBundel());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            llProgress.setVisibility(View.GONE);

            if (wsGetProductDetailsData.isSuccess()) {

                llInfo.setVisibility(View.VISIBLE);
                rlEmpty.setVisibility(View.GONE);

                tvProductName.setText(productDetailsModels.getProductName());
                tvPrice.setText("₹"+productDetailsModels.getProductPrice());

                if(productDetailsModels.getProductHtml() !=null && !productDetailsModels.getProductHtml().isEmpty())
                tvDes.setText(Html.fromHtml(productDetailsModels.getProductDesc()));

                handlerMultiImg = new Handler();
                handlerMultiImg.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        setUpViewpager();
                    }
                }, 1000);


            } else {

                rlEmpty.setVisibility(View.VISIBLE);
                Utils.snackbar(llContainer, "" + wsGetProductDetailsData.getMessage(), true, getActivity());

            }

        }

    }

    private void setUpViewpager() {


        List<String> imgARr= productDetailsModels.getProductImageAr();
        String[] stockArr = new String[imgARr.size()];
        stockArr = imgARr.toArray(stockArr);

        vpProductImages.setImageUrls(stockArr, new ImageURLLoader() {
            @Override
            public void loadImage(ImageView view, String url) {
                Glide.with(getActivity()).load(url).placeholder(R.drawable.ic_placeholder).centerCrop().into(view);
            }
        });

        //optional:
        int indicatorColor = Color.parseColor("#ffffff");
        int selectedIndicatorColor = Color.parseColor("#FF7043");
        vpProductImages.showIndicator(indicatorColor, selectedIndicatorColor);

    }

    private Bundle getBundel() {

        Bundle bundle = new Bundle();
        bundle.putString(ParamsConstans.PARAM_PRODUCT_ID, productId);
        return bundle;
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


        if (v == btnAddCart) {

            cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

            if (cart_p_id.contains(productId)) {

                CartListFragment cartListFragment = new CartListFragment();
                Utils.addNextFragment(getActivity(), cartListFragment, ProductDetailsFragment.this, false);


            } else {
                if (cart_p_id.isEmpty()) {
                    cart_p_id = productId;
                } else {
                    cart_p_id = cart_p_id + "," + productId;
                }
                AutumobileAplication.getmInstance().savePreferenceDataString(getString(R.string.preferances_cartids), cart_p_id);
                btnAddCart.setText(R.string.go_to_cart);
                updateBadgeInActionbar();
            }


        } else if (v == btnBuyNow) {

            cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

            if (cart_p_id.contains(productId)) {

                CartListFragment cartListFragment = new CartListFragment();
                Utils.addNextFragment(getActivity(), cartListFragment, ProductDetailsFragment.this, false);


            } else {
                if (cart_p_id.isEmpty()) {
                    cart_p_id = productId;
                } else {
                    cart_p_id = cart_p_id + "," + productId;
                }
                AutumobileAplication.getmInstance().savePreferenceDataString(getString(R.string.preferances_cartids), cart_p_id);

                CartListFragment cartListFragment = new CartListFragment();
                Utils.addNextFragment(getActivity(), cartListFragment, ProductDetailsFragment.this, false);
            }

        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden) {
            initToolbar();
            setHasOptionsMenu(true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (handlerMultiImg != null) {
            handlerMultiImg.removeCallbacksAndMessages(null);
        }

        if (productDetailsAsyncTask != null && productDetailsAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            productDetailsAsyncTask.cancel(true);
        }


    }


}
