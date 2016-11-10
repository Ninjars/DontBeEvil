package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.content.EffectScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class Effect {
    private final String targetId;
    private final double value;
    private final Incrementer.Function function;
    private final boolean isDisabled;

    @Nullable
    public static Effect create(@Nullable EffectScript effect) {
        if (effect == null) {
            return null;
        }
        return Effect.create(effect.getTargetId(), effect.getValue(),
                Incrementer.Function.getFunctionFromKey(effect.getFunction()),
                effect.isDisabled());
    }

    public static Effect create(String targetId, double value, @Nullable Incrementer.Function function, boolean isDisabled) {
        return new Effect(targetId, value, function, isDisabled);
    }

    private Effect(String targetId, double value, @Nullable Incrementer.Function function, boolean isDisabled) {
        this.targetId = targetId;
        this.value = value;
        this.function = function == null ? Incrementer.Function.VALUE : function;
        this.isDisabled = isDisabled;
    }

    @Override
    public String toString() {
        return "<" + Effect.class.getSimpleName() + " " + targetId + " " + value + ">";
    }

    @Override
    public int hashCode() {
        return 13 + targetId.hashCode() + function.hashCode() + (int) value + (isDisabled ? 0 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Effect && hashCode() == obj.hashCode();
    }

    public String getTargetId() {
        return targetId;
    }

    public double getValue() {
        return value;
    }

    public Incrementer.Function getFunction() {
        return function;
    }

    public boolean isDisabled() {
        return isDisabled;
    }
}
