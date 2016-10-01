package com.projects.jez.dontbeevil.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.content.EffectScript;
import com.projects.jez.dontbeevil.content.LoopDataScript;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.observable.Mapper;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class LoopData {
    private final List<Effect> effects;
    private final long chargeTime;

    public static LoopData create(@Nullable LoopDataScript data) {
        if (data == null) {
            return null;
        }
        List<Effect> effects = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return Effect.create(arg);
            }
        });
        return LoopData.create(data.getChargeTime(), effects);
    }

    public static LoopData create(long chargeTime, @NonNull List<Effect> effects) {
        return new LoopData(chargeTime, effects);
    }

    private LoopData(long chargeTime, List<Effect> effects) {
        this.chargeTime = chargeTime;
        this.effects = effects;
    }

    public long getChargeTime() {
        return chargeTime;
    }

    public List<Effect> getEffects() {
        return effects;
    }

}
