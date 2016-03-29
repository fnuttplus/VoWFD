package com.example.android.wifidirect.discovery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MainListFragment extends Fragment implements View.OnClickListener {
    Handler mHandler;
    public MainListFragment(Handler handler) {
        mHandler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);
        mainView.findViewById(R.id.textView).setOnClickListener(this);
        mainView.findViewById(R.id.textView2).setOnClickListener(this);
        return mainView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView:
                mHandler.obtainMessage(1,"test").sendToTarget();
                Toast.makeText(v.getContext(), R.string.txt_create_lobby, Toast.LENGTH_SHORT).show();
                break;
            case R.id.textView2:
                mHandler.obtainMessage(2,"test").sendToTarget();
                Toast.makeText(v.getContext(), R.string.txt_search, Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
