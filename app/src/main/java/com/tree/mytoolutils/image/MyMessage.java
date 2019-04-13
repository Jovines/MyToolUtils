package com.tree.mytoolutils.image;

public class MyMessage <T, V> {
    T imageView;
    V bitmap;

    MyMessage(T imageView, V bitmap) {
        this.imageView = imageView;
        this.bitmap= bitmap;
    }

    public T getImageView() {
        return imageView;
    }

    public V getBitmap() {
        return bitmap;
    }

}
