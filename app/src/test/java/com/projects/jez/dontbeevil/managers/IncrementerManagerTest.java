package com.projects.jez.dontbeevil.managers;

import com.projects.jez.dontbeevil.data.Incrementer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jez on 03/10/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class IncrementerManagerTest {

    private static final String id1 = "id1";
    private static final String id2 = "id2";

    @Mock
    Incrementer incrementer1;
    @Mock
    Incrementer incrementer2;

    @Before
    public void setUp() throws Exception {
        when(incrementer1.getId()).thenReturn(id1);
        when(incrementer2.getId()).thenReturn(id2);
    }

    @Test
    public void addIncrementer() throws Exception {
        IncrementerManager im = new IncrementerManager();
        assertEquals(null, im.getIncrementer(id1));

        im.addIncrementer(incrementer1);
        assertEquals(1, im.getAllIncrementers().size());
        assertEquals(incrementer1, im.getIncrementer(id1));
    }

    @Test
    public void getIncrementer() throws Exception {
        IncrementerManager im = new IncrementerManager();
        assertEquals(null, im.getIncrementer(id1));

        im.addIncrementer(incrementer1);
        assertEquals(incrementer1, im.getIncrementer(id1));
        assertEquals(null, im.getIncrementer(id2));
    }

    @Test
    public void getAllIncrementers() throws Exception {
        IncrementerManager im = new IncrementerManager();
        im.addIncrementer(incrementer1);
        assertEquals(1, im.getAllIncrementers().size());
        assertEquals(incrementer1, im.getIncrementer(id1));
        assertEquals(null, im.getIncrementer(id2));

        im.addIncrementer(incrementer2);
        assertEquals(2, im.getAllIncrementers().size());
        assertEquals(incrementer1, im.getIncrementer(id1));
        assertEquals(incrementer2, im.getIncrementer(id2));
    }

    @Test
    public void addAll() throws Exception {
        List<Incrementer> incrementers = new ArrayList<>();
        incrementers.add(incrementer1);
        incrementers.add(incrementer2);

        IncrementerManager im = new IncrementerManager();
        assertEquals(null, im.getIncrementer(id1));
        assertEquals(null, im.getIncrementer(id2));

        im.addAll(incrementers);
        assertEquals(incrementer1, im.getIncrementer(id1));
        assertEquals(incrementer2, im.getIncrementer(id2));
    }

    @Test
    public void removeIncrementer() throws Exception {
        IncrementerManager im = new IncrementerManager();
        assertEquals(0, im.getAllIncrementers().size());

        im.addIncrementer(incrementer1);
        assertEquals(1, im.getAllIncrementers().size());

        im.addIncrementer(incrementer2);
        assertEquals(2, im.getAllIncrementers().size());

        im.removeIncrementer(incrementer1);
        assertEquals(1, im.getAllIncrementers().size());

        im.removeIncrementer(incrementer2);
        assertEquals(0, im.getAllIncrementers().size());

        im.addIncrementer(incrementer1);
        assertEquals(1, im.getAllIncrementers().size());
    }
}