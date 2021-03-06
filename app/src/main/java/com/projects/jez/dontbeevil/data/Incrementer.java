package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.DebugConfig;
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
import java.util.HashSet;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer implements IIncrementerUpdater {
    private static final boolean DEBUG_ALLOW_INVALID_PURCHASE_ACTIONS = DebugConfig.DEBUG_ALLOW_INVALID_PURCHASE_ACTIONS;

    private final @NonNull String id;
    private final @NonNull Metadata metadata;
    private final @Nullable LoopTaskManager taskManager;
    private final @NonNull PurchaseData purchaseData;
    private final @Nullable LoopData loopData;
    private final @NonNull IncrementerManager incrementerManager;
    private final HashMap<String, Double> multipliers = new HashMap<>();
    private final HashSet<String> disabledEffects = new HashSet<>();
    private final HashMap<String, Incrementer> effectMultipliers = new HashMap<>();
    private final boolean isUpgrade;
    private @Nullable IIncrementerListener listener;
    private final Runnable loopTaskRunnable;
    private double currentMultiplier;
    private double value = 0.0;
    private ILoopingTask loopTask;

    public double getValue() {
        return value;
    }

    @Override
    public String getCostName() {
        return purchaseData.getBaseCost().getTargetId();
    }

    @Override
    @Nullable
    public Long getCostValue() {
        return getCurrentCost();
    }

    public enum Function {
        VALUE("value"),
        MULTIPLIER("multi")
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
                                     @Nullable LoopTaskManager taskMngr, boolean isUpgrade) {
        Metadata meta = Metadata.create(arg.getMetadata());
        PurchaseData purchase = PurchaseData.create(arg.getPurchaseData());
        LoopData loop = LoopData.create(arg.getLoopData());
        String id = arg.getId();

        return create(id, meta, purchase, loop, incManager, taskMngr, isUpgrade);
    }

    public static Incrementer create(@NonNull String id, @NonNull Metadata metadata, @NonNull PurchaseData purchaseData,
                                     @Nullable LoopData loopData, @NonNull IncrementerManager incManager,
                                     @Nullable LoopTaskManager taskMngr, boolean isUpgrade) {
        return new Incrementer(id, metadata, purchaseData, loopData, incManager, taskMngr, isUpgrade);
    }

    private Incrementer(@NonNull String id, @NonNull Metadata meta, @NonNull PurchaseData purchase,
                        @Nullable LoopData loop, @NonNull IncrementerManager incManager,
                        @Nullable LoopTaskManager taskMngr, boolean isUpgrade) {
        Logger.d(this, "init: " + id);
        this.id = id;
        this.taskManager = taskMngr;
        this.incrementerManager = incManager;
        this.isUpgrade = isUpgrade;
        currentMultiplier = calculateCurrentMultiplier();
        metadata = meta;
        purchaseData = purchase;
        loopData = loop;

        if (loopData != null) {
            Logger.d(this, id + " has loop data");

            // populate set of disabled effects
            for (Effect effect : loopData.getEffects()) {
                if (effect.isDisabled()) {
                    disabledEffects.add(effect.getTargetId());
                }
            }

            loopTaskRunnable = new Runnable() {
                @Override
                public void run() {
                    double count = value;
                    for (Effect effect : loopData.getEffects()) {
                        if (disabledEffects.contains(effect.getTargetId())) {
                            continue;
                        }
                        String targetId = effect.getTargetId();
                        Incrementer inc = incrementerManager.getIncrementer(targetId);
                        if (inc == null) {
                            throw new UnknownIncrementerRuntimeError(targetId);
                        }
                        Incrementer affector = effectMultipliers.get(effect.getTargetId());
                        double affectorMultiplier = 1f;
                        if (affector != null) {
                            Logger.d(this, "affector multiplier: " + affector.getValue());
                            affectorMultiplier = affector.getValue();
                        }
                        double change = effect.getValue() * count * currentMultiplier * affectorMultiplier;
                        inc.applyChange(Incrementer.this.id, effect.getFunction(), change);
                    }
                }
            };

        } else {
            loopTaskRunnable = null;
        }
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
        updateListener();
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
            case VALUE:
                modifyValue(change);
                break;
            case MULTIPLIER:
                applyMultiplier(applierId, change);
                break;
            default:
                Logger.e(this, "unsupported operation when lacking id: " + function);
        }
    }

    /**
     * Multipliers are tracked by source id, so the source of each change is accountable.
     * Multiplier values are used as a factor, so '0.5' would be a 50% increase, '-1' would be a
     * 100% decrease.  All multiplier values are summed to get the final factor to apply to the
     * incrementer's effects when it loops.
     *
     * @param applierId Id of source of new value
     * @param change value to add to the multiplication factor
     */
    private void applyMultiplier(String applierId, double change) {
        if (multipliers.containsKey(applierId)) {
            Logger.i(this, "updating multiplier " + applierId);
            multipliers.put(applierId, multipliers.get(applierId) + change);
        } else {
            Logger.i(this, "applying multiplier " + applierId);
            multipliers.put(applierId, change);
        }
        currentMultiplier = calculateCurrentMultiplier();

        updateListener();
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

    public void attachListener(@Nullable IIncrementerListener listener) {
        this.listener = listener;
        updateListener();
    }

    private void updateListener() {
        if (listener != null) {
            listener.onUpdate(this);
        }
    }

    public String getTitle() {
        return metadata.getTitle();
    }

    public String getCaption() {
        return metadata.getCaption();
    }

    public Integer getSortOrder() {
        return isUpgrade ? -metadata.getSortOrder() : metadata.getSortOrder();
    }

    /**
     * Exposed for tests
     *
     * @return current factor to apply to base purchase costs
     */
    double getPurchaseFactor() {
        return Math.pow(value + 1, purchaseData.getLevelFactor());
    }

    @Nullable
    private Long getCurrentCost() {
        double factor = getPurchaseFactor();
        Effect baseCost = purchaseData.getBaseCost();
        if (baseCost != null) {
            switch (baseCost.getFunction()) {
                case VALUE:
                    return Math.round(baseCost.getValue() * factor);
                default:
                    // ignore multiplier for cost effects value
                    Logger.d(this, "> ignoring effect function " + baseCost.getFunction() + " " + baseCost.getTargetId());
                    return null;
            }
        }
        return null;
    }

    public boolean preformPurchaseActions() {
        Logger.d(this, id + " preformPurchaseActions()");

        Long cost = getCurrentCost();
        Effect baseCost = purchaseData.getBaseCost();
        if (null != cost && null != baseCost) {
            String targetId = baseCost.getTargetId();
            Incrementer inc = incrementerManager.getIncrementer(targetId);
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(targetId);
            }

            boolean canApply = inc.canApplyChange(cost);
            if (!canApply) {
                Logger.d(this, "> unable to make purchase");
                return false;
            }
            boolean changeApplied = inc.modifyValue(cost);
            if (!changeApplied) {
                throw new IllegalStateException("Attempted to apply change failed: " + cost + " targeting " + inc.getId() + " with current value " + inc.getValue());
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

        for (Toggle toggle : purchaseData.getToggles()) {
            String targetId = toggle.getTargetId();
            Incrementer inc = incrementerManager.getIncrementer(targetId);
            if (inc == null) {
                throw new UnknownIncrementerRuntimeError(targetId);
            }
            String effectId = toggle.getEffectId();
            boolean enable = toggle.isEnable();
            inc.toggle(effectId, this, enable);
        }

        if (purchaseData.isUnique()) {
            Logger.d(this, "removing unique incrementer " + id);
            incrementerManager.removeIncrementer(this);
        }
        return true;
    }

    private void toggle(String effectId, Incrementer incrementer, boolean enable) {
        boolean noChange = enable && !disabledEffects.contains(effectId);
        if (noChange) {
            return;
        }
        if (enable) {
            disabledEffects.remove(effectId);
            effectMultipliers.put(effectId, incrementer);
        } else {
            disabledEffects.add(effectId);
            effectMultipliers.remove(effectId);
        }
    }
}
