package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.ILoopingTask;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.errors.UnknownIncrementerRuntimeError;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.Reducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private static final String TAG = Incrementer.class.getSimpleName();
    private static final boolean DLOG = true;

    private final @NonNull String id;
    private final @NonNull IncrementerMetadata metadata;
    private final @Nullable LoopTaskManager taskManager;
    private final @NonNull PurchaseData purchaseData;
    private final @Nullable LoopData loopData;
    private final @NonNull IncrementerManager incrementerManager;
    private final HashMap<String, Double> multipliers = new HashMap<>();
    private final List<IIncrementerListener> listeners = new ArrayList<>();
    private final Runnable loopTaskRunnable;
    private double currentMultiplier;
    private double value = 0.0;
    private ILoopingTask loopTask;

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

    public static Incrementer create(@NonNull IncrementerScript arg, @NonNull IncrementerManager incManager,
                                     @Nullable LoopTaskManager taskMngr) {
        IncrementerMetadata meta = IncrementerMetadata.create(arg.getMetadata());
        PurchaseData purchase = PurchaseData.create(arg.getPurchaseData());
        LoopData loop = arg.getLoopData() == null ? null : new LoopData(arg.getLoopData());
        String id = arg.getId();

        return create(id, meta, purchase, loop, incManager, taskMngr);
    }

    public static Incrementer create(@NonNull String id, @NonNull IncrementerMetadata metadata,
                                     @NonNull PurchaseData purchaseData, @Nullable LoopData loopData,
                                     @NonNull IncrementerManager incManager, @Nullable LoopTaskManager taskMngr) {
        return new Incrementer(id, metadata, purchaseData, loopData, incManager, taskMngr);
    }

    private Incrementer(@NonNull String id, @NonNull IncrementerMetadata meta,
                        @NonNull PurchaseData purchase, @Nullable LoopData loop,
                        @NonNull IncrementerManager incManager, @Nullable LoopTaskManager taskMngr) {
        if (DLOG) Log.d(TAG, "init: " + id);
        this.id = id;
        this.taskManager = taskMngr;
        this.incrementerManager = incManager;
        currentMultiplier = calculateCurrentMultiplier();
        metadata = meta;
        purchaseData = purchase;
        loopData = loop;

        if (loopData != null) {
            if (DLOG) Log.d(TAG, id + " has loop data");
            loopTaskRunnable = new Runnable() {
                @Override
                public void run() {
                    double count = value;
                    for (Effect effect : loopData.getEffects()) {
                        String targetId = effect.getTargetId();
                        Incrementer inc = incrementerManager.getIncrementer(targetId);
                        if (inc == null) {
                            throw new UnknownIncrementerRuntimeError(targetId);
                        }
                        double change = effect.getValue() * count * currentMultiplier;
                        inc.applyChange(Incrementer.this.id, effect.getFunction(), change);
                    }
                }
            };

        } else {
            loopTaskRunnable = null;
        }
    }

    /**
     * Apply a change to this incrementer; the value will be used as-is, no multipliers will be applied
     * This method only accepts addition and subtraction functions; multiplication and division
     * should also supply an id
     *
     * @param function how the change should be applied, ie addition, subtraction
     * @param change the value of the change.
     */
    public void applyChange(Function function, double change) {
        if (DLOG) Log.d(TAG, id + " applyChange() - simple " + function + " " + change);
        switch(function) {
            case ADD:
                value += change;
                break;
            case SUB:
                value -= change;
                break;
            default:
                throw new IllegalStateException("unsupported operation applied to " + id
                        + " with function: " + function);
        }
        if (loopTask != null && value <= 0 && taskManager != null) {
            taskManager.stopLoopingTask(id);
            loopTask = null;
        }
        if (loopData != null && loopTask == null && value > 0 && taskManager != null) {
            loopTask = taskManager.startLoopingTask(id, loopData.getChargeTime(), loopTaskRunnable);
        }
        for (IIncrementerListener listener : listeners) {
            listener.onValueUpdate(value);
        }
    }

    /**
     * Apply a change to this incrementer; the value will be used as-is, no multipliers will be applied
     *
     * @param function how the change should be applied, ie addition, multiplication
     * @param change the value of the change.
     */
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

    public @Nullable Range getRange() {
        if (loopData == null) {
            // this incrementer won't have a progressbar
            return null;
        }
        if (loopTask == null) {
            // at this time, this progressbar isn't running
            return Range.empty();
        }
        // active progressbar
        return loopTask.getRange();
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void addListener(IIncrementerListener listener) {
        listeners.add(listener);
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
        // TODO: price check before executing purchase
        double factor = Math.pow(value + 1, purchaseData.getLevelFactor());
        for (Effect effect : purchaseData.getBaseCosts()) {
            Incrementer inc = incrementerManager.getIncrementer(effect.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(effect.getTargetId());
            }
            double change = effect.getValue() * factor;
            if (DLOG) Log.d(TAG, "> applying cost effect " + effect.getTargetId()
                    + " " + change + " (base " + effect.getValue()
                    + " count " + value + " factor " + factor + ")");
            inc.applyChange(effect.getFunction(), change);
        }
        for (Effect effect : purchaseData.getEffects()) {
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
