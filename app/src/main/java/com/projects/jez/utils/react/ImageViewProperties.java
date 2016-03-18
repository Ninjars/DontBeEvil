package com.projects.jez.utils.react;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Property;
import android.widget.ImageView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Observable;

public class ImageViewProperties {

    private static class ImageProperty extends Property<ImageView, Bitmap> {
        public ImageProperty() {
            super(Bitmap.class, "image");
        }

        @Override
        public Bitmap get(ImageView view) {
            return ((BitmapDrawable)view.getDrawable()).getBitmap();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(ImageView object, Bitmap value) {
            object.setImageBitmap(value);
        }
    }

    private static class ImageDrawableProperty extends Property<ImageView, Integer>{
        public ImageDrawableProperty() {
            super(Integer.class, "image");
        }

        @Override
        public Integer get(ImageView view) {
            return 0;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public void set(ImageView object, Integer value) {
            object.setImageDrawable(object.getContext().getResources().getDrawable(value));
        }
    }

    private static class ImageBackgroundProperty extends Property<ImageView, Integer>{
        public ImageBackgroundProperty() {
            super(Integer.class, "image");
        }

        @Override
        public Integer get(ImageView view) {
            return ((ColorDrawable)view.getBackground()).getColor();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(ImageView object, Integer value) {
            object.setBackgroundColor(value);
        }
    }

    public static final Property<ImageView, Bitmap> BITMAP = new ImageProperty();
    public static final Property<ImageView, Integer> DRAWABLE = new ImageDrawableProperty();
    public static final Property<ImageView, Integer> BACKGROUND_COLOR = new ImageBackgroundProperty();

    public static void bindBitmapProperty(final ImageView view, Observable<Bitmap> observable){
        ViewProperties.bindProperty(view, observable, BITMAP, R.id.reactive_image_disposable);
    }

    public static void bindDrawableProperty(final ImageView view, Observable<Integer> observable){
        ViewProperties.bindProperty(view, observable, DRAWABLE, R.id.reactive_image_disposable);
    }

    public static void bindBackgroundColorProperty(final ImageView view, Observable<Integer> observable){
        ViewProperties.bindProperty(view, observable, BACKGROUND_COLOR, R.id.reactive_image_disposable);
    }
}
