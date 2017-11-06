package test;

import utils.factory.Creator;
import utils.factory.Workshop;
import utils.factory.arguments.AbsArgument;
import utils.factory.arguments.DoubleArg;
import utils.factory.arguments.IntegerArg;
import utils.factory.arguments.StringArg;
import junit.framework.TestCase;
import org.json.JSONObject;

/**
 * Created by TimeWz on 2017/11/3.
 */
public class CreatorTest extends TestCase {

    public void testToString() throws Exception {
        Workshop ws = Workshop.getWorkshop("Test");
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