package com.projects.jez.dontbeevil.content;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class PurchaseDataScript {
    @SerializedName("base_cost")
    List<EffectScript> baseCost;
    @SerializedName("level_multiplier")
    double levelFactor = 1.0;
    List<EffectScript> effects;

    @NonNull
    public List<EffectScript> getBaseCost() {
        return baseCost == null ? new ArrayList<EffectScript>() : baseCost;
    }

    public double getLevelFactor() {
        return levelFactor;
    }

    @NonNull
    public List<EffectScript> getEffects() {
        return effects == null ? new ArrayList<EffectScript>() : effects;
    }
}
