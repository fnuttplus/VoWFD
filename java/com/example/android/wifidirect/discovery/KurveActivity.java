package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class KurveActivity extends Activity implements Handler.Callback {
    public Handler mHandler = new Handler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(R.layout.main);
        getFragmentManager().beginTransaction().add(R.id.container_root, new MainListFragment(mHandler), "main").commit();

//        setContentView(new KurveView(this, metrics.heightPixels, metrics.widthPixels));
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Toast.makeText(this, "WiFi Direct is not supported", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();

        if (playing) {
            playing = false;
            setContentView(R.layout.main);
        }
        super.onBackPressed();
    }

    boolean playing = false;

    @Override
    public boolean handleMessage(Message msg) {
        FragmentTransaction transaction;
            switch (msg.what) {
            case 0:
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                Log.d("START", String.valueOf(msg.arg1));
                playing = true;
                setContentView(new KurveView(this, metrics.heightPixels, metrics.widthPixels));
                break;
            case 2:
                Fragment searchFragment = new SearchFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_root, searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case 1:
                Fragment createFragment = new CreateFragment();
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_root, createFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
                //getFragmentManager().beginTransaction().replace(R.id.container_root, new SearchFragment(), "main").commit();

        }
        return false;
    }
}
