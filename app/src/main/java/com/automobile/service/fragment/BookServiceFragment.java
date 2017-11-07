package com.automobile.service.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.automobile.service.R;
import com.automobile.service.adapter.StateSpinnerAdapter;
import com.automobile.service.imagecrop.CropHelper;
import com.automobile.service.util.Utils;
import com.automobile.service.view.MenuBarActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BookServiceFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private LinearLayout llContainer;
    private EditText etCarName;
    private EditText etDescription;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvTypeSpinner;
    private Spinner spServiceType;
    private Button btnNext;

    private String carNAme = "";
    private String description = "";
    private String mDate = "";
    private String mTime = "";
    private String serviceType = "";

    private ProgressDialog progress;
    private ArrayList<String> serviceTypeArr = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_bookservice, container, false);
        initComponents(rootView);
        initToolbar();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void initComponents(View rootView) {

        llContainer = (LinearLayout) rootView.findViewById(R.id.fragment_bookservice_llContainer);
        etDescription = (EditText) rootView.findViewById(R.id.fragment_bookservice_etDescription);
        etCarName = (EditText) rootView.findViewById(R.id.fragment_bookservice_etCarName);
        tvDate = (TextView) rootView.findViewById(R.id.fragment_bookservice_tvDate);
        tvTime = (TextView) rootView.findViewById(R.id.fragment_bookservice_tvTime);
        tvTypeSpinner = (TextView) rootView.findViewById(R.id.fragment_bookservice_tvTypeSpinner);
        spServiceType = (Spinner) rootView.findViewById(R.id.fragment_bookservice_spServiceType);
        btnNext = (Button) rootView.findViewById(R.id.fragment_bookservice_btnNext);

        spServiceType.setOnItemSelectedListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvTypeSpinner.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        serviceTypeArr.add(getString(R.string.type_pickup));
        serviceTypeArr.add(getString(R.string.type_dropoff));
        serviceTypeArr.add(getString(R.string.type_doorstep));


        StateSpinnerAdapter stateSpinnerAdapter = new StateSpinnerAdapter(getActivity(), serviceTypeArr);
        spServiceType.setAdapter(stateSpinnerAdapter);


    }


    public void initToolbar() {
        ((MenuBarActivity) getActivity()).setUpToolbar(getString(R.string.menu_book_service), true);

    }


    @Override
    public void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        CropHelper.clearCacheDir();
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity());

        if (v == tvDate) {
            showDatePickerDialog();
        } else if (v == tvTime) {
            showTimePickerDialog();
        } else if (v == tvTypeSpinner) {
            spServiceType.performClick();
        }
        else if (v == btnNext) {
            submitForm();
        }
    }


    /**
     * Validating form
     */

    private void submitForm() {

        carNAme = etCarName.getText().toString().trim();
        description = etDescription.getText().toString().trim();
        mDate = tvDate.getText().toString().trim();
        mTime = tvTime.getText().toString().trim();
        description = etDescription.getText().toString().trim();

        if (serviceType.isEmpty()) {
            validateField(llContainer, etCarName, getString(R.string.val_enter_type));
        } else if (carNAme.isEmpty()) {
            validateField(llContainer, etCarName, getString(R.string.val_enter_carname));
        } else if (mDate.isEmpty()) {
            validateField(llContainer, etCarName, getString(R.string.val_enter_date));
        } else if (mTime.isEmpty()) {
            validateField(llContainer, etCarName, getString(R.string.val_enter_time));
        } else {

            BookServiceCustomerFragment bookServiceCustomerFormFragment = new BookServiceCustomerFragment();
            Bundle bundle=new Bundle();
            bundle.putString(getString(R.string.bdl_service_type),serviceType);
            bundle.putString(getString(R.string.bdl_carname),carNAme);
            bundle.putString(getString(R.string.bdl_service_date),mDate);
            bundle.putString(getString(R.string.bdl_service_time),mTime);
            bundle.putString(getString(R.string.bdl_service_instruction),description);
            bookServiceCustomerFormFragment.setArguments(bundle);
            Utils.addNextFragment(getActivity(), bookServiceCustomerFormFragment, BookServiceFragment.this, false);

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectType = serviceTypeArr.get(position);
        tvTypeSpinner.setText(selectType);

        if (selectType.equals(getString(R.string.type_pickup))) {
            serviceType = "1";
        } else if (selectType.equals(getString(R.string.type_dropoff))) {
            serviceType = "2";
        } else if (selectType.equals(getString(R.string.type_doorstep))) {
            serviceType = "3";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        int Seconds;
        int ampm;
        String strampm;
        final Calendar c = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            TimePickerDialog timePickerDialog = null;
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            Seconds = c.get(Calendar.SECOND);
            c.set(Calendar.AM_PM, c.get(Calendar.AM_PM));


            // Create a new instance of TimePickerDialog and return it
            timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            return timePickerDialog;

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
            date.set(Calendar.MINUTE, minute);
            date.set(Calendar.AM_PM, date.get(Calendar.AM_PM));
            String myFormat = "HH:mm:ss a";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            tvTime.setText(sdf.format(date.getTime()));

        }
    }

    public void showDatePickerDialog() {
        Bundle bundle = new Bundle();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "datePicker");
    }


    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog;
            dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            String mDate=day+"/"+month+"/"+year;

            long mindate = dateToMilisecond(mDate);
            dialog.getDatePicker().setMinDate(mindate);

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            tvDate.setText(day + "/" + month + "/" + year);
        }
    }

    public Long dateToMilisecond(final String string_date) {
        Date d;
        long milliseconds = 0;
        try {
            if (string_date != null && !string_date.isEmpty()) {

                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                d = f.parse(string_date);
                milliseconds = d.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
}
