package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.LoopingTask;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.errors.UnknownIncrementerRuntimeError;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.Reducer;

import java.util.ArrayList;
import java.util.HashMap;

import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private static final String TAG = Incrementer.class.getSimpleName();
    private static final boolean DLOG = true;

    private final String id;
    private final BehaviorSubject<Double> rxValue = BehaviorSubject.create(0.0);
    private final IncrementerMetadata metadata;
    private final LoopTaskManager taskManager;
    private final PurchaseData purchaseData;
    private final LoopData loopData;
    private final IncrementerManager incrementerManager;
    private final rx.Observable<Box<LoopingTask>> loopTask;
    private final HashMap<String, Double> multipliers = new HashMap<>();
    private double currentMultiplier;

    public enum Function {
        ADD("+"),
        SUB("-"),
        MULT("*"),
        DIV("/")
        ;
        String key;
        Function(String key) {
            this.key = key;
        }
        public static Function getFunctionFromKey(String key) {
            for (Function v : values()) {
                if (v.key.equals(key)) {
                    return v;
                }
            }
            throw new RuntimeException("no match found for key " + key);
        }
    }

    public Incrementer(IncrementerScript arg, IncrementerManager incManager, LoopTaskManager taskMngr) {
        if (DLOG) Log.d(TAG, "init() " + arg.getId());
        this.taskManager = taskMngr;
        this.incrementerManager = incManager;
        currentMultiplier = calculateCurrentMultiplier();
        id = arg.getId();
        metadata = new IncrementerMetadata(arg.getMetadata());
        purchaseData = new PurchaseData(arg.getPurchaseData());
        loopData = arg.getLoopData() == null ? null : new LoopData(arg.getLoopData());

        if (loopData != null) {
            if (DLOG) Log.d(TAG, id + " has loop data");
            // only really want to evaluate when we have the first value sent through
            loopTask = getValue().filter(new Func1<Double, Boolean>() {
                @Override
                public Boolean call(Double aDouble) {
                    if (DLOG) Log.d(TAG, id + " take first filter: " + aDouble + " passes? " + (aDouble > 0));
                    return aDouble > 0;
                }
            }).first().map(new Func1<Double, Box<LoopingTask>>() {
                @Override
                public Box<LoopingTask> call(Double arg) {
                    if (DLOG) Log.d(TAG, id + " loopTask populating");
                    return new Box<>(taskManager.startLoopingTask(id, loopData.getChargeTime(), new Runnable() {
                        @Override
                        public void run() {
                            double count = rxValue.getValue();
                            for (Effect effect : loopData.getEffects()) {
                                String targetId = effect.getTargetId();
                                Incrementer inc = incrementerManager.getIncrementer(targetId);
                                if (inc == null) {
                                    throw new UnknownIncrementerRuntimeError(targetId);
                                }
                                double change = effect.getValue() * count * currentMultiplier;
                                inc.applyChange(id, effect.getFunction(), change);
                            }

                        }
                    }));
                }
            }).defaultIfEmpty(new Box<LoopingTask>(null));

        } else {
            loopTask = null;
        }
    }

    public void applyChange(Function function, double change) {
        if (DLOG) Log.d(TAG, id + " applyChange() - simple " + function + " " + change);
        switch(function) {
            case ADD:
                rxValue.onNext(rxValue.getValue() + change);
                break;
            case SUB:
                rxValue.onNext(rxValue.getValue() - change);
                break;
            default:
                Log.e(TAG, "unsupported operation when lacking id: " + function);
        }
    }

    public void applyChange(@NonNull String applierId, Function function, double change) {
        if (DLOG) Log.d(TAG, id + " applyChange() " + function + " " + change);
        switch(function) {
            case ADD:
            case SUB:
                applyChange(function, change);
                break;
            case MULT:
                applyMultiplier(applierId, change);
            case DIV:
                applyMultiplier(applierId, 1.0 / change);
                break;
            default:
                Log.e(TAG, "unsupported operation when lacking id: " + function);
        }
    }

    private void applyMultiplier(String applierId, double change) {
        multipliers.put(applierId, change);
        currentMultiplier = calculateCurrentMultiplier();
    }

    private double calculateCurrentMultiplier() {
        return MapperUtils.reduce(new ArrayList<>(multipliers.values()), 1.0, new Reducer<Double, Double>() {
            @Override
            public Double reduce(Double accumulatedValue, Double deltaValue) {
                return accumulatedValue + deltaValue;
            }
        });
    }

    @Nullable
    public rx.Observable<Box<Range>> getRangeObservable() {
        if (loopTask == null) return null;
        return loopTask.flatMap(new Func1<Box<LoopingTask>, rx.Observable<Box<Range>>>() {
            @Override
            public rx.Observable<Box<Range>> call(Box<LoopingTask> arg) {
                return arg.valueNotNull() ? arg.getValue().getRangeObservable() : rx.Observable.<Box<Range>>empty().defaultIfEmpty(new Box<Range>(null));
            }
        });
    }

    public String getId() {
        return id;
    }

    public rx.Observable<Double> getValue() {
        return rxValue.asObservable();
    }

    private Double getCurrentValue() {
        return rxValue.getValue();
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
        if (DLOG) Log.d(TAG, id + " preformPurchaseActions()");
        double factor = Math.pow(getCurrentValue(), purchaseData.getLevelFactor());
        for (Effect effect : purchaseData.getBaseCosts()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue() * factor;
            if (DLOG) Log.d(TAG, "> applying cost effect " + effect.getTargetId() + " " + change);
            inc.applyChange(effect.getFunction(), change);
        }
        for (Effect effect : purchaseData.getEffect()) {
            String targetId = effect.getTargetId();
            Incrementer inc = incrementerManager.getIncrementer(targetId);
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(targetId);
            }
            double change = effect.getValue();
            if (DLOG) Log.d(TAG, "> applying effect " + effect.getTargetId() + " " + change);
            inc.applyChange(id, effect.getFunction(), change);
        }
    }
}
