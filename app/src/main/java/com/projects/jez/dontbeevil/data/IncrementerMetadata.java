package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.IncrementerMetadataScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class IncrementerMetadata {

    private final String title;
    private final String caption;
    private final int sortOrder;

    public IncrementerMetadata(IncrementerMetadataScript metadata) {
        title = metadata.getTitle();
        caption = metadata.getCaption();
        sortOrder = metadata.getSortOrder();
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
