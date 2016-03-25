package com.projects.jez.dontbeevil.data;

import android.util.Log;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.utils.observable.Observable;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private static final String TAG = Incrementer.class.getSimpleName();
    private static final boolean DLOG = true;

    private final String id;
    private final IncrementableValue value = new IncrementableValue();
    private final IncrementerMetadata metadata;
    private final LoopTaskManager taskManager;
    private final PurchaseData purchaseData;
    private final LoopData loopData;

    public Incrementer(IncrementerScript arg, LoopTaskManager taskManager) {
        this.taskManager = taskManager;
        id = arg.getId();
        metadata = new IncrementerMetadata(arg.getMetadata());
        purchaseData = new PurchaseData(arg.getPurchaseData());
        loopData = arg.getLoopData() == null ? null : new LoopData(arg.getLoopData());
    }

    public String getId() {
        return id;
    }

    public void increment() {
        if (DLOG) Log.d(TAG, "increment() " + id);
        value.increment();
    }

    public Observable<Long> getValue() {
        return value.getValue();
    }

    public String getTitle() {
        return metadata.getTitle();
    }

    public String getCaption() {
        return metadata.getCaption();
    }

    public Integer getSortOrder() {
        return metadata.getSortOrder();
    }
}
