package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.managers.IncrementerManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;

/**
 * Created by Jez on 01/10/2016.
 */
public class IncrementerTest {
    private static final String id = "test1";
    private static final String name = "test1 name";
    private static final String caption = "test1 caption";
    private static final double levelFactor = 1.5;
    private IncrementerManager incrementerManager;
    private LoopTaskManager loopTaskManager;
    private PurchaseData purchaseData;
    private IncrementerMetadata incrementerMetadata;
    private LoopData loopData;

    @Before
    public void setUp() throws Exception {
        incrementerManager = new IncrementerManager();
        loopTaskManager = new LoopTaskManager();
        List<Effect> baseCost = Collections.singletonList(Effect.create(id, 1, Incrementer.Function.SUB));
        List<Effect> baseEffect = Collections.singletonList(Effect.create(id, 5, Incrementer.Function.ADD));
        purchaseData = PurchaseData.create(baseCost, baseEffect, levelFactor);
        incrementerMetadata = IncrementerMetadata.create(name, caption, 0);
        List<Effect> loopEffect = Collections.singletonList(Effect.create(id, 5, Incrementer.Function.ADD));
        loopData = LoopData.create(100, loopEffect);
    }

    @After
    public void tearDown() throws Exception {
        loopTaskManager.pauseAll();
    }

    private Incrementer createNonLoopingIncrementer() {
        return Incrementer.create(id, incrementerMetadata, purchaseData, null, incrementerManager, null);
    }

    private Incrementer createLoopingIncrementer() {
        return Incrementer.create(id, incrementerMetadata, purchaseData, loopData, incrementerManager, loopTaskManager);
    }

    @Test
    public void createIncrementer() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        double startValue = incrementer.getValue();
        assertEquals(0.0, startValue);
    }

    @Test
    public void getId() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        assertEquals(id, incrementer.getId());
    }

    @Test
    public void applyChange_add_positive() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.ADD, 10);
        double endValue = incrementer.getValue();
        assertEquals(10.0, endValue);
    }

    @Test
    public void applyChange_add_negative() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.ADD, -10);
        double endValue = incrementer.getValue();
        assertEquals(-10.0, endValue);
    }

    @Test
    public void applyChange_subtract_positive() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.SUB, 10);
        double endValue = incrementer.getValue();
        assertEquals(-10.0, endValue);
    }

    @Test
    public void applyChange_subtract_negative() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.SUB, -10);
        double endValue = incrementer.getValue();
        assertEquals(10.0, endValue);
    }

    @Test
    public void getRange_no_loop() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        assertNull(incrementer.getRange());
    }

    @Test
    public void getRange_loop_not_started() throws Exception {
        Incrementer incrementer = createLoopingIncrementer();
        assertSame(Range.empty(), incrementer.getRange());
    }

    @Test
    public void getRange_loop_started() throws Exception {
        Incrementer incrementer = createLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.ADD, 1.0);
        assertNotNull(incrementer.getRange());
        assertNotSame(Range.empty(), incrementer.getRange());
        incrementer.applyChange(Incrementer.Function.SUB, 1.0);
        assertNotNull(incrementer.getRange());
        assertSame(Range.empty(), incrementer.getRange());
    }

    @Test
    public void preformPurchaseActions_valid() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.ADD, 10.0);
        incrementerManager.addIncrementer(incrementer);
        incrementer.preformPurchaseActions();
        assertEquals(5.0, incrementer.getValue());
        incrementerManager.removeIncrementer(incrementer);
    }

    @Test
    public void preformPurchaseActions_invalid_1() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        double factor = Math.max(1.0, Math.abs(Math.pow(incrementer.getValue(), purchaseData.getLevelFactor())));
        incrementerManager.addIncrementer(incrementer);
        incrementer.preformPurchaseActions();
        assertEquals("This purchase would result in negative value, so should be prevented", 0.0, incrementer.getValue());
        incrementerManager.removeIncrementer(incrementer);
    }

    @Test
    public void preformPurchaseActions_invalid_2() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer();
        incrementer.applyChange(Incrementer.Function.ADD, 7.0);
        incrementerManager.addIncrementer(incrementer);
        incrementer.preformPurchaseActions();
        assertEquals("Even if effect of purchase would result in positive value, initial cost should block purchase", 10.0, incrementer.getValue());
        incrementerManager.removeIncrementer(incrementer);
    }

}