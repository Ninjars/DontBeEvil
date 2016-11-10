package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.MetadataScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class Metadata {

    private final String title;
    private final String caption;
    private final int sortOrder;

    public static Metadata create(MetadataScript metadata) {
        return Metadata.create(metadata.getTitle(), metadata.getCaption(), metadata.getSortOrder());
    }

    public static Metadata create(String title, String caption, Integer sortOrder) {
        return new Metadata(title, caption, sortOrder);
    }

    private Metadata(String title, String caption, Integer sortOrder) {
        this.title = title;
        this.caption = caption;
        this.sortOrder = sortOrder;
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
}
