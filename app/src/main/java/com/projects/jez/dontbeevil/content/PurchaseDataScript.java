package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class PurchaseDataScript {
    @SerializedName("base_cost")
    List<EffectScript> baseCost;
    @SerializedName("per_level_factor")
    List<EffectScript> perLevelCostFactor;
    List<EffectScript> effects;

    public List<EffectScript> getBaseCost() {
        return baseCost;
    }

    public List<EffectScript> getPerLevelCostFactor() {
        return perLevelCostFactor;
    }

    public List<EffectScript> getEffects() {
        return effects;
    }
}
