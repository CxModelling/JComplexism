package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.element.Disclosure;

import static org.junit.Assert.*;

public class HaveStringCheckerTest {

    private IChecker Checker;

    @Before
    public void setUp() {
        Checker = new HaveStringChecker("remove agent");
    }

    @Test
    public void check() {
        assertTrue(Checker.check(new Disclosure("XXX remove agent YYY", "I", "Here")));
        assertFalse(Checker.check(new Disclosure("XXX add agent YYY", "He", "Here")));
    }

    @Test
    public void toJSON() {
        JSONObject js = Checker.toJSON();
        assertEquals("HaveString", js.getString("Type"));
        assertEquals("remove agent", js.getString("Have"));
    }
}