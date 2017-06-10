package com.moscowmuleaddicted.neighborhoodsecuritywear.extra;

import android.graphics.drawable.Drawable;

/**
 * Created by Simone Ripamonti on 10/06/2017.
 */

public class MainItem {

    private Drawable image;
    private String text;
    private ItemType type;

    public MainItem() {

    }

    public MainItem(Drawable image, String text, ItemType type) {
        this.image = image;
        this.text = text;
        this.type = type;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

}
