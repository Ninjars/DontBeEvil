package com.projects.jez.dontbeevil.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class PurchaseDataScript {
    @SerializedName("base_cost")
    private EffectScript baseCost;
    @SerializedName("level_multiplier")
    private double levelFactor = 1.0;
    private List<EffectScript> effects;
    private List<ToggleScript> toggles;
    @SerializedName("is_unique")
    private boolean isUnique;

    @Nullable
    public EffectScript getBaseCost() {
        return baseCost;
    }

    public double getLevelFactor() {
        return levelFactor;
    }

    public boolean isUnique() {
        return isUnique;
    }

    @NonNull
    public List<EffectScript> getEffects() {
        return effects == null ? new ArrayList<EffectScript>() : effects;
    }

    @Nullable
    public List<ToggleScript> getToggles() {
        return toggles;
    }
}
