package com.projects.jez.utils.react;

import android.util.Property;
import android.widget.ProgressBar;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Observable;

public class ProgressbarProperties {

    private static class ProgressProperty extends Property<ProgressBar, Integer> {
        public ProgressProperty() {
            super(Integer.class, "progress");
        }

        @Override
        public Integer get(ProgressBar object) {
            return object.getProgress();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(ProgressBar object, Integer value) {
            object.setProgress(value);
        }
    }

    private static class MaxValueProperty extends Property<ProgressBar, Integer> {
        public MaxValueProperty() {
            super(Integer.class, "max_value");
        }

        @Override
        public Integer get(ProgressBar object) {
            return object.getMax();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(ProgressBar object, Integer value) {
            object.setMax(value);
        }
    }

    public static final Property<ProgressBar, Integer> PROGRESS = new ProgressProperty();
    public static final Property<ProgressBar, Integer> MAX = new MaxValueProperty();

    public static void bindProgressProperty(final ProgressBar view, Observable<Integer> observable){
        ViewProperties.bindProperty(view, observable, PROGRESS, R.id.reactive_progress_disposable);
    }

    public static void bindMaxProperty(final ProgressBar view, Observable<Integer> observable){
        ViewProperties.bindProperty(view, observable, MAX, R.id.reactive_progress_max_disposable);
    }
}
