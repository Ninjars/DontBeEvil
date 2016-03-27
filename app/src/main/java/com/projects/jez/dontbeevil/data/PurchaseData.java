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
    private final double levelFactor;
    private final List<Effect> baseCosts;
    private final List<Effect> effect;

    public PurchaseData(PurchaseDataScript data) {
        baseCosts = MapperUtils.map(data.getBaseCost(), new Mapper<EffectScript, Effect>() {
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
        levelFactor = data.getLevelFactor();
    }

    public List<Effect> getBaseCosts() {
        return baseCosts;
    }

    public double getLevelFactor() {
        return levelFactor;
    }

    public List<Effect> getEffect() {
        return effect;
    }
}
