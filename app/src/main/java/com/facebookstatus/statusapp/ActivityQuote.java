package com.facebookstatus.statusapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.facebookstatus.statusapp.adapter.AdapterQuote;
import com.facebookstatus.statusapp.model.ModelQuote;

import java.util.ArrayList;
import java.util.List;

public class ActivityQuote extends AppCompatActivity {
    InterstitialAd interstitialAd = null;
    String title;
    ImageView toolbar_background;
    RecyclerView quoteRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_interstetial_ad));
        interstitialAd.loadAd(adRequest);
        applyFontForToolbarTitle(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
        }
        setTitle(title);
        toolbar_background = findViewById(R.id.quoteToolbarImageView);
        String xImage = "cat_" + title.toLowerCase();
        int resID = getResources().getIdentifier(xImage, "drawable", getPackageName());
        //toolbar_background.setImageResource(resID);

        Log.d("asdasdasd", "ID " + resID);

        Glide.with(this)
                .load(resID)
                .into(toolbar_background);

        showAllCategories();
    }

    public static void applyFontForToolbarTitle(Activity context) {
        Toolbar toolbar = context.findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "fonts/default_font.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }

    private void showAllCategories() {
        quoteRecyclerView = findViewById(R.id.quoteRecyclerView);

        ArrayList<ModelQuote> modelQuotes = prepareData();
        AdapterQuote adapter = new AdapterQuote(ActivityQuote.this, modelQuotes);

        quoteRecyclerView.setHasFixedSize(true);

        quoteRecyclerView.setItemViewCacheSize(16);
        quoteRecyclerView.setDrawingCacheEnabled(true);
        quoteRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        quoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quoteRecyclerView.setAdapter(adapter);
    }

    private ArrayList<ModelQuote> prepareData() {

        int xPos = 0;
        for (int i = 0; i < ActivityConfig.CATEGORY_NAME.length; i++) {
            if (ActivityConfig.CATEGORY_NAME[i].contains(title)) {
                xPos = i;
                break;
            }
        }

        String[] selectedList = ActivityConfig.ALL_CATEGORIES[xPos];

        ArrayList<ModelQuote> modelCategories = new ArrayList<>();
        for (int i = 0; i < ActivityConfig.ALL_CATEGORIES[xPos].length; i++) {
            ModelQuote modelQuote = new ModelQuote();
            modelQuote.setQuote(selectedList[i]);
            modelQuote.setLiked(checkIfLiked(selectedList[i]));
            modelCategories.add(modelQuote);
        }
        return modelCategories;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkIfLiked(String quote) {
        SharedPreferences myPref = getSharedPreferences("ActivityLiked", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPref.getString("quote", "");

        Log.d("logListA", "List:" + json);

        List<String> arrPackageData = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

        if (arrPackageData != null) {
            ArrayList<String> myLikedData = new ArrayList<>(arrPackageData);
            return myLikedData.contains(quote);
        } else {
            return false;
        }
    }

    private void showFullScreenAd() {
        final InterstitialAd mInterstitialAd;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ActivityConfig.ADMOB_INTERSTITIAL);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }


    }

}