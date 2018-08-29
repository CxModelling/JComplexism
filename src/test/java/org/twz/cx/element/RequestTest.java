package org.twz.cx.element;

import org.junit.Before;
import org.junit.Test;
import org.twz.dataframe.Pair;

import static org.junit.Assert.*;

public class RequestTest {
    private Request R;

    @Before
    public void setUp() {
        R = new Request(new Event("Have fun", 10), "I", "Here");
    }

    @Test
    public void upScale() {
        assertEquals(R.getAddress(), "Here");
        Request Up = R.upScale("Taipei");
        assertEquals(Up.getAddress(), "Here@Taipei");
    }

    @Test
    public void downScale() {
        Request Up = R.upScale("Taipei");
        Pair<String, Request> Down = Up.downScale();

        assertEquals(Down.getFirst(), "Taipei");
        assertEquals(Down.getSecond().getAddress(), "Here");
    }
}