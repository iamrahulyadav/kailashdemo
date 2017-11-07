package com.automobile.service.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.automobile.service.R;
import com.automobile.service.model.product.MultipleImageProductModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ProductDetailsMultiImagePagerAdapter extends android.support.v4.view.PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MultipleImageProductModel> multipleImageProductModelArrayList;

    public ProductDetailsMultiImagePagerAdapter(Context context, ArrayList<MultipleImageProductModel> multipleImageProductModelArrayList) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.multipleImageProductModelArrayList = multipleImageProductModelArrayList;
    }

    @Override
    public int getCount()
    {
        return multipleImageProductModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.productdetail_pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_item_ivProduct);

        if (multipleImageProductModelArrayList.get(position).getImage() != null) {
            final String url = multipleImageProductModelArrayList.get(position).getImage();
            Glide.with(mContext).load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageView);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
