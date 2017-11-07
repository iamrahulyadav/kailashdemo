package com.automobile.service.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomTextView;
import com.automobile.service.fragment.CategoryListFragment;
import com.automobile.service.model.BookService.BookServiceModel;

import java.util.ArrayList;
import java.util.List;


public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<BookServiceModel> bookServiceModelList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private CategoryListFragment serviceListFragment;
    private boolean isLoading;
    private int lastPosition = -1;

    public CategoryListAdapter(CategoryListFragment guidelinesFragment, Context context, List<BookServiceModel> items) {
        this.bookServiceModelList = items;
        this.serviceListFragment = guidelinesFragment;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_servicelist, parent, false);
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

    public void addRecord(ArrayList<BookServiceModel> sleeptipsModelArrayList) {
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
                    onItemClickListener.onItemClick(v, (BookServiceModel) v.getTag());
                }
            }, 200);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, BookServiceModel viewModel);

    }

    protected class ViewHolderData extends RecyclerView.ViewHolder {


        private CardView cvContainer;
        private CustomTextView tvCatName;
        private ImageView ivCatImg;



        public ViewHolderData(View itemView) {
            super(itemView);

            cvContainer = (CardView) itemView.findViewById(R.id.row_servicelist_cvContainer);
            tvCatName = (CustomTextView) itemView.findViewById(R.id.row_servicelist_tvCatName);
            ivCatImg = (ImageView) itemView.findViewById(R.id.row_servicelist_ivCatImg);


        }

        public void bindData(BookServiceModel item, int position)
        {
            tvCatName.setText(mContext.getString(R.string.service_no) + " : " + item.getBookId());
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
