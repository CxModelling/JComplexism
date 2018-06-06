package org.twz.cx.mcore.Ticker;

import org.twz.factory.Workshop;
import org.twz.factory.arguments.AbsArgument;
import org.twz.factory.arguments.DoubleArg;
import org.twz.factory.arguments.ListArg;
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
