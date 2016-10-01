package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.IncrementerMetadataScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class IncrementerMetadata {

    private final String title;
    private final String caption;
    private final int sortOrder;

    public static IncrementerMetadata create(IncrementerMetadataScript metadata) {
        return IncrementerMetadata.create(metadata.getTitle(), metadata.getCaption(), metadata.getSortOrder());
    }

    public static IncrementerMetadata create(String title, String caption, Integer sortOrder) {
        return new IncrementerMetadata(title, caption, sortOrder);
    }

    private IncrementerMetadata(String title, String caption, Integer sortOrder) {
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
