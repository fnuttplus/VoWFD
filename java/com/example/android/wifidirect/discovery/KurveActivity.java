package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class KurveActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(new KurveView(this, metrics.heightPixels, metrics.widthPixels));

    }
}
