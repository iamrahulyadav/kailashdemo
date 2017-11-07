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

import com.automobile.service.R;
import com.automobile.service.customecomponent.CustomTextView;
import com.automobile.service.fragment.NotificationListFragment;
import com.automobile.service.model.notification.NotificationModel;

import java.util.ArrayList;
import java.util.List;


public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<NotificationModel> bookServiceModelList;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private NotificationListFragment serviceListFragment;
    private boolean isLoading;
    private int lastPosition = -1;

    public NotificationListAdapter(NotificationListFragment guidelinesFragment, Context context, List<NotificationModel> items) {
        this.bookServiceModelList = items;
        this.serviceListFragment = guidelinesFragment;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notificationlist, parent, false);
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

    public void addRecord(ArrayList<NotificationModel> sleeptipsModelArrayList) {
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
                    onItemClickListener.onItemClick(v, (NotificationModel) v.getTag());
                }
            }, 200);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, NotificationModel viewModel);

    }

    protected class ViewHolderData extends RecyclerView.ViewHolder {


        private CardView cvContainer;
        private CustomTextView tvTitle;
        private CustomTextView tvDescription;
        private CustomTextView tvDateTime;


        public ViewHolderData(View itemView) {
            super(itemView);

            cvContainer = (CardView) itemView.findViewById(R.id.row_notification_cvContainer);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.row_notification_tvTitle);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.row_notification_tvDescription);
            tvDateTime = (CustomTextView) itemView.findViewById(R.id.row_notification_tvDAteTime);


        }

        public void bindData(NotificationModel item, int position) {


            tvTitle.setText(item.getNotiTitle());
            tvDateTime.setText(item.getNotiCreated());
            tvDescription.setText(item.getNotiText());

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
