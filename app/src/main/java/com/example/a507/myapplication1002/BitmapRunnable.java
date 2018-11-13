package com.example.a507.myapplication1002;

import android.widget.ImageView;

public class BitmapRunnable implements Runnable {
    protected  ImageView ivBitmap;
    protected String sBitmapUrl;

    public BitmapRunnable(ImageView ivBitmap) {
        this.ivBitmap = ivBitmap;
        this.sBitmapUrl = sBitmapUrl;
    }

    @Override
    public void run() {

    }
}
