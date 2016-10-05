package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.engine.ILoopingTask;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.errors.UnknownIncrementerRuntimeError;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.Logger;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.Reducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private static final boolean DEBUG_ALLOW_INVALID_PURCHASE_ACTIONS = false;

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

    public double getValue() {
        return value;
    }

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
        public static Function getFunctionFromKey(@Nullable String key) {
            if (key == null) {
                return null;
            }
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
        LoopData loop = LoopData.create(arg.getLoopData());
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
        Logger.d(this, "init: " + id);
        this.id = id;
        this.taskManager = taskMngr;
        this.incrementerManager = incManager;
        currentMultiplier = calculateCurrentMultiplier();
        metadata = meta;
        purchaseData = purchase;
        loopData = loop;

        if (loopData != null) {
            Logger.d(this, id + " has loop data");
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

    public boolean canApplyChange(Function function, double change) {
        switch(function) {
            case ADD:
                // no action
                break;
            case SUB:
                change = -change;
                break;
            default:
                return true;
        }
        return canApplyChange(change);
    }

    public boolean canApplyChange(double change) {
        //noinspection SimplifiableConditionalExpression
        return DEBUG_ALLOW_INVALID_PURCHASE_ACTIONS ? true : value + change >= 0;
    }

    /**
     * Apply a change to this incrementer; the value will be used as-is, no multipliers will be applied
     * This method only accepts addition and subtraction functions; multiplication and division
     * should also supply an id
     *
     * @param change the value of the change.
     */
    public boolean modifyValue(double change) {
        Logger.d(this, id + " modifyValue() " + change);
        if (!canApplyChange(change)) {
            return false;
        }
        value += change;
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
        return true;
    }

    /**
     * Apply a change to this incrementer; the value will be used as-is, no multipliers will be applied
     *
     * @param function how the change should be applied, ie addition, multiplication
     * @param change the value of the change.
     */
    public void applyChange(@NonNull String applierId, Function function, double change) {
        Logger.d(this, id + " applyChange() " + function + " " + change);
        switch(function) {
            case ADD:
                modifyValue(change);
                break;
            case SUB:
                modifyValue(-change);
                break;
            case MULT:
                applyMultiplier(applierId, change);
            case DIV:
                applyMultiplier(applierId, 1.0 / change);
                break;
            default:
                Logger.e(this, "unsupported operation when lacking id: " + function);
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

    /**
     * Exposed for tests
     *
     * @return current factor to apply to base purchase costs
     */
    double getPurchaseFactor() {
        return Math.pow(value + 1, purchaseData.getLevelFactor());
    }

    public boolean preformPurchaseActions() {
        Logger.d(this, id + " preformPurchaseActions()");
        double factor = getPurchaseFactor();
        Effect baseCost = purchaseData.getBaseCost();
        if (baseCost != null) {
            Incrementer inc = incrementerManager.getIncrementer(baseCost.getTargetId());
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(baseCost.getTargetId());
            }
            double effectValue = 0.0;
            switch (baseCost.getFunction()) {
                case ADD:
                    effectValue = baseCost.getValue() * factor;
                    break;
                case SUB:
                    effectValue = -baseCost.getValue() * factor;
                    break;
                default:
                    // ignore divide and multiplier for cost effects value
                    Logger.d(this, "> ignoring effect function " + baseCost.getFunction() + " " + baseCost.getTargetId());
            }
            boolean canApply = inc.canApplyChange(effectValue);
            if (!canApply) {
                Logger.d(this, "> unable to make purchase");
                return false;
            }
            boolean changeApplied = inc.modifyValue(effectValue);
            if (!changeApplied) {
                throw new IllegalStateException("Attempted to apply change failed: " + effectValue + " targeting " + inc.getId() + " with current value " + inc.getValue());
            }
        }

        for (Effect effect : purchaseData.getEffects()) {
            String targetId = effect.getTargetId();
            Incrementer inc = incrementerManager.getIncrementer(targetId);
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(targetId);
            }
            double change = effect.getValue();
            Logger.d(this, "> applying effect " + effect.getTargetId() + " " + change);
            inc.applyChange(id, effect.getFunction(), change);
        }
        return true;
    }
}
