package org.twz.cx.mcore.communicator;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.element.Disclosure;

import static org.junit.Assert.*;

public class StartWithCheckerTest {

    private AbsChecker Checker;

    @Before
    public void setUp() {
        Checker = new StartWithChecker("update");
    }

    @Test
    public void check() {
        assertTrue(Checker.check(new Disclosure("update information", "I", "Here")));
        assertFalse(Checker.check(new Disclosure("initialise information", "He", "Here")));
    }

    @Test
    public void toJSON() {
        JSONObject js = Checker.toJSON();
        assertEquals("StartWith", js.getString("Type"));
        assertEquals("update", js.getString("Start"));
    }
}