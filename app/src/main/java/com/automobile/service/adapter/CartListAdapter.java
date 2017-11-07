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
import com.automobile.service.fragment.CartListFragment;
import com.automobile.service.model.product.ProductModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<ProductModel> productModelList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private CartListFragment cartListFragment;
    private boolean isLoading;
    private int lastPosition = -1;


    public CartListAdapter(CartListFragment productListFragment, Context context, List<ProductModel> items) {
        this.productModelList = items;
        this.cartListFragment = productListFragment;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_list, parent, false);
        v.setOnClickListener(this);
        return new ViewHolderData(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ViewHolderData) holder).bindData(productModelList.get(position), position);

    }

    public void addRecord(ArrayList<ProductModel> sleeptipsModelArrayList) {
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
                    onItemClickListener.onItemClick(v, (ProductModel) v.getTag());
                }
            }, 200);
        }
    }


    public interface OnItemClickListener {

        void onItemClick(View view, ProductModel viewModel);

    }

    protected class ViewHolderData extends RecyclerView.ViewHolder {


        private CardView cvContainer;
        private CustomTextView tvTitle;
        private CustomTextView tvDes;
        private CustomTextView tvPrise;
        private CustomTextView tvPlus;
        private CustomTextView tvMines;
        private CustomTextView tvREmove;
        private ImageView ivProductImg;
        private CustomTextView tvQty;

        public ViewHolderData(View itemView) {
            super(itemView);

            cvContainer = (CardView) itemView.findViewById(R.id.row_cartlist_cvContainer);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvTitle);
            tvPrise = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvPrise);
            tvDes = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvDes);
            tvPlus = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvPlus);
            tvMines = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvMines);
            tvQty = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvQty);
            tvREmove = (CustomTextView) itemView.findViewById(R.id.row_cartlist_tvRemove);
            ivProductImg = (ImageView) itemView.findViewById(R.id.row_cartlist_ivProductImg);


        }

        public void bindData(ProductModel item, final int position) {


            tvTitle.setText("" + item.getProductName());
            tvPrise.setText("₹" + item.getProductPrice());
            tvDes.setText("" + item.getProductDesc());
            tvQty.setText(item.getProductQty() != null ? "" + item.getProductQty() : "1");
            itemView.setTag(item);

            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(mContext).load(item.getProductImage()).placeholder(R.drawable.ic_placeholder).centerCrop().into(ivProductImg);
            }

            tvREmove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartListFragment.removeListViwItem(position);

                }
            });

            tvPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartListFragment.addQty(position, tvQty.getText().toString());

                }
            });

            tvMines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartListFragment.minesQty(position, tvQty.getText().toString());
                }
            });

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
        // holder.itemView.clearAnimation();
    }


}
