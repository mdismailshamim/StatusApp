package com.facebookstatus.statusapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.facebookstatus.statusapp.adapter.AdapterCategory;
import com.facebookstatus.statusapp.model.ModelCategory;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class
ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AlertDialog exitDialog;

    //View bottomSpacer;
    ImageView home_quote_bg;
    TextView home_quote_txt;
    RecyclerView homeRecyclerView;

    NavigationView navigationView;

    TextView home_item_rate, home_item_share, home_item_twitter, home_item_fb_like, home_item_insta_follow;

    static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setStatusBarGradiant(this);
        setSupportActionBar(toolbar);
        applyFontForToolbarTitle(this);

        //bottomSpacer = findViewById(R.id.bottomSpacer);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initiateMiddleBars();

        showAllCategories();
        askStoragePermission();
        startExitDialog();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_actionbar);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
            toolbar.setBackground(background);
        }
    }

    private void initiateMiddleBars() {
        home_item_rate = findViewById(R.id.home_item_rate);
        home_item_share = findViewById(R.id.home_item_share);
        home_item_twitter = findViewById(R.id.home_item_twitter);
        home_item_fb_like = findViewById(R.id.home_item_fb_like);
        home_item_insta_follow = findViewById(R.id.home_item_insta_follow);

        home_item_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateTheApp();
            }
        });

        home_item_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTheApp();
            }
        });

        home_item_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitterUrl(ActivityConfig.SOCIAL_TWITTER);
            }
        });

        home_item_fb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFbUrl(ActivityConfig.SOCIAL_FACEBOOK);
            }
        });

        home_item_insta_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram(ActivityConfig.SOCIAL_INSTAGRAM);
            }
        });

    }

    private void showQuoteOfTheDay() {
        home_quote_bg = findViewById(R.id.home_quote_bg);
        home_quote_txt = findViewById(R.id.home_quote_txt);

        Random random = new Random();
        int indexToGetImageFrom = random.nextInt(ActivityConfig.RANDOM_IMAGE.length);

        Glide.with(ActivityHome.this)
                .load(ActivityConfig.RANDOM_IMAGE[indexToGetImageFrom])
                .into(home_quote_bg);

        SharedPreferences myPref = getSharedPreferences("ActivityHome", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPref.edit();

        ArrayList<String> myAllQuotes = new ArrayList<>();

        Gson gson = new Gson();
        String json = myPref.getString("allQuotes", "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> arrPackageData = gson.fromJson(json, type);

            //Full data added to list
            for (String data : arrPackageData) {
                myAllQuotes.add(data);
            }
        }

        int quoteToday = myPref.getInt("quoteToday", 0);

        String gotDay = myPref.getString("gotDay", "20190430");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());

        if (gotDay.equals(currentDate)) {
            //Same Day
            home_quote_txt.setText(myAllQuotes.get(quoteToday));

        } else {
            //New Day
            quoteToday = quoteToday + 1;
            home_quote_txt.setText(myAllQuotes.get(quoteToday));
        }

        if (myAllQuotes.size() < quoteToday) {
            quoteToday = 0;
        }

        myEditor.putInt("quoteToday", quoteToday);
        myEditor.putString("gotDay", currentDate);
        myEditor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_liked) {
            startActivity(new Intent(ActivityHome.this, ActivityLiked.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_favorite) {
            startActivity(new Intent(ActivityHome.this, ActivityLiked.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_twitter) {
            openTwitterUrl(ActivityConfig.SOCIAL_TWITTER);

        } else if (id == R.id.nav_facebook) {
            openFbUrl(ActivityConfig.SOCIAL_FACEBOOK);

        } else if (id == R.id.nav_instagram) {
            openInstagram(ActivityConfig.SOCIAL_INSTAGRAM);

        } else if (id == R.id.nav_share) {
            shareTheApp();

        } else if (id == R.id.nav_rate) {
            rateTheApp();

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(ActivityHome.this, ActivityAbout.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_privacy) {
            startActivity(new Intent(ActivityHome.this, ActivityPrivacy.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void showAllCategories() {
        homeRecyclerView = findViewById(R.id.homeRecyclerView);

        ArrayList<ModelCategory> modelCategory = prepareData();
        AdapterCategory adapter = new AdapterCategory(ActivityHome.this, modelCategory);

        homeRecyclerView.setHasFixedSize(true);

        homeRecyclerView.setItemViewCacheSize(16);
        homeRecyclerView.setDrawingCacheEnabled(true);
        homeRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        GridLayoutManager grdMgr = new GridLayoutManager(this, 3);
        homeRecyclerView.setLayoutManager(grdMgr);
        homeRecyclerView.setAdapter(adapter);
    }

    private ArrayList<ModelCategory> prepareData() {
        ArrayList<ModelCategory> modelCategories = new ArrayList<>();
        for (int i = 0; i < ActivityConfig.CATEGORY_NAME.length; i++) {
            ModelCategory modelCategory = new ModelCategory();
            modelCategory.setTitle(ActivityConfig.CATEGORY_NAME[i]);
            modelCategory.setImage(ActivityConfig.CATEGORY_IMAGE[i]);
            modelCategory.setImage(ActivityConfig.CATEGORY_IMAGE[i]);
            modelCategories.add(modelCategory);
        }
        return modelCategories;
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

    private boolean askStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder permissionAlert = new AlertDialog.Builder(this);
            permissionAlert.setTitle("Permission Required!")
                    .setMessage("We need storage permission to downloaded files")
                    .setPositiveButton("GRANT IT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            askStoragePermission();
                        }
                    })
                    .setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ActivityHome.this, "Sorry, closing App!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }


    private void shareTheApp() {
        final String appPackageName = getPackageName();
        String appName = getString(R.string.app_name);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String postData = "Get " + appName + " for amazing quotes from various categories: https://play.google.com/store/apps/details?id=" + appPackageName;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Get App Now!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, postData);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share App Via"));
    }

    private void rateTheApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    protected void openFbUrl(String username) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = openFacebookUrl(username);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    public String openFacebookUrl(String username) {
        String FACEBOOK_URL = "https://www.facebook.com/" + username;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + username;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    protected void openTwitterUrl(String username) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=" + username));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/" + username)));
        }
    }

    protected void openInstagram(String username) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + username);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + username)));
        }
    }

    private void startExitDialog() {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.dialog_home_exit, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        Button myExitClose = alertLayout.findViewById(R.id.homeExitClose);
        ImageView myExitDismiss = alertLayout.findViewById(R.id.homeExitDismiss);
        final LinearLayout adContainer = alertLayout.findViewById(R.id.homeExitAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(ActivityConfig.ADMOB_BANNER);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adContainer.setVisibility(View.VISIBLE);
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(adView, params);

        myExitClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });

        myExitDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        alert.setView(alertLayout);
        alert.setCancelable(false);
        exitDialog = alert.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showQuoteOfTheDay();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}