package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jez on 25/03/2016.
 */
public class EffectScript {
    @SerializedName("target_id")
    String targetId;
    double value;
    String type;
    boolean disabled;

    public String getTargetId() {
        return targetId;
    }

    public double getValue() {
        return value;
    }

    public String getFunction() {
        return type;
    }

    public boolean isDisabled() {
        return disabled;
    }
}
