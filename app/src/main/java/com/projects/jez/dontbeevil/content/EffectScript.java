package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jez on 25/03/2016.
 */
public class EffectScript {
    @SerializedName("target_id")
    String targetId;
    double value;

    public String getTargetId() {
        return targetId;
    }

    public double getValue() {
        return value;
    }
}
