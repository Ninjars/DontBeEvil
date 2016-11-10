package com.projects.jez.dontbeevil.data;

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
    private TestLoopTaskManager loopTaskManager;
    private PurchaseData purchaseData1;
    private Metadata incrementerMetadata1;
    private LoopData loopData1;
    private Metadata incrementerMetadata2;
    private PurchaseData purchaseData2;
    private LoopData loopDataDisabled;


    @Before
    public void setUp() throws Exception {
        loopTaskManager = new TestLoopTaskManager();
        incrementerManager = new IncrementerManager();
        Effect baseCost1 = Effect.create(id1, -1, Incrementer.Function.VALUE, false);
        List<Effect> baseEffect1 = Collections.singletonList(Effect.create(id1, 5, Incrementer.Function.VALUE, false));
        purchaseData1 = PurchaseData.create(baseCost1, false, baseEffect1, Collections.<Toggle>emptyList(), levelFactor);
        incrementerMetadata1 = Metadata.create(name1, caption1, 0);
        List<Effect> loopEffect = Collections.singletonList(Effect.create(id1, 5, Incrementer.Function.VALUE, false));
        loopData1 = LoopData.create(100, loopEffect);

        Effect baseCost2 = Effect.create(id1, -1, Incrementer.Function.VALUE, false);
        List<Effect> baseEffect2 = Collections.singletonList(Effect.create(id2, 1, Incrementer.Function.VALUE, false));
        purchaseData2 = PurchaseData.create(baseCost2, false, baseEffect2, Collections.<Toggle>emptyList(), levelFactor);
        incrementerMetadata2 = Metadata.create(name2, caption2, 0);

        // disabled cost incremementer
        List<Effect> loopEffectDisabled = Collections.singletonList(Effect.create(id1, -1, Incrementer.Function.VALUE, true));
        loopDataDisabled = LoopData.create(100, loopEffectDisabled);
    }

    @After
    public void tearDown() throws Exception {
        loopTaskManager.pauseAll();
    }

    private Incrementer createNonLoopingIncrementer1() {
        return Incrementer.create(id1, incrementerMetadata1, purchaseData1, null, incrementerManager, null, false);
    }

    private Incrementer createNonLoopingIncrementer2() {
        return Incrementer.create(id2, incrementerMetadata2, purchaseData2, null, incrementerManager, null, false);
    }

    private Incrementer createLoopingDisabledIncrementer2() {
        return Incrementer.create(id2, incrementerMetadata2, purchaseData2, loopDataDisabled, incrementerManager, loopTaskManager, false);
    }

    private Incrementer createLoopingIncrementer1() {
        return Incrementer.create(id1, incrementerMetadata1, purchaseData1, loopData1, incrementerManager, loopTaskManager, false);
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
    public void getCaption() throws Exception {
        Incrementer incrementer = createNonLoopingIncrementer1();
        assertEquals(caption1, incrementer.getCaption());
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
        loopTaskManager.clear();
    }

    @Test
    public void getRange_loop_started() throws Exception {
        Incrementer incrementer = createLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer);
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
        incrementerManager.removeIncrementer(incrementer);
        loopTaskManager.clear();
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

    @Test
    public void enabledIncrementer() throws Exception {
        Incrementer incrementer1 = createLoopingIncrementer1();
        incrementerManager.addIncrementer(incrementer1);
        assertEquals(0.0, incrementer1.getValue());

        incrementer1.modifyValue(1);
        assertEquals(1.0, incrementer1.getValue());

        boolean ran = loopTaskManager.runLastTask();
        assertEquals(true, ran);
        assertEquals(6.0, incrementer1.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        loopTaskManager.clear();
    }

    @Test
    public void disabledIncrementer() throws Exception {
        Incrementer incrementer1 = createNonLoopingIncrementer1();
        Incrementer disabled = createLoopingDisabledIncrementer2();
        incrementerManager.addIncrementer(incrementer1);
        incrementerManager.addIncrementer(disabled);
        assertEquals(0.0, incrementer1.getValue());
        assertEquals(0.0, disabled.getValue());

        incrementer1.modifyValue(2);
        assertEquals(2.0, incrementer1.getValue());
        disabled.modifyValue(1);
        assertEquals(1.0, disabled.getValue());
        assertEquals(2.0, incrementer1.getValue());

        boolean ran = loopTaskManager.runLastTask();
        assertEquals(true, ran);
        assertEquals(2.0, incrementer1.getValue());

        incrementerManager.removeIncrementer(incrementer1);
        incrementerManager.removeIncrementer(disabled);
        loopTaskManager.clear();
    }
}