package com.example.android.wifidirect.discovery;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateFragment extends Fragment implements View.OnClickListener {
    Handler mHandler;
    LinearLayout ll;
    private int mLocalPlayers = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KurveActivity kurveActivity = (KurveActivity)getActivity();
        mHandler = kurveActivity.mHandler;

        View view = inflater.inflate(R.layout.fragment_create, container, false);
        view.findViewById(R.id.btn_start).setOnClickListener(this);
        view.findViewById(R.id.btn_add_local).setOnClickListener(this);

        ll = (LinearLayout)view.findViewById(R.id.ll_players);
        TextView tv = new TextView(getActivity());
        tv.setText(Build.MODEL);
        ll.addView(tv);



        WifiP2pManager.Channel channel;
        Context ctx = getActivity();
        WifiP2pManager manager = (WifiP2pManager) ctx.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(ctx, ctx.getMainLooper(), null);

        manager.createGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Group Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                switch (reason) {
                    case 1:
                        Toast.makeText(getActivity(), "This device does not support P2P", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        new Thread(new GameServer()).start();
        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                mHandler.obtainMessage(0,mLocalPlayers,0,null).sendToTarget();
                break;
            case R.id.btn_add_local:
                TextView tv = new TextView(getActivity());
                tv.setText(Build.MODEL+"_"+String.valueOf(++mLocalPlayers));
                ll.addView(tv);
                break;
            default:
                Log.d("CreateClick", String.valueOf(v.getId()));
                break;

        }
    }
}
