package com.automobile.service.fragment;

import android.animation.LayoutTransition;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.adapter.CategoryListAdapter;
import com.automobile.service.comman.SheetLayout;
import com.automobile.service.comman.Utils2;
import com.automobile.service.dailog.DialogServiceDetailsFragment;
import com.automobile.service.model.BookService.BookServiceModel;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSGetBookedServiceData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.fiskur.simpleviewpager.ImageURLLoader;


public class CategoryListFragment extends BaseFragment implements CategoryListAdapter.OnItemClickListener {


    //Declaration
    private LinearLayout llContainer;
    private LinearLayout llProgress;
    private RecyclerView rvSleeptipsList;
    private RelativeLayout rlEmpty;
    private eu.fiskur.simpleviewpager.SimpleViewPager vpProductImages;


    private GetServiceListDataAsyncTask getServiceListDataAsyncTask;
    private ArrayList<BookServiceModel> bookServiceModelArrayList = new ArrayList<>();
    private CategoryListAdapter serviceListAdapter;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_categorylist, container, false);
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

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_servicelist_llContainer);
        llProgress = (LinearLayout) rootView.findViewById(R.id.fragment_servicelist_llProgress);
        rvSleeptipsList = (RecyclerView) rootView.findViewById(R.id.fragment_servicelist_rvProductList);
        rlEmpty = (RelativeLayout) rootView.findViewById(R.id.fragment_rlEmpty);
        vpProductImages = (eu.fiskur.simpleviewpager.SimpleViewPager) rootView.findViewById(R.id.fragment_product_detail_vpProductImages);

        rvSleeptipsList.setHasFixedSize(true);


        setUpAdapater(bookServiceModelArrayList);
        getServiceListData();
        setUpViewpager();


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
        inflater.inflate(R.menu.menu_only_cart, menu);
        MenuItem item = menu.findItem(R.id.menu_cart);

        String cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

        if (!cart_p_id.isEmpty()) {
            List<String> elephantList = Arrays.asList(cart_p_id.split(","));

            if (elephantList.size() > 0) {

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
            addClaimFragment.setTargetFragment(CategoryListFragment.this, 9999);
            Utils.addNextFragment(getActivity(), addClaimFragment, CategoryListFragment.this, false);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_category), false);

    }


    private void setUpViewpager() {

        String[] imgARr = new String[]{
                "http://fiskur.eu/apps/simpleviewpagerdemo/001.jpg",
                "http://fiskur.eu/apps/simpleviewpagerdemo/002.jpg",
                "http://fiskur.eu/apps/simpleviewpagerdemo/003.jpg",
                "http://fiskur.eu/apps/simpleviewpagerdemo/004.jpg",
                "http://fiskur.eu/apps/simpleviewpagerdemo/005.jpg",
        };

        //List<String> imgARr= productDetailsModels.getProductImageAr();
        //String[] stockArr = new String[imgARr.size()];
        // stockArr = imgARr.toArray(stockArr);

        vpProductImages.setImageUrls(imgARr, new ImageURLLoader() {
            @Override
            public void loadImage(ImageView view, String url) {
                Glide.with(getActivity()).load(url).placeholder(R.drawable.ic_placeholder).centerCrop().into(view);
            }
        });

        //optional:
        int indicatorColor = Color.parseColor("#FFFFBB33");
        int selectedIndicatorColor = Color.parseColor("#FF7043");
        vpProductImages.showIndicator(indicatorColor, selectedIndicatorColor);

    }

    public void getServiceListData() {

        if (Utils.isOnline(getActivity(), true)) {
            if (getServiceListDataAsyncTask != null && getServiceListDataAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                getServiceListDataAsyncTask.execute();
            } else if (getServiceListDataAsyncTask == null || getServiceListDataAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                getServiceListDataAsyncTask = new GetServiceListDataAsyncTask();
                getServiceListDataAsyncTask.execute();
            }

        } else {
            emptyView();
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }

    @Override
    public void onItemClick(View view, final BookServiceModel viewModel) {


        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Utils.hideKeyboard(getActivity());


        ProductListFragment productListFragment = new ProductListFragment();
        productListFragment.setTargetFragment(CategoryListFragment.this, 0);
        Bundle bundle = new Bundle();
        productListFragment.setArguments(bundle);
        Utils.addNextFragment(getActivity(), productListFragment, CategoryListFragment.this, false);

    }


    private void emptyView() {

        rlEmpty.setVisibility(View.VISIBLE);
        rvSleeptipsList.setVisibility(View.GONE);
        llProgress.setVisibility(View.GONE);
    }

    private void setUpAdapater(final ArrayList<BookServiceModel> guidelineModelArrayList) {

        rlEmpty.setVisibility(View.GONE);
        rvSleeptipsList.setVisibility(View.VISIBLE);

        rvSleeptipsList.removeAllViews();
        rvSleeptipsList.setHasFixedSize(true);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvSleeptipsList.setLayoutManager(mLayoutManager);



        serviceListAdapter = new CategoryListAdapter(CategoryListFragment.this, getActivity(), guidelineModelArrayList);
        serviceListAdapter.setOnItemClickListener(this);
        rvSleeptipsList.setAdapter(serviceListAdapter);


    }


    @Override
    public void onClick(View v) {


    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (getServiceListDataAsyncTask != null && getServiceListDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            getServiceListDataAsyncTask.cancel(true);
        }

    }


    private class GetServiceListDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private WSGetBookedServiceData wsGetBookedServiceData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                wsGetBookedServiceData = new WSGetBookedServiceData(getActivity());
                bookServiceModelArrayList = wsGetBookedServiceData.executeService(getActivity(), getBundel());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            llProgress.setVisibility(View.GONE);

            if (wsGetBookedServiceData.isSuccess()) {

                if (bookServiceModelArrayList != null && bookServiceModelArrayList.size() > 0) {


                    if (serviceListAdapter != null) {
                        serviceListAdapter.addRecord(bookServiceModelArrayList);
                        serviceListAdapter.notifyDataSetChanged();
                    } else {
                        setUpAdapater(bookServiceModelArrayList);
                    }

                } else {
                    if (CategoryListFragment.this.bookServiceModelArrayList.size() < 0) {
                        Utils.snackbar(llContainer, "" + getString(R.string.no_servicelist_found), true, getActivity());
                        emptyView();
                    }
                }
            } else {
                Utils.snackbar(llContainer, "" + wsGetBookedServiceData.getMessage(), true, getActivity());
                emptyView();
            }
        }
    }


    private Bundle getBundel() {
        final String userId = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_userID)), "");
        Bundle bundle = new Bundle();
        bundle.putString(ParamsConstans.PARAM_USERID, userId);
        return bundle;
    }

    private void setFloatingActionButtonColors(FloatingActionButton fab, int primaryColor, int rippleColor) {
        int[][] states = {
                {android.R.attr.state_enabled},
                {android.R.attr.state_pressed},
        };

        int[] colors = {
                primaryColor,
                rippleColor,
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        fab.setBackgroundTintList(colorStateList);
    }


    /**
     * Called when coming back from next screen
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            initToolbar();
            setHasOptionsMenu(true);

        }
    }
}
