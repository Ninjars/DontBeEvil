package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;

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
    private final @Nullable Effect baseCost;
    private final List<Effect> effects;

    public static PurchaseData create(PurchaseDataScript data) {
        List<Effect> effects = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return Effect.create(arg);
            }
        });
        return create(Effect.create(data.getBaseCost()), effects, data.getLevelFactor());
    }

    public static PurchaseData create (@Nullable Effect baseCosts, List<Effect> effects, double levelFactor) {
        return new PurchaseData(baseCosts, effects, levelFactor);
    }

    private PurchaseData(@Nullable Effect baseCost, List<Effect> effects, double levelFactor) {
        this.baseCost = baseCost;
        this.effects = effects;
        this.levelFactor = levelFactor;
    }

    @Nullable
    public Effect getBaseCost() {
        return baseCost;
    }

    public double getLevelFactor() {
        return levelFactor;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
