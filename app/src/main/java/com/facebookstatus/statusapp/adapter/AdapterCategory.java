package com.facebookstatus.statusapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebookstatus.statusapp.ActivityQuote;
import com.facebookstatus.statusapp.R;
import com.facebookstatus.statusapp.model.ModelCategory;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    ArrayList<ModelCategory> modelCategory;
    Context context;

    public AdapterCategory(Context context, ArrayList<ModelCategory> modelCategory) {
        this.modelCategory = modelCategory;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Glide.with(context)
                .load(modelCategory.get(position).getImage())
                .into(holder.categoryImage);

        holder.categoryName.setText(modelCategory.get(position).getTitle());

/*
        Picasso.get().load(modelCategory.get(position).getImage_url()[position]).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        holder.catTextView.setBackgroundColor(palette.getDarkVibrantSwatch().getTitleTextColor());

                        */
/*
                        Palette.Swatch mutedColor = palette.getDarkVibrantSwatch();
                        Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();
                        Palette.Swatch dominantColor = palette.getDominantSwatch();
                        Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        if (mutedColor != null) {
                            holder.catTextView.setBackgroundColor(palette.getMutedColor(0));
                        } else if (darkVibrant != null) {
                            holder.catTextView.setBackgroundColor(palette.getVibrantColor(0));
                        } else if (dominantColor != null) {
                            holder.catTextView.setBackgroundColor(palette.getDominantColor(0));
                        } else if (lightVibrant != null) {
                            holder.catTextView.setBackgroundColor(palette.getLightMutedColor(0));

                        } else if (mutedSwatch != null) {
                            holder.catTextView.setBackgroundColor(palette.getMutedColor(0));
                        }*//*

                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                activity.startActivity(new Intent(context, ActivityQuote.class)
                        .putExtra("title", modelCategory.get(position).getTitle())
                        .putExtra("category", modelCategory.get(position).getCategory()));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;
        private CircleImageView categoryImage;

        public ViewHolder(View view) {
            super(view);

            categoryName = view.findViewById(R.id.item_category_name);
            categoryImage = view.findViewById(R.id.item_category_image);
        }
    }
}