package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.IncrementerMetadataScript;
import com.projects.jez.utils.MapperUtils;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class IncrementerMetadata {

    private final String title;
    private final String caption;
    private final int sortOrder;
    private final long chargeTime;
    private final boolean autoLooping;
    private final List<Cost> baseCost;
    private final List<Cost> perLevelFactor;

    public IncrementerMetadata(IncrementerMetadataScript metadata) {
        title = metadata.getTitle();
        caption = metadata.getCaption();
        sortOrder = metadata.getSortOrder();
        chargeTime = metadata.getChargeTime();
        autoLooping = metadata.isAutoLooping();
        baseCost = MapperUtils.map(metadata.getBaseCost(), Cost.costScriptMapper);
        perLevelFactor = MapperUtils.map(metadata.getPerLevelCostFactor(), Cost.costScriptMapper);
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public long getChargeTime() {
        return chargeTime;
    }

    public boolean isAutoLooping() {
        return autoLooping;
    }

    public List<Cost> getBaseCost() {
        return baseCost;
    }

    public List<Cost> getPerLevelFactor() {
        return perLevelFactor;
    }
}
