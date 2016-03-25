package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.projects.jez.dontbeevil.BuildConfig;
import com.projects.jez.dontbeevil.content.CostScript;
import com.projects.jez.utils.observable.Mapper;

/**
 * Created by Jez on 25/03/2016.
 */
public class Cost {

    enum Currency {
        PLAYS ("plays");

        String value;

        Currency(String name) {
            value = name;
        }

        @Nullable
        public static Currency getCurrencyForName(String name) {
            for (Currency c : values()) {
                if (c.value.equals(name)) {
                    return c;
                }
            }
            if (BuildConfig.DEBUG) {
                throw new RuntimeException("No match found for " + name);
            } else {
                Log.w(Currency.class.getSimpleName(), "No match found for " + name);
            }
            return null;
        }
    }

    public static Mapper<CostScript, Cost> costScriptMapper = new Mapper<CostScript, Cost>(){
        @Override
        public Cost map(CostScript arg) {
            return new Cost(arg);
        }
    };

    private final Currency currency;
    private final double amount;

    public Cost(CostScript costScript) {
        amount = costScript.getValue();
        currency = Currency.getCurrencyForName(costScript.getCurrency());
    }

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
