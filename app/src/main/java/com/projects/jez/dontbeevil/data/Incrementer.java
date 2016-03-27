package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.LoopingTask;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.errors.UnknownIncrementerRuntimeError;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.Reducer;
import com.projects.jez.utils.observable.Mapper;
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
    private final Observable<Box<LoopingTask>> loopTask;

    public Incrementer(IncrementerScript arg, IncrementerManager incManager, LoopTaskManager taskMngr) {
        if (DLOG) Log.d(TAG, "init() " + arg.getId());
        this.taskManager = taskMngr;
        this.incrementerManager = incManager;
        id = arg.getId();
        metadata = new IncrementerMetadata(arg.getMetadata());
        purchaseData = new PurchaseData(arg.getPurchaseData());
        loopData = arg.getLoopData() == null ? null : new LoopData(arg.getLoopData());
        if (loopData != null) {
            // only really want to evaluate when we have the first value sent through
            loopTask = getValue().reduce(0.0, new Reducer<Double, Double>() {
                @Override
                public Double reduce(Double old, Double newVal) {
                    return old == 0 || newVal == null ? newVal : null;
                }
            }).map(new Mapper<Double, Box<LoopingTask>>() {
                @Override
                public Box<LoopingTask> map(Double arg) {
                    if ((loopTask == null
                            || loopTask.getCurrent() == null
                            || loopTask.getCurrent().getValue() == null) && arg > 0) {
                        return new Box<>(taskManager.startLoopingTask(id, loopData.getChargeTime(), new Runnable() {
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
                        }));
                    } else {
                        return new Box<>(null);
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
        return Observable.flatten(loopTask.map(new Mapper<Box<LoopingTask>, Observable<Box<Range>>>() {
            @Override
            public Observable<Box<Range>> map(Box<LoopingTask> arg) {
                return arg.valueNotNull() ? arg.getValue().getRangeObservable() : new Observable<>(new Box<Range>(null));
            }
        }));
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
        if (DLOG) Log.d(TAG, "preformPurchaseActions()");
        double multiplier = getCurrentValue();
        for (Effect effect : purchaseData.getBaseCosts()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue() * multiplier;
            if (DLOG) Log.d(TAG, "> applying effect " + effect.getTargetId() + " " + -change);
            inc.addValue(-change);
        }
        for (Effect effect : purchaseData.getEffect()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue();
            if (DLOG) Log.d(TAG, "> applying effect " + effect.getTargetId() + " " + change);
            inc.addValue(change);
        }
    }
}
