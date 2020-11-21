package com.facebookstatus.statusapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (properInternetAvailable()) {
                    startActivity(new Intent(ActivitySplash.this, ActivityHome.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    startActivity(new Intent(ActivitySplash.this, ActivityInternet.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        }, 2000);
    }

    private boolean properInternetAvailable() {
        boolean returnState = false;
        if (isNetworkConnected()) {
            if (isNetworkConnected()) {
                returnState = true;
            } else {
                returnState = false;
            }
        } else {
            returnState = false;
        }
        return returnState;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}