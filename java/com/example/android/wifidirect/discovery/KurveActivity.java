package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class KurveActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(new KurveView(this, metrics.heightPixels, metrics.widthPixels));
/*
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mLunarThread.setState(LunarThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mLunarThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
*/
    }
}
