package org.twz.factory;

import org.twz.factory.Workshop;
import org.twz.factory.arguments.AbsArgument;
import org.twz.factory.arguments.DoubleArg;
import org.twz.factory.arguments.IntegerArg;
import org.twz.factory.arguments.StringArg;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.twz.factory.PseudoType;

/**
 * Created by TimeWz on 2017/11/3.
 */
public class CreatorTest extends TestCase {

    public void testToString() {
        Workshop ws = new Workshop();
        AbsArgument[] args = new AbsArgument[]{
                new StringArg("S"),
                new IntegerArg("I"),
                new DoubleArg("D")
        };
        ws.register("P", PseudoType.class, args);
        Object o = ws.create(new JSONObject("{'Name': 'A', 'Type': 'P', 'Args': {'S': 'str', 'I': 1, 'D': 0.1}}"));
        System.out.println(o);
    }

}