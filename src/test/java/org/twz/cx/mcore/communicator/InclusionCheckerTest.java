package org.twz.cx.mcore.communicator;

import org.junit.Before;
import org.junit.Test;
import org.twz.cx.element.Disclosure;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InclusionCheckerTest {
    InclusionChecker Checker;
    Disclosure Truthy, Falsy;

    @Before
    public void setUp() {
        List<String> arr = new ArrayList<>();
        arr.add("cook");
        arr.add("hunt");
        arr.add("write");
        Checker = new InclusionChecker(arr);

        Truthy = new Disclosure("cook", "I", "home");
        Falsy = new Disclosure("shop", "You", "store");
    }


    @Test
    public void check() {
        assertTrue(Checker.check(Truthy));
        assertFalse(Checker.check(Falsy));
    }

    @Test
    public void deepCopy() {
        assertTrue(Checker.deepcopy().check(Truthy));
        assertFalse(Checker.deepcopy().check(Falsy));
    }
}