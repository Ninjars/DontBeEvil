package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class LoopDataScript {
    @SerializedName("charge_time")
    long chargeTime;
    List<EffectScript> effects;

    public long getChargeTime() {
        return chargeTime;
    }

    public List<EffectScript> getEffects() {
        return effects;
    }
}
