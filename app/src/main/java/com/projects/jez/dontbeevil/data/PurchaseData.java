package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.content.EffectScript;
import com.projects.jez.dontbeevil.content.PurchaseDataScript;
import com.projects.jez.dontbeevil.content.ToggleScript;
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
    private final List<Toggle> toggles;
    private final boolean isUnique;

    public static PurchaseData create(PurchaseDataScript data) {
        List<Effect> effects = MapperUtils.map(data.getEffects(), new Mapper<EffectScript, Effect>() {
            @Override
            public Effect map(EffectScript arg) {
                return Effect.create(arg);
            }
        });
        List<Toggle> toggles = MapperUtils.optionalMapOptionalList(data.getToggles(), new Mapper<ToggleScript, Toggle>() {
            @Override
            public Toggle map(ToggleScript arg) {
                return Toggle.create(arg);
            }
        });
        return create(Effect.create(data.getBaseCost()), data.isUnique(), effects, toggles, data.getLevelFactor());
    }

    public static PurchaseData create(@Nullable Effect baseCosts, boolean isUnique, List<Effect> effects, List<Toggle> toggles, double levelFactor) {
        return new PurchaseData(baseCosts, isUnique, effects, toggles, levelFactor);
    }

    private PurchaseData(@Nullable Effect baseCost, boolean isUnique, List<Effect> effects, List<Toggle> toggles, double levelFactor) {
        this.baseCost = baseCost;
        this.effects = effects;
        this.toggles = toggles;
        this.levelFactor = levelFactor;
        this.isUnique = isUnique;
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

    public List<Toggle> getToggles() {
        return toggles;
    }

    public boolean isUnique() {
        return isUnique;
    }
}
