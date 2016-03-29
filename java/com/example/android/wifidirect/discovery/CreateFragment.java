package com.example.android.wifidirect.discovery;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CreateFragment extends Fragment implements View.OnClickListener {
    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KurveActivity kurveActivity = (KurveActivity)getActivity();
        mHandler = kurveActivity.mHandler;

        View view = inflater.inflate(R.layout.fragment_create, container, false);
        view.findViewById(R.id.button2).setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    public void onStart() {
        Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        mHandler.obtainMessage(0).sendToTarget();
    }
}
