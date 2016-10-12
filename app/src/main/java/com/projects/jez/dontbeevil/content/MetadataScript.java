package com.projects.jez.dontbeevil.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jez on 25/03/2016.
 */
public class MetadataScript {
    @SerializedName("title")
    String title;
    @SerializedName("caption")
    String caption;
    @SerializedName("sort_order")
    Integer sortOrder;

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}
