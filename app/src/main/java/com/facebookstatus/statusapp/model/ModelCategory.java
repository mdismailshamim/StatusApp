package com.facebookstatus.statusapp.model;

public class ModelCategory {

    private int image;
    private String title, category;

    public ModelCategory() {
    }

    public ModelCategory(int image, String title, String category) {
        this.image = image;
        this.title = title;
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}