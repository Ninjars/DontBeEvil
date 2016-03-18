package com.projects.jez.utils.react;

import android.util.Property;
import android.widget.Button;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Observable;

public class ButtonProperties {

    private static class TextProperty extends Property<Button, String>{

        public TextProperty() {
            super(String.class, "text");
        }

        @Override
        public String get(Button view) {
            return (String)view.getText();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(Button object, String value) {
            object.setText(value);
        }
    }

    public static final Property<Button, String> TEXT = new TextProperty();

    public static void bindTextProperty(final Button view, Observable<String> observable){
        ViewProperties.bindProperty(view, observable, TEXT, R.id.reactive_text_disposable);
    }
}
