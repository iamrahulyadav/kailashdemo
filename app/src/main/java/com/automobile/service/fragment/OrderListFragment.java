package com.automobile.service.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.adapter.OrderListAdapter;
import com.automobile.service.model.Order.OrderListModel;
import com.automobile.service.util.ParamsConstans;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;
import com.automobile.service.webservice.WSGetOrderListData;

import java.util.ArrayList;


public class OrderListFragment extends BaseFragment implements OrderListAdapter.OnItemClickListener {


    //Declaration
    private LinearLayout llContainer;
    private RecyclerView rvSleeptipsList;
    private RelativeLayout rlEmpty;


    private GetOrderListDataAsyncTask getOrderListDataAsyncTask;
    private ArrayList<OrderListModel> orderListModelArrayList = new ArrayList<>();
    private OrderListAdapter orderListAdapter;

    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_orderlist, container, false);
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

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_orderlist_llContainer);
        rvSleeptipsList = (RecyclerView) rootView.findViewById(R.id.fragment_orderlist_rvProductList);
        rlEmpty = (RelativeLayout) rootView.findViewById(R.id.fragment_rlEmpty);

        rvSleeptipsList.setHasFixedSize(true);

        setUpAdapater(orderListModelArrayList);
        getOrderListData();


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_myorder), false);

    }


    public void getOrderListData() {

        if (Utils.isOnline(getActivity(), true)) {
            if (getOrderListDataAsyncTask != null && getOrderListDataAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                getOrderListDataAsyncTask.execute();
            } else if (getOrderListDataAsyncTask == null || getOrderListDataAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                getOrderListDataAsyncTask = new GetOrderListDataAsyncTask();
                getOrderListDataAsyncTask.execute();
            }

        } else {
            emptyView();
            Utils.snackbar(llContainer, "" + getString(R.string.check_connection), true, getActivity());

        }
    }

    @Override
    public void onItemClick(View view, OrderListModel viewModel) {

        /**
         * Logic to Prevent the Launch of the Fragment Twice if User makes
         * the Tap(Click) very Fast.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Utils.hideKeyboard(getActivity());
//        DetailsVideoPdfWebviewImgFragment detailsVideoPdfWebviewImgFragment = new DetailsVideoPdfWebviewImgFragment();
//        detailsVideoPdfWebviewImgFragment.setTargetFragment(CategoryListFragment.this, 0);
//        Bundle bundle = new Bundle();
//        bundle.putString(getString(R.string.type), viewModel.getLg_content_type());
//        bundle.putString(getString(R.string.value), viewModel.getLg_content_type().equals(getString(R.string.lbl_html)) ? viewModel.getLg_content_description() : viewModel.getLg_image());
//        bundle.putString(getString(R.string.title_val), viewModel.getLg_title());
//        detailsVideoPdfWebviewImgFragment.setArguments(bundle);
//        Utils.addNextFragment(getActivity(), detailsVideoPdfWebviewImgFragment, CategoryListFragment.this, false);
    }


    private void emptyView() {

        rlEmpty.setVisibility(View.VISIBLE);
        rvSleeptipsList.setVisibility(View.GONE);
    }

    private void setUpAdapater(final ArrayList<OrderListModel> guidelineModelArrayList) {


        rvSleeptipsList.removeAllViews();
        rvSleeptipsList.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvSleeptipsList.setLayoutManager(mLayoutManager);

        orderListAdapter = new OrderListAdapter(OrderListFragment.this, getActivity(), guidelineModelArrayList);
        orderListAdapter.setOnItemClickListener(this);
        rvSleeptipsList.setAdapter(orderListAdapter);


    }


    @Override
    public void onClick(View v) {


    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (getOrderListDataAsyncTask != null && getOrderListDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            getOrderListDataAsyncTask.cancel(true);
        }

    }


    private class GetOrderListDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private WSGetOrderListData wsGetNotificationList;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            orderListModelArrayList = new ArrayList<>();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                wsGetNotificationList = new WSGetOrderListData(getActivity());
                orderListModelArrayList = wsGetNotificationList.executeService(getActivity(), getBundel());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (wsGetNotificationList.isSuccess()) {

                if (orderListModelArrayList != null && orderListModelArrayList.size() > 0) {

                    rlEmpty.setVisibility(View.GONE);
                    rvSleeptipsList.setVisibility(View.VISIBLE);

                    if (orderListAdapter != null) {
                        orderListAdapter.addRecord(orderListModelArrayList);
                        orderListAdapter.notifyDataSetChanged();
                    } else {
                        setUpAdapater(orderListModelArrayList);
                    }

                } else {

                    if (orderListModelArrayList.size() < 0) {

                        Utils.snackbar(llContainer, "" + getString(R.string.no_order_found), true, getActivity());
                        emptyView();
                    }
                }
            } else {
                Utils.snackbar(llContainer, "" + wsGetNotificationList.getMessage(), true, getActivity());
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
