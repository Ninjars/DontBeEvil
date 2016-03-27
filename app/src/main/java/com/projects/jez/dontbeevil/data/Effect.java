package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.EffectScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class Effect {
    private final String targetId;
    private final double value;
    private final Incrementer.Function function;

    public Effect(EffectScript effect) {
        targetId = effect.getTargetId();
        value = effect.getValue();
        function = effect.getFunction() == null ? Incrementer.Function.ADD : Incrementer.Function.getFunctionFromKey(effect.getFunction());
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
