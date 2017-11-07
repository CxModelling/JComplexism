package mcore.Ticker;

import utils.factory.Workshop;
import utils.factory.arguments.AbsArgument;
import utils.factory.arguments.DoubleArg;
import utils.factory.arguments.ListArg;
import utils.json.FnJSON;
import utils.json.NotRecoverableFromJSONException;
import org.json.JSONObject;


/**
 * Factory object providing interface to create tickers
 * Created by TimeWz on 2017/10/13.
 */
public class TickerFactory {
    private static Workshop<AbsTicker> Factory = new Workshop<>();

    static {

        AbsArgument[] ags;

        ags = new AbsArgument[]{new ListArg("ts"), new DoubleArg("t")};
        Factory.register("Step", StepTicker.class, ags);

        ags = new AbsArgument[]{new ListArg("queue"), new DoubleArg("t")};
        Factory.register("Appointment", AppointmentTicker.class, ags);

        ags = new AbsArgument[]{new DoubleArg("dt"), new DoubleArg("t")};
        Factory.register("Clock", ClockTicker.class, ags);
    }

    public static AbsTicker create(JSONObject js) {
        return Factory.create(js);
    }

}
