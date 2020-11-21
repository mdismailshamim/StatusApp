package com.facebookstatus.statusapp.model;

public class ModelQuote {

    private int image;
    private String quote;
    private boolean isLiked;

    public ModelQuote() {
    }

    public ModelQuote(int image, String quote, boolean isLiked) {
        this.image = image;
        this.quote = quote;
        this.isLiked = isLiked;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}