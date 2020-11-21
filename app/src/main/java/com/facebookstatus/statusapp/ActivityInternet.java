package com.facebookstatus.statusapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ActivityInternet extends AppCompatActivity {

    Button retryBtn;
    ProgressBar retryProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retryBtn = findViewById(R.id.retry_btn);
        retryProgress = findViewById(R.id.retry_progress);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retryBtn.setEnabled(false);
                retryProgress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnected(6000)) {
                            Intent homeIntent = new Intent(ActivityInternet.this, ActivityHome.class);
                            startActivity(homeIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();

                        } else {
                            retryProgress.setVisibility(View.GONE);
                            retryBtn.setEnabled(true);
                            retryBtn.setText("TRY AGAIN");
                        }
                    }
                }, 2000);
            }
        });
    }

    private boolean isConnected(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected(10000)) {
            Intent homeIntent = new Intent(ActivityInternet.this, ActivityHome.class);
            startActivity(homeIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            retryProgress.setVisibility(View.GONE);
            retryBtn.setEnabled(true);
            retryBtn.setText("TRY AGAIN");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}