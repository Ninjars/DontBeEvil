package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.EffectScript;
import com.projects.jez.dontbeevil.content.PurchaseDataScript;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.observable.Mapper;

import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class PurchaseData {
    private final List<Effect> baseCosts;
    private final List<Effect> perLevelCostFactors;
    private final List<Effect> effect;

    public PurchaseData(PurchaseDataScript data) {
        baseCosts = MapperUtils.map(data.getBaseCost(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
        perLevelCostFactors = MapperUtils.map(data.getPerLevelCostFactor(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
        effect = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
    }

    public List<Effect> getBaseCosts() {
        return baseCosts;
    }

    public List<Effect> getPerLevelCostFactors() {
        return perLevelCostFactors;
    }

    public List<Effect> getEffect() {
        return effect;
    }
}
