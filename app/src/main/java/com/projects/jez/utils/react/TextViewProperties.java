package com.projects.jez.utils.react;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Property;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Observable;

public class TextViewProperties {

    private static class TextProperty extends Property<TextView, String>{

        public TextProperty() {
            super(String.class, "text");
        }

        @Override
        public String get(TextView textView) {
            return (String)textView.getText();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(TextView object, String value) {
            object.setText(value);
        }
    }

    private static class SpannableTextProperty extends Property<TextView, Spannable> {
        public SpannableTextProperty() {
            super(Spannable.class, "spannableText");
        }

        @Override
        public Spannable get(TextView textView) {
            return ((SpannableStringBuilder)textView.getText());
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(TextView object, Spannable value) {
            object.setText(value, TextView.BufferType.SPANNABLE);
        }
    }

    private static class TextColorProperty extends Property<TextView, Integer>{

        public TextColorProperty() {
            super(Integer.class, "textColor");
        }

        @Override
        public Integer get(TextView textView) {
            return textView.getCurrentTextColor();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(TextView object, Integer value) {
            object.setTextColor(value);
        }
    }

    public static final Property<TextView, String> TEXT = new TextProperty();
    public static final Property<TextView, Spannable> TEXTSPANNABLE = new SpannableTextProperty();
    public static final Property<TextView, Integer> TEXTCOLOR = new TextColorProperty();

    public static void bindTextProperty(final TextView view, Observable<String> observable){
        ViewProperties.bindProperty(view, observable, TEXT, R.id.reactive_text_disposable);
    }

    public static void bindTextSpannableProperty(final TextView view, Observable<Spannable> observable){
        ViewProperties.bindProperty(view, observable, TEXTSPANNABLE, R.id.reactive_text_disposable);
    }

    public static void bindTextColorProperty(final TextView view, Observable<Integer> observable){
        ViewProperties.bindProperty(view, observable, TEXTCOLOR, R.id.reactive_text_color_disposable);
    }
}
