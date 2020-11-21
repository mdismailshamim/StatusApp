package com.facebookstatus.statusapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.facebookstatus.statusapp.adapter.AdapterLiked;
import com.facebookstatus.statusapp.model.ModelLiked;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityLiked extends AppCompatActivity {

    ArrayList<String> myLikedData;
    RecyclerView likedRecyclerView;

    AdapterLiked adapter;

    ImageView liked_empty;

    SharedPreferences myPref;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        likedRecyclerView = findViewById(R.id.likedRecyclerView);
        liked_empty = findViewById(R.id.liked_empty);

        myPref = getSharedPreferences("ActivityLiked", Context.MODE_PRIVATE);
        myEditor = myPref.edit();

        myLikedData = new ArrayList<>();

        checkIfEmptyOrNot();

        ArrayList<ModelLiked> modelLiked = prepareLikedList();
        adapter = new AdapterLiked(ActivityLiked.this, modelLiked);

        likedRecyclerView.setHasFixedSize(true);

        likedRecyclerView.setItemViewCacheSize(8);
        likedRecyclerView.setDrawingCacheEnabled(true);
        likedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        likedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        likedRecyclerView.setAdapter(adapter);

        showFullScreenAd();
    }

    private ArrayList<ModelLiked> prepareLikedList() {
        ArrayList<ModelLiked> modelLikeds = new ArrayList<>();
        for (int i = 0; i < myLikedData.size(); i++) {
            ModelLiked modelLiked = new ModelLiked();
            modelLiked.setQuote(myLikedData.get(i));
            modelLikeds.add(modelLiked);
        }
        return modelLikeds;
    }

    private void checkIfEmptyOrNot() {

        Gson gson = new Gson();
        String json = myPref.getString("quote", "");
        if (!json.isEmpty()) {

            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> arrPackageData = gson.fromJson(json, type);

            //Full data added to list
            myLikedData.addAll(arrPackageData);
        }

        String newJson = gson.toJson(myLikedData);
        myEditor.putString("quote", newJson);
        myEditor.apply();

        if (myLikedData.size() == 0) {
            likedRecyclerView.setVisibility(View.GONE);
            liked_empty.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkIfAdapterEmpty() {
        if (adapter.getItemCount() == 0) {
            likedRecyclerView.setVisibility(View.GONE);
            liked_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}