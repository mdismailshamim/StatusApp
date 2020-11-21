package com.facebookstatus.statusapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;

import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.facebookstatus.statusapp.helper.DefaultFontUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityBase extends Application {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, ActivityConfig.ADMOB_APP_ID);

        DefaultFontUtils.setDefaultFont(this, "DEFAULT", "fonts/default_font.ttf");
        DefaultFontUtils.setDefaultFont(this, "MONOSPACE", "fonts/default_font.ttf");
        DefaultFontUtils.setDefaultFont(this, "SERIF", "fonts/default_font.ttf");
        DefaultFontUtils.setDefaultFont(this, "SANS_SERIF", "fonts/default_font.ttf");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        File appFolder = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        getQuoteOfTheDayData();

    }

    private void getQuoteOfTheDayData() {
        ArrayList<String> myFullQuote = new ArrayList<>();

        SharedPreferences myPref = getSharedPreferences("ActivityHome", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPref.edit();

        boolean isFirstTime = myPref.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            int totalCount = ActivityConfig.ALL_CATEGORIES.length;
            for (int a = 0; a < totalCount; a++) {
                int subCount = ActivityConfig.ALL_CATEGORIES[a].length;
                for (int b = 0; b < subCount; b++) {
                    myFullQuote.add(ActivityConfig.ALL_CATEGORIES[a][b]);
                }
            }

            Gson gson = new Gson();
            String json = myPref.getString("allQuotes", "");
            if (!json.isEmpty()) {

                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> arrPackageData = gson.fromJson(json, type);
            }
            String newJson = gson.toJson(myFullQuote);
            myEditor.putBoolean("isFirstTime", false);
            myEditor.putString("allQuotes", newJson);
            myEditor.apply();

        }
        
        /* else {

            ArrayList<String> mySavedQuotes = new ArrayList<>();

            Gson gson = new Gson();
            String json = myPref.getString("quote", "");
            if (!json.isEmpty()) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> arrPackageData = gson.fromJson(json, type);

                //Full data added to list
                for (String data : arrPackageData) {
                    Log.d("sharedList", data);
                    mySavedQuotes.add(data);
                }
            }

            for (int i = 0; i < mySavedQuotes.size(); i++) {
                Log.d("myArrayList", mySavedQuotes.get(i));
            }
        }*/

    }

}
