package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jez on 25/03/2016.
 */
public class ToggleScript {
    @SerializedName("target_id")
    String targetId;
    @SerializedName("effect_id")
    String effectId;
    boolean enable;

    public String getTargetId() {
        return targetId;
    }

    public String getEffectId() {
        return effectId;
    }

    public boolean enable() {
        return enable;
    }
}
