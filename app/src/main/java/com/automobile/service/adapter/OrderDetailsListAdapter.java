package com.automobile.service.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomTextView;
import com.automobile.service.model.Order.OrderProductDetailModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<OrderProductDetailModel> productModelList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;


    public OrderDetailsListAdapter(Context context, List<OrderProductDetailModel> items) {
        this.productModelList = items;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_details, parent, false);
        v.setOnClickListener(this);
        return new ViewHolderData(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ViewHolderData) holder).bindData(productModelList.get(position), position);

    }

    public void addRecord(ArrayList<OrderProductDetailModel> sleeptipsModelArrayList) {
        productModelList = sleeptipsModelArrayList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(v, (OrderProductDetailModel) v.getTag());
                }
            }, 200);
        }
    }


    public interface OnItemClickListener {

        void onItemClick(View view, OrderProductDetailModel viewModel);

    }

    protected class ViewHolderData extends RecyclerView.ViewHolder {


        private CardView cvContainer;
        private CustomTextView tvTitle;
        private ImageView ivProduct;
        private CustomTextView tvQty;
        private CustomTextView tvPice;


        public ViewHolderData(View itemView) {
            super(itemView);

            cvContainer = (CardView) itemView.findViewById(R.id.row_order_details_cvContainer);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.row_order_details_tvTitle);
             tvQty = (CustomTextView) itemView.findViewById(R.id.row_order_details_tvQty);
            tvPice = (CustomTextView) itemView.findViewById(R.id.row_order_details_tvPrise);
            ivProduct = (ImageView) itemView.findViewById(R.id.row_order_details_ivProductImg);


        }

        public void bindData(OrderProductDetailModel item, final int position) {


            tvTitle.setText("" + item.getProductName());
            //tvQty.setText("" + item.getProductName());
            tvPice.setText("₹" + item.getProductPrice());
            tvQty.setText("quantity: " + item.getQuantity());

            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(mContext).load(item.getProductImage()).placeholder(R.drawable.ic_placeholder).centerCrop().into(ivProduct);
            }

            itemView.setTag(item);


        }
    }


    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        // holder.itemView.clearAnimation();
    }


}
