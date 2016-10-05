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

    @Nullable
    public static Effect create(@Nullable EffectScript effect) {
        if (effect == null) {
            return null;
        }
        return Effect.create(effect.getTargetId(), effect.getValue(),
                Incrementer.Function.getFunctionFromKey(effect.getFunction()));
    }

    public static Effect create(String targetId, double value, @Nullable Incrementer.Function function) {
        return new Effect(targetId, value, function);
    }

    private Effect(String targetId, double value, @Nullable Incrementer.Function function) {
        this.targetId = targetId;
        this.value = value;
        this.function = function == null ? Incrementer.Function.ADD : function;
    }

    public String getTargetId() {
        return targetId;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "<" + Effect.class.getSimpleName() + " " + targetId + " " + value + ">";
    }

    public Incrementer.Function getFunction() {
        return function;
    }

}
