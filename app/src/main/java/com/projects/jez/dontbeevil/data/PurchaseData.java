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
    private final List<Effect> effects;

    public static PurchaseData create(PurchaseDataScript data) {
        List<Effect> costs = MapperUtils.map(data.getBaseCost(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
        List<Effect> effects = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return new Effect(arg);
            }
        });
        return create(costs, effects, data.getLevelFactor());
    }

    public static PurchaseData create (List<Effect> baseCosts, List<Effect> effects, double levelFactor) {
        return new PurchaseData(baseCosts, effects, levelFactor);
    }

    private PurchaseData(List<Effect> baseCosts, List<Effect> effects, double levelFactor) {
        this.baseCosts = baseCosts;
        this.effects = effects;
        this.levelFactor = levelFactor;
    }

    public List<Effect> getBaseCosts() {
        return baseCosts;
    }

    public double getLevelFactor() {
        return levelFactor;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
