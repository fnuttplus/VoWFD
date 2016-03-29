package com.example.android.wifidirect.discovery;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment {
    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KurveActivity kurveActivity = (KurveActivity)getActivity();
        mHandler = kurveActivity.mHandler;

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

}
