package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.LoopingTask;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.errors.UnknownIncrementerRuntimeError;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Source;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private static final String TAG = Incrementer.class.getSimpleName();
    private static final boolean DLOG = true;

    private final String id;
    private final Source<Double> value = new Source<>(0.0);
    private final IncrementerMetadata metadata;
    private final LoopTaskManager taskManager;
    private final PurchaseData purchaseData;
    private final LoopData loopData;
    private final IncrementerManager incrementerManager;
    private final LoopingTask loopTask;

    public Incrementer(IncrementerScript arg, IncrementerManager incManager, LoopTaskManager taskManager) {
        this.taskManager = taskManager;
        this.incrementerManager = incManager;
        id = arg.getId();
        metadata = new IncrementerMetadata(arg.getMetadata());
        purchaseData = new PurchaseData(arg.getPurchaseData());
        loopData = arg.getLoopData() == null ? null : new LoopData(arg.getLoopData());
        if (loopData != null) {
            loopTask = taskManager.startLoopingTask(id, loopData.getChargeTime(), new Runnable() {
                @Override
                public void run() {
                    @SuppressWarnings("ConstantConditions")
                    double multiplier = getCurrentValue();
                    for (Effect effect : loopData.getEffects()) {
                        Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
                        if (inc == null) {
                            throw new UnknownIncrementerRuntimeError(effect.getTargetId());
                        }
                        double change = effect.getValue() * multiplier;
                        inc.addValue(change);
                    }

                }
            });
        } else {
            loopTask = null;
        }
    }

    @Nullable
    public Observable<Box<Range>> getRangeObservable() {
        if (loopTask == null) return null;
        return loopTask.getRangeObservable();
    }

    public String getId() {
        return id;
    }

    public Observable<Double> getValue() {
        return value.getObservable();
    }

    public void addValue(double change) {
        value.put(getCurrentValue() + change);
    }

    @SuppressWarnings("ConstantConditions")
    private double getCurrentValue() {
        return value.getObservable().getCurrent();
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

    public void preformPurchaseActions() {
        double multiplier = getCurrentValue();
        for (Effect effect : purchaseData.getBaseCosts()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue() * multiplier;
            inc.addValue(-change);
        }
        for (Effect effect : purchaseData.getPerLevelEffects()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue();
            inc.addValue(change);
        }
    }
}
