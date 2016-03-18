package com.projects.jez.utils.react;

import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.util.Property;
import android.view.View;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Observer;

public class ViewProperties {
    public static class EnabledProperty extends Property<View, Boolean>{
        public EnabledProperty() {
            super(Boolean.class, "enabled");
        }

        @Override
        public Boolean get(View view) {
            return view.isEnabled();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(View object, Boolean value) {
            object.setEnabled(value);
        }
    }

    public static class VisibilityProperty extends Property<View, Integer>{
        public VisibilityProperty() {
            super(Integer.class, "visibility");
        }

        @Override
        public Integer get(View view) {
            return view.getVisibility();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(View object, Integer value) {
            object.setVisibility(value);
        }
    }

    public static class BackgroundColorProperty extends Property<View, Integer> {
        public BackgroundColorProperty() {
            super(Integer.class, "backgroundColor");
        }

        @Override
        public Integer get(View view) {
            return ((ColorDrawable) view.getBackground()).getColor();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(View view, Integer value) {
            view.setBackgroundColor(value);
        }
    }

    public static final Property<View, Boolean> ENABLED = new EnabledProperty();
    public static final Property<View, Integer> VISIBILITY = new VisibilityProperty();
    public static final Property<View, Integer> BACKGROUND = new BackgroundColorProperty();

    public static void bindEnabledProperty(final View view, Observable<Boolean> observable){
        bindProperty(view, observable, ENABLED, R.id.reactive_enabled_disposable);
    }

    public static void bindVisibilityProperty(final View view, Observable<Integer> observable){
        bindProperty(view, observable, VISIBILITY, R.id.reactive_visibility_disposable);
    }

    public static void bindBackgroundColorProperty(final View view, Observable<Integer> observable) {
        bindProperty(view, observable, BACKGROUND, R.id.reactive_background_color_disposable);
    }

    static <S extends View, T> void bindProperty(final S view, Observable<T> observable, final Property<S, T> property, int tagId){
        Disposable disposable = (Disposable)view.getTag(tagId);

        if(disposable != null){
            disposable.dispose();
        }

        if(observable != null){
            disposable = observable.addWeakObserverImmediate(view, new Observer<Pair<S, T>>() {
                @Override
                public void observe(Pair<S, T> arg) {
                    property.set(arg.first, arg.second);
                }
            });
            view.setTag(tagId, disposable);
        }
    }
}
