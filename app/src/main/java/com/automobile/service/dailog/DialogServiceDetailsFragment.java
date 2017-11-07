package com.automobile.service.dailog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automobile.service.R;
import com.automobile.service.model.BookService.BookServiceModel;


public class DialogServiceDetailsFragment extends DialogFragment implements OnClickListener {

    // Member fields
    private TextView tvCarNAme;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvType;
    private TextView tvDes;
    private TextView tvDateTime;
    private TextView tvStutas;
    private TextView serviceNo;
    private ImageView ivClose;
    private BookServiceModel viewModel = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        //dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimationProfileInfo;
        dialog.setCanceledOnTouchOutside(true);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        //final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        Bundle mBundle = getArguments();

        if (mBundle != null) {

            viewModel = mBundle.getParcelable("selected_item");
        }

        dialog.setContentView(R.layout.dailog_service_details);
        initializeComponent(dialog);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme);
        super.onCreate(savedInstanceState);

    }

    protected void initializeComponent(Dialog v) {
        getActivity().setResult(Activity.RESULT_CANCELED);

        tvCarNAme = (TextView) v.findViewById(R.id.dailog_servicelist_tvCarname);
        tvAddress = (TextView) v.findViewById(R.id.dailog_servicelist_tvAddress);
        tvPhone = (TextView) v.findViewById(R.id.dailog_servicelist_tvPhone);
        tvDateTime = (TextView) v.findViewById(R.id.dailog_servicelist_tvDAteTime);
        tvDes = (TextView) v.findViewById(R.id.dailog_servicelist_tvServiceDes);
        tvStutas = (TextView) v.findViewById(R.id.dailog_servicelist_tvStatus);
        tvType = (TextView) v.findViewById(R.id.dailog_servicelist_tvServiceType);
        serviceNo = (TextView) v.findViewById(R.id.dailog_servicelist_tvServiceNo);
        ivClose = (ImageView) v.findViewById(R.id.dailog_servicelist_ivClose);

        ivClose.setOnClickListener(this);


        if (viewModel != null) {

            tvCarNAme.setText("" + viewModel.getServicesCarName());
            tvAddress.setText("Address : " + viewModel.getServiceAddress());
            tvPhone.setText("Phone : " + viewModel.getServiceCustomerContact());
            tvDateTime.setText("" + viewModel.getServicesBookedDate() + " " + viewModel.getServicesBookedTime());
            tvDes.setText("" + viewModel.getServiceDesc());
            tvStutas.setText("" + viewModel.getServiceStatus());
            tvType.setText("" + viewModel.getServiceType());
            serviceNo.setText("Service No : " + viewModel.getBookId());


            if (viewModel.getServiceType().equals("1")) {
                tvType.setText(getString(R.string.service_type) + " : " + getString(R.string.type_pickup));
            } else if (viewModel.getServiceType().equals("2")) {
                tvType.setText(getString(R.string.service_type) + " : " + getString(R.string.type_dropoff));
            } else if (viewModel.getServiceType().equals("3")) {
                tvType.setText(getString(R.string.service_type) + " : " + getString(R.string.type_doorstep));
            }

            if (viewModel.getServiceStatus().equals("Pending")) {
                tvStutas.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            } else if (viewModel.getServiceStatus().equals("Completed")) {
                tvStutas.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkgreen));
            } else if (viewModel.getServiceStatus().equals("Cancel")) {
                tvStutas.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.dailog_servicelist_ivClose:
                getDialog().cancel();

            default:
                break;
        }

    }


}
