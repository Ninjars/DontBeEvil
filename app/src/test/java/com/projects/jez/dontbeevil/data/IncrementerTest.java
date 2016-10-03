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
    private static final String id1 = "test1";
    private static final String name1 = "test1 name";
    private static final String caption1 = "test1 caption";
    private static final String id2 = "test2";
    private static final String name2 = "test2 name";
    private static final String caption2 = "test2 caption";
    private static final double levelFactor = 2;
    private IncrementerManager incrementerManager;
    private LoopTaskManager loopTaskManager;
    private PurchaseData purchaseData1;
    private IncrementerMetadata incrementerMetadata1;
    private LoopData loopData1;
    private IncrementerMetadata incrementerMetadata2;
    private PurchaseData purchaseData2;

    @Before
    public void setUp() throws Exception {
        incrementerManager = new IncrementerManager();
        loopTaskManager = new LoopTaskManager();
        List<Effect> baseCost1 = Collections.singletonList(Effect.create(id1, 1, Incrementer.Function.SUB));
        List<Effect> baseEffect1 = Collections.singletonList(Effect.create(id1, 5, Incrementer.Function.ADD));
        purchaseData1 = PurchaseData.create(baseCost1, baseEffect1, levelFactor);
        incrementerMetadata1 = IncrementerMetadata.create(name1, caption1, 0);
        List<Effect> loopEffect = Collections.singletonList(Effect.create(id1, 5, Incrementer.Function.ADD));
        loopData1 = LoopData.create(100, loopEffect);

        List<Effect> baseCost2 = Collections.singletonList(Effect.create(id1, 1, Incrementer.Function.SUB));
        List<Effect> baseEffect2 = Collections.singletonList(Effect.create(id2, 1, Incrementer.Function.ADD));
        purchaseData2 = PurchaseData.create(baseCost2, baseEffect2, levelFactor);
        incrementerMetadata2 = IncrementerMetadata.create(name2, caption2, 0);
    }

    @After
    public void tearDown() throws Exception {
        loopTaskManager.pauseAll();
    }

    private Incrementer createNonLoopingIncrementer1() {
        return Incrementer.create(id1, incrementerMetadata1, purchaseData1, null, incrementerManager, null);
    }

    private Incrementer createNonLoopingIncrementer2() {
        return Incrementer.create(id2, incrementerMetadata2, purchaseData2, null, incrementerManager, null);
    }

    private Incrementer createLoopingIncrementer1() {
        return Incrementer.create(id1, incrementerMetadata1, purchaseData1, loopData1, incrementerManager, loopTaskManager);
    }

    @Test
    public void createIncrementer() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        double startValue = incrementer.getValue();
        assertEquals(0.0, startValue);
    }

    @Test
    public void getId() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        assertEquals(id1, incrementer.getId());
    }

    @Test
    public void applyChange_add() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        boolean applied = incrementer.modifyValue(10);
        double endValue = incrementer.getValue();
        assertEquals(true, applied);
        assertEquals(10.0, endValue);
    }

    @Test
    public void applyChange_sub() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        boolean appliedAddition = incrementer.modifyValue(10);
        double midValue = incrementer.getValue();
        boolean appliedSubtraction = incrementer.modifyValue(-10);
        double endValue = incrementer.getValue();
        assertEquals(true, appliedAddition);
        assertEquals(10.0, midValue);
        assertEquals(true, appliedSubtraction);
        assertEquals(0.0, endValue);
    }

    @Test
    public void applyChange_sub_negative() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        boolean appliedAddition = incrementer.modifyValue(5);
        double midValue = incrementer.getValue();
        boolean appliedSubtraction = incrementer.modifyValue(-10);
        double endValue = incrementer.getValue();
        assertEquals(true, appliedAddition);
        assertEquals(5.0, midValue);
        assertEquals(false, appliedSubtraction);
        assertEquals(5.0, endValue);
    }

    @Test
    public void getRange_no_loop() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        assertNull(incrementer.getRange());
    }

    @Test
    public void getRange_loop_not_started() throws Exception {
        Incrementer incrementer = createLoopingIncrementer1();
        assertSame(Range.empty(), incrementer.getRange());
    }

    @Test
    public void getRange_loop_started() throws Exception {
        Incrementer incrementer = createLoopingIncrementer1();
        boolean appliedAddition = incrementer.modifyValue(1.0);
        assertEquals(true, appliedAddition);
        assertEquals(1.0, incrementer.getValue());
        assertNotNull(incrementer.getRange());
        assertNotSame(Range.empty(), incrementer.getRange());

        boolean appliedSubtraction = incrementer.modifyValue(-1.0);
        assertEquals(true, appliedSubtraction);
        assertEquals(0.0, incrementer.getValue());
        assertNotNull(incrementer.getRange());
        assertSame(Range.empty(), incrementer.getRange());
    }

    @Test
    public void preformPurchaseActions_valid_1() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        boolean appliedAddition = incrementer1.modifyValue(2.0);
        assertEquals(true, appliedAddition);
        assertEquals(2.0, incrementer1.getValue());

        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);
        assertEquals(0.0, incrementer2.getValue());
        assertEquals(1.0, incrementer2.getPurchaseFactor());

        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(true, purchase);
        assertEquals(1.0, incrementer1.getValue());
        assertEquals(1.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

    @Test
    public void preformPurchaseActions_valid_2() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        boolean appliedAddition = incrementer1.modifyValue(4.0);
        assertEquals(true, appliedAddition);
        assertEquals(4.0, incrementer1.getValue());

        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);
        appliedAddition = incrementer2.modifyValue(1.0);
        assertEquals(true, appliedAddition);
        assertEquals(1.0, incrementer2.getValue());
        assertEquals(4.0, incrementer2.getPurchaseFactor());

        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(true, purchase);
        assertEquals(0.0, incrementer1.getValue());
        assertEquals(2.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

    @Test
    public void preformPurchaseActions_valid_3() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        boolean appliedAddition = incrementer1.modifyValue(10.0);
        assertEquals(true, appliedAddition);
        assertEquals(10.0, incrementer1.getValue());

        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);
        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(true, purchase);
        assertEquals(9.0, incrementer1.getValue());
        assertEquals(1.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

    @Test
    public void preformPurchaseActions_valid_4() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        boolean appliedAddition = incrementer1.modifyValue(10.0);
        assertEquals(true, appliedAddition);
        assertEquals(10.0, incrementer1.getValue());

        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);
        appliedAddition = incrementer2.modifyValue(2.0);
        assertEquals(true, appliedAddition);
        assertEquals(2.0, incrementer2.getValue());

        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(true, purchase);
        assertEquals(1.0, incrementer1.getValue());
        assertEquals(3.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

    @Test
    public void preformPurchaseActions_invalid_1() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);

        assertEquals(0.0, incrementer1.getValue());
        assertEquals(0.0, incrementer2.getValue());

        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(false, purchase);
        assertEquals(0.0, incrementer1.getValue());
        assertEquals(0.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

    @Test
    public void preformPurchaseActions_invalid_2() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        Incrementer incrementer2 = createNonLoopingIncrementer2();
        incrementerManager.addIncrementer(incrementer2);

        incrementer1.modifyValue(2);
        incrementer2.modifyValue(2);
        assertEquals(2.0, incrementer1.getValue());
        assertEquals(2.0, incrementer2.getValue());

        assertEquals(9.0, incrementer2.getPurchaseFactor());
        boolean purchase = incrementer2.preformPurchaseActions();
        assertEquals(false, purchase);
        assertEquals(2.0, incrementer1.getValue());
        assertEquals(2.0, incrementer2.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(incrementer2);
    }

}