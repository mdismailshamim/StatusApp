package com.facebookstatus.statusapp.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.facebookstatus.statusapp.ActivityConfig;
import com.facebookstatus.statusapp.R;
import com.facebookstatus.statusapp.model.ModelQuote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AdapterQuote extends RecyclerView.Adapter<AdapterQuote.ViewHolder> {

    ArrayList<ModelQuote> modelQuote;
    Context context;

    public AdapterQuote(Context context, ArrayList<ModelQuote> modelQuote) {
        this.modelQuote = modelQuote;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_quote, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Random random = new Random();
        int indexToGetImageFrom = random.nextInt(ActivityConfig.RANDOM_IMAGE.length);

        Glide.with(context)
                .load(ActivityConfig.RANDOM_IMAGE[indexToGetImageFrom])
                .into(holder.quoteImage);

        final String stringQuote = modelQuote.get(position).getQuote();

        holder.quoteName.setText(Html.fromHtml("&ldquo; \n" + modelQuote.get(position).getQuote() + "\n &rdquo;"));

        holder.quoteLikeButton.setLiked(modelQuote.get(position).isLiked());

        holder.quoteLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addToLikedQuotes(stringQuote, true);
                Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addToLikedQuotes(stringQuote, false);
                Toast.makeText(context, "Like Removed", Toast.LENGTH_SHORT).show();
            }
        });

        holder.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.quoteLikeButton.isLiked()) {
                    holder.quoteLikeButton.setLiked(false);
                    addToLikedQuotes(stringQuote, false);
                    Toast.makeText(context, "Like Removed", Toast.LENGTH_SHORT).show();
                } else {
                    holder.quoteLikeButton.setLiked(true);
                    addToLikedQuotes(stringQuote, true);
                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.lnrSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuoteAsImage(holder);
            }
        });

        holder.lnrCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(position);
            }
        });

        holder.lnrShare.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                MenuBuilder menuBuilder =new MenuBuilder(context);
                MenuInflater inflater = new MenuInflater(context);
                inflater.inflate(R.menu.menu_quote_popup, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, v);
                optionsMenu.setForceShowIcon(true);

                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sub_text:
                                try {
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("text/plain");
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, stringQuote + "\n\nGet more amazing quotes at " + context.getResources().getString(R.string.app_name) +
                                            ". Get now: https://play.google.com/store/apps/details?id=" + context.getPackageName());
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
                                } catch (Exception e) {
                                }
                                break;

                            case R.id.sub_image:
                                RelativeLayout content = holder.quoteBackground;
                                content.setDrawingCacheEnabled(true);
                                Bitmap bitmap = content.getDrawingCache();

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, stringQuote + "\n\nGet more amazing quotes at " + context.getResources().getString(R.string.app_name) +
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, bitmap)));
                                shareIntent.setType("image/jpeg");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
                                break;
                        }
                        return true;
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {}
                });

                optionsMenu.show();
            }
        });

        if (position % 10 == 8) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = new AdView(context);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(ActivityConfig.ADMOB_BANNER);
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    holder.adContainer.setVisibility(View.VISIBLE);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.adContainer.addView(adView, params);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public int getItemCount() {
        return modelQuote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout adContainer;

        private TextView txtLike;
        private TextView quoteName;
        private ImageView quoteImage;
        private LikeButton quoteLikeButton;
        private RelativeLayout quoteBackground;
        private LinearLayout lnrSave, lnrCopy, lnrShare;

        public ViewHolder(View view) {
            super(view);

            adContainer = view.findViewById(R.id.adViewLayout);

            quoteName = view.findViewById(R.id.item_quote_name);
            quoteImage = view.findViewById(R.id.item_quote_image);

            txtLike = view.findViewById(R.id.item_quote_like);
            lnrSave = view.findViewById(R.id.item_quote_save);
            lnrCopy = view.findViewById(R.id.item_quote_copy);
            lnrShare = view.findViewById(R.id.item_quote_share);
            quoteBackground = view.findViewById(R.id.item_quote_background);
            quoteLikeButton = view.findViewById(R.id.item_quote_like_heart);
        }
    }

    private void copyToClipboard(int pos) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", modelQuote.get(pos).getQuote());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Caption Copied", Toast.LENGTH_SHORT).show();
    }

    private void saveQuoteAsImage(ViewHolder holder) {
        RelativeLayout content = holder.quoteBackground;
        content.setDrawingCacheEnabled(true);
        Bitmap bitmap = content.getDrawingCache();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        File file = new File(Environment.getExternalStorageDirectory() + File.separator +
                context.getResources().getString(R.string.app_name) + File.separator + "Quote_" + currentDateandTime + ".png");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();
            content.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            content.setDrawingCacheEnabled(false);
        }
        Toast.makeText(context, "Caption Saved!", Toast.LENGTH_SHORT).show();
    }


    private void addToLikedQuotes(String quote, boolean isLiked) {

        SharedPreferences myPref = context.getSharedPreferences("ActivityLiked", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = myPref.edit();

        ArrayList<String> myLikedData = new ArrayList<>();

        Gson gson = new Gson();
        String json = myPref.getString("quote", "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> arrPackageData = gson.fromJson(json, type);

            //Full data added to list
            for (String data : arrPackageData) {
                Log.d("sharedList", data);
                myLikedData.add(data);
            }
        }

        if (isLiked) {
            myLikedData.add(quote);
        } else {
            myLikedData.remove(quote);
        }
        String newJson = gson.toJson(myLikedData);
        myEditor.putString("quote", newJson);
        myEditor.apply();
    }
}