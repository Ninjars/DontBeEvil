package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.EffectScript;

/**
 * Created by Jez on 25/03/2016.
 */
public class Effect {
    private final String targetId;
    private final double value;

    public Effect(EffectScript effect) {
        targetId = effect.getTargetId();
        value = effect.getValue();
    }

    public String getTargetId() {
        return targetId;
    }

    public double getValue() {
        return value;
    }
}
