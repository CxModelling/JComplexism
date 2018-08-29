package org.twz.cx.element;

import org.junit.Before;
import org.junit.Test;
import org.twz.dataframe.Pair;

import static org.junit.Assert.*;

public class DisclosureTest {
    private Disclosure D;

    @Before
    public void setUp() {
        D = new Disclosure("Have fun", "I", "Here");
    }

    @Test
    public void upScale() {
        assertEquals(D.getAddress(), "Here");
        Disclosure Up = D.upScale("Taipei");
        assertEquals(Up.getAddress(), "Here@Taipei");
    }

    @Test
    public void siblingScale() {
        assertEquals(D.getAddress(), "Here");
        Disclosure Up = D.upScale("Taipei");
        Disclosure Sib = Up.siblingScale();
        assertEquals(Sib.getAddress(), "Here@Taipei@^");
        assertFalse(Up.isSibling());
        assertTrue(Sib.isSibling());
    }

    @Test
    public void downScale() {
        Disclosure Up = D.upScale("Taipei");
        Pair<String, Disclosure> Down = Up.downScale();

        assertEquals(Down.getFirst(), "Taipei");
        assertEquals(Down.getSecond().getAddress(), "Here");
    }
}