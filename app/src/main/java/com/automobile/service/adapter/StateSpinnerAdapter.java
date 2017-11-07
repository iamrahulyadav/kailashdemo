package com.automobile.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.automobile.service.R;

import java.util.List;


public class StateSpinnerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> items;

    public StateSpinnerAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null)
            view = inflater.inflate(R.layout.row_state_spinner_item, viewGroup, false);
        ((TextView) view.findViewById(R.id.row_taskProperty_tv_item)).setText(items.get(position));

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.row_state_spinner_dropdown, viewGroup, false);
        ((TextView) view.findViewById(R.id.row_taskproperty_tv_item_dropdown)).setText(items.get(position));


        return view;
    }
}
