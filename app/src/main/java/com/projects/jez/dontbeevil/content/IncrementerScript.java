package com.projects.jez.dontbeevil.content;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jez on 25/03/2016.
 */
public class IncrementerScript {
    String id;
    IncrementerMetadataScript metadata;
    @SerializedName("loop_data") @Nullable
    LoopDataScript loopData;
    @SerializedName("purchase_data")
    PurchaseDataScript purchaseData;

    public String getId() {
        return id;
    }

    public IncrementerMetadataScript getMetadata() {
        return metadata;
    }

    public PurchaseDataScript getPurchaseData() {
        return purchaseData;
    }

    public LoopDataScript getLoopData() {
        return loopData;
    }
}
