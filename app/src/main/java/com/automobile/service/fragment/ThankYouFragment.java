package com.automobile.service.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.automobile.service.AutumobileAplication;
import com.automobile.service.R;
import com.automobile.service.util.Constans;
import com.automobile.service.view.MenuBarActivity;


public class ThankYouFragment extends BaseFragment {


    //Declaration
    private LinearLayout llContainer;
    private Button btnGoToHomepage;


    private static int MAX_CLICK_INTERVAL = 1500;
    private long mLastClickTime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_thankyou_page, container, false);
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

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_thankyou_llContainer);
        btnGoToHomepage = (Button) rootView.findViewById(R.id.fragment_thankyou_btnGotoHomePage);

        Constans.IS_HOME = true;
        btnGoToHomepage.setOnClickListener(this);
        AutumobileAplication.getmInstance().savePreferenceDataString(getString(R.string.preferances_cartids), "");
    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_thank_you), true);

    }


    @Override
    public void onClick(View v) {

        if (v == btnGoToHomepage) {

            Constans.IS_HOME = false;
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();


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
