package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;

import com.projects.jez.dontbeevil.content.ToggleScript;

/**
 * Created by Jez on 12/10/2016.
 */

public class Toggle {
    private final String targetId;
    private final String EffectId;
    private final boolean enable;

    public static Toggle create(ToggleScript script) {
        return Toggle.create(script.getTargetId(), script.getEffectId(), script.enable());
    }

    public static Toggle create(@NonNull String targetId, @NonNull String effectId, boolean enable) {
        return new Toggle(targetId, effectId, enable);
    }

    private Toggle(@NonNull String targetId, @NonNull String effectId, boolean enable) {
        this.targetId = targetId;
        EffectId = effectId;
        this.enable = enable;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getEffectId() {
        return EffectId;
    }

    public boolean isEnable() {
        return enable;
    }
}
