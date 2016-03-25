package com.projects.jez.dontbeevil.data;

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

    public LoopData(LoopDataScript data) {
        chargeTime = data.getChargeTime();
        effects = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
    }

    public long getChargeTime() {
        return chargeTime;
    }

    public List<Effect> getEffects() {
        return effects;
    }

}
