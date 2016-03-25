package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class IncrementerMetadataScript {
    @SerializedName("title")
    String title;
    @SerializedName("plays")
    String caption;
    @SerializedName("sort_order")
    Integer sortOrder;
    @SerializedName("base_cost")
    List<CostScript> baseCost;
    @SerializedName("per_level_factor")
    List<CostScript> perLevelCostFactor;
    @SerializedName("loop_period")
    Long loopPeriod;

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public List<CostScript> getBaseCost() {
        return baseCost;
    }

    public List<CostScript> getPerLevelCostFactor() {
        return perLevelCostFactor;
    }

    public Long getLoopPeriod() {
        return loopPeriod;
    }
}
