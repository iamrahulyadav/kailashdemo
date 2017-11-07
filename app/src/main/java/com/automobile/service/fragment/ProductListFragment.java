package com.automobile.service.fragment;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.adapter.ProductListAdapter;
import com.automobile.service.comman.Utils2;
import com.automobile.service.model.product.ProductModel;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSGetProductData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProductListFragment extends BaseFragment implements ProductListAdapter.OnItemClickListener {


    //Declaration
    private LinearLayout llContainer;
    private LinearLayout llProgress;
    private RecyclerView rvSleeptipsList;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout llLoadMoreProgress;
    private RelativeLayout rlEmpty;


    private SearchView searchView;
    private String searchKeyWord;
    private boolean isFromSearch = false;
    private boolean isDataLoadingFromServer = false;


    private GetGuidelineAsyncTask getSleeptipsAsyncTask;
    private ArrayList<ProductModel> modelArrayList = new ArrayList<>();
    private ProductListAdapter productListAdapter;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;
    private int lastVisibleItem;
    private int totalItemCount;
    private int visibleThreshold = 1;
    private int pageItemCount = 0;
    private int pageIndex = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_productlist, container, false);
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

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_productlist_llContainer);
        llLoadMoreProgress = (LinearLayout) rootView.findViewById(R.id.fragment_productlist_llLoadMoreProgress);
        rvSleeptipsList = (RecyclerView) rootView.findViewById(R.id.fragment_productlist_rvProductList);
        rlEmpty = (RelativeLayout) rootView.findViewById(R.id.fragment_rlEmpty);
        llProgress = (LinearLayout) rootView.findViewById(R.id.fragment_productlist_llProgress);

        rvSleeptipsList.setHasFixedSize(true);

        setUpAdapater(modelArrayList);
        getProductListData(false);


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_product), false);

    }


    public void getProductListData(final boolean isLoadmore) {

        if (Utils.isOnline(getActivity(), true)) {
            if (getSleeptipsAsyncTask != null && getSleeptipsAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                getSleeptipsAsyncTask.execute();
            } else if (getSleeptipsAsyncTask == null || getSleeptipsAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                getSleeptipsAsyncTask = new GetGuidelineAsyncTask(isLoadmore);
                getSleeptipsAsyncTask.execute();
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

        Utils.hideKeyboard(getActivity());
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.bdl_product_id), viewModel.getProductId());
        productDetailsFragment.setArguments(bundle);
        Utils.addNextFragment(getActivity(), productDetailsFragment, ProductListFragment.this, false);
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
        final MenuItem menuItem = menu.findItem(R.id.menu_search);


        String cart_p_id = AutumobileAplication.getmInstance().getSharedPreferences().getString(getString((R.string.preferances_cartids)), "");

        if(!cart_p_id.isEmpty())
        {
            List<String> elephantList = Arrays.asList(cart_p_id.split(","));

            if(elephantList.size() > 0)
            {
                MenuItem item = menu.findItem(R.id.menu_cart);
                LayerDrawable icon = (LayerDrawable) item.getIcon();
                Utils2.setBadgeCount(getActivity(), icon, elephantList.size());
            }

        }




        searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.actionbar_search_hint));
        searchView.setGravity(Gravity.END);
        final LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setStartDelay(LayoutTransition.APPEARING, 100);
        searchBar.setLayoutTransition(layoutTransition);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchKeyWord = "";
                return true;
            }
        });


        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchKeyWord = "";
                isFromSearch = false;

                if (Utils.isNetworkAvailable(getActivity())) {
                    modelArrayList.clear();
                    pageIndex=0;
                    getProductListData(false);
                } else {
                    Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());
                }

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (!isDataLoadingFromServer) {
                    isFromSearch = true;
                    searchKeyWord = query;
                    //Clear list data before search
                    pageIndex=0;
                    modelArrayList.clear();
                    getProductListData(false);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (isFromSearch) {
            menuItem.expandActionView();
            if (!TextUtils.isEmpty(searchKeyWord))
                searchView.setQuery(searchKeyWord, false);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cart)
        {
            if (!isDataLoadingFromServer) {

                Utils.hideKeyboard(getActivity());
                CartListFragment addClaimFragment = new CartListFragment();
                addClaimFragment.setTargetFragment(ProductListFragment.this,9999);
                Utils.addNextFragment(getActivity(), addClaimFragment, ProductListFragment.this, false);

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvSleeptipsList.setLayoutManager(mLayoutManager);

        productListAdapter = new ProductListAdapter(ProductListFragment.this, getActivity(), guidelineModelArrayList);
        productListAdapter.setOnItemClickListener(this);
        rvSleeptipsList.setAdapter(productListAdapter);

        rvSleeptipsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount > 0) {

                    if (!productListAdapter.isLoading() && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                        if (pageItemCount > totalItemCount) {
                            if (getSleeptipsAsyncTask != null && getSleeptipsAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                                productListAdapter.setLoading();
                                llLoadMoreProgress.setVisibility(View.VISIBLE);
                                pageIndex++;
                                getProductListData(true);
                            }
                        }
                    }
                }
            }
        });


    }


    @Override
    public void onClick(View v) {


    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (getSleeptipsAsyncTask != null && getSleeptipsAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            getSleeptipsAsyncTask.cancel(true);
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


    private class GetGuidelineAsyncTask extends AsyncTask<Void, Void, Void> {
        private WSGetProductData wsGetProductData;
        private ArrayList<ProductModel> productModelArrayList;
        private boolean isLoadmore;

        public GetGuidelineAsyncTask(boolean loadMore) {
            isLoadmore = loadMore;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productModelArrayList = new ArrayList<>();

            llProgress.setVisibility(isLoadmore ? View.GONE : View.VISIBLE);

            if (searchView != null) {
                searchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }

                }, 300);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                isDataLoadingFromServer = true;
                wsGetProductData = new WSGetProductData(getActivity());
                productModelArrayList = wsGetProductData.executeService(getActivity(), getBundel());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            isDataLoadingFromServer = false;

            if (wsGetProductData.isSuccess())
            {
                pageItemCount = Integer.parseInt(wsGetProductData.getListCount());


                if (productListAdapter != null && productListAdapter.isLoading()) {
                    llLoadMoreProgress.setVisibility(View.GONE);
                    productListAdapter.setLoaded();

                }

                if (productModelArrayList != null && productModelArrayList.size() > 0) {

                    if (productListAdapter != null) {
                        modelArrayList.addAll(productModelArrayList);
                        productListAdapter.addRecord(modelArrayList);
                        productListAdapter.notifyDataSetChanged();
                    } else {
                        modelArrayList.addAll(productModelArrayList);
                        setUpAdapater(modelArrayList);
                    }
                    llProgress.setVisibility(View.GONE);

                    rlEmpty.setVisibility(View.GONE);
                    rvSleeptipsList.setVisibility(View.VISIBLE);

                } else {

                    if (modelArrayList.size() < 0) {
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

    private String getFilterUrl() {
        String url = "";
        if (searchKeyWord != null && !searchKeyWord.isEmpty()) {

            url = ParamsConstans.PARAM_SEARCH + "=" + searchKeyWord + "&" + ParamsConstans.PARAM_PAGE_INDEX + "=" + pageIndex;
        } else {
            url = ParamsConstans.PARAM_PAGE_INDEX + "=" + pageIndex;
        }
        return url;
    }


    private Bundle getBundel() {

        Bundle bundle = new Bundle();
        bundle.putString(ParamsConstans.PARAM_SEARCH, searchKeyWord);
        bundle.putString(ParamsConstans.PARAM_PAGE_INDEX, String.valueOf(pageIndex));
        return bundle;
    }


}
