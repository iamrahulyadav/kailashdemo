package com.automobile.service.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomTextView;
import com.automobile.service.fragment.OrderListFragment;
import com.automobile.service.model.Order.OrderListModel;

import java.util.ArrayList;
import java.util.List;


public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<OrderListModel> bookServiceModelList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private OrderListFragment orderListFragment;
    private boolean isLoading;
    private int lastPosition = -1;
    private  OrderDetailsListAdapter orderListAdapter;

    public OrderListAdapter(OrderListFragment guidelinesFragment, Context context, List<OrderListModel> items) {
        this.bookServiceModelList = items;
        this.orderListFragment = guidelinesFragment;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_orderlist, parent, false);
        v.setOnClickListener(this);
        return new ViewHolderData(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //final int itemType = getItemViewType(position);
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.up_from_bottom
            );
            holder.itemView.startAnimation(animation);
        }
        lastPosition = position;

        ((ViewHolderData) holder).bindData(bookServiceModelList.get(position), position);

    }

    public void addRecord(ArrayList<OrderListModel> sleeptipsModelArrayList) {
        bookServiceModelList = sleeptipsModelArrayList;
    }


    @Override
    public int getItemCount() {
        return bookServiceModelList.size();
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(v, (OrderListModel) v.getTag());
                }
            }, 200);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, OrderListModel viewModel);

    }

    protected class ViewHolderData extends RecyclerView.ViewHolder {


        private CardView cvContainer;
        private CustomTextView tvOrderNo;
        private CustomTextView tvOrderDate;
        private CustomTextView tvAmount;
        private CustomTextView tvStatus;
        private RecyclerView rvOrderDetails;


        public ViewHolderData(View itemView) {
            super(itemView);

            cvContainer = (CardView) itemView.findViewById(R.id.row_orderlist_cvContainer);
            tvOrderNo = (CustomTextView) itemView.findViewById(R.id.row_orderlist_tvOrderNo);
            tvOrderDate = (CustomTextView) itemView.findViewById(R.id.row_orderlist_tvDate);
            tvAmount = (CustomTextView) itemView.findViewById(R.id.row_orderlist_tvAmount);
            tvStatus = (CustomTextView) itemView.findViewById(R.id.row_orderlist_tvStatus);
            rvOrderDetails = (RecyclerView) itemView.findViewById(R.id.row_orderlist_rvProductList);

            rvOrderDetails.setHasFixedSize(true);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            rvOrderDetails.setLayoutManager(mLayoutManager);

        }

        public void bindData(OrderListModel item, int position) {


            tvOrderNo.setText(mContext.getString(R.string.order_no) + " : " + item.getOrderId());
            tvAmount.setText("₹"+item.getOrderTotalAmount());
            tvOrderDate.setText(item.getOrderCreatedDate());
            tvStatus.setText("Status : " + item.getOrderStatus());

            orderListAdapter = new OrderDetailsListAdapter(mContext,  item.getProductDetails());
            rvOrderDetails.setAdapter(orderListAdapter);


            if (item.getOrderStatus().equals("Pending")) {
                tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else if (item.getOrderStatus().equals("Completed")) {
                tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.darkgreen));
            } else if (item.getOrderStatus().equals("Cancel")) {
                tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            }
            itemView.setTag(item);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setLoading() {
        isLoading = true;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }
}
