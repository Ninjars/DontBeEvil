package com.projects.jez.utils.react;

import android.util.Property;
import android.widget.CompoundButton;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Observable;

public class CompoundButtonProperties {
    public static class CheckedProperty extends Property<CompoundButton, Boolean>{
        public CheckedProperty(){
            super(Boolean.class, "checked");
        }

        @Override
        public Boolean get(CompoundButton compoundButton) {
            return compoundButton.isChecked();
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public void set(CompoundButton object, Boolean value) {
            object.setChecked(value);
        }
    }

    public static final Property<CompoundButton, Boolean> CHECKED = new CheckedProperty();

    public static void bindCheckedProperty(final CompoundButton compoundButton, Observable<Boolean> observable){
        ViewProperties.bindProperty(compoundButton, observable, CHECKED, R.id.reactive_checked_disposable);
    }
}
