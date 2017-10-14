package mcore.Ticker;

import hgm.utils.FnJSON;
import hgm.utils.NotRecoverableFromJSONException;
import org.json.JSONObject;


/**
 * Factory object providing interface to create tickers
 * Created by TimeWz on 2017/10/13.
 */
public class TickerFactory {

    public AbsTicker createTicker(JSONObject js) throws NotRecoverableFromJSONException {
        String type = js.getString("Type");

        switch (type) {
            case "StepTicker":
                return createStepTicker(js.getJSONObject("Args"));
            case "AppointmentTicker":
                return createAppointmentTicker(js.getJSONObject("Args"));
            case "ClockTicker":
                return createClockTicker(js.getJSONObject("Args"));
        }
        throw new NotRecoverableFromJSONException("Type of ticker does not exist");
    }

    private StepTicker createStepTicker(JSONObject js) throws NotRecoverableFromJSONException {
        try {
            StepTicker ticker = new StepTicker(FnJSON.toDoubleList(js.getJSONArray("ts")));
            if (js.has("t")) ticker.initialise(js.getDouble("t"));
            return ticker;
        } catch (NullPointerException e) {
            throw new NotRecoverableFromJSONException("Ill-defined ticker");
        }
    }

    private AppointmentTicker createAppointmentTicker(JSONObject js) throws NotRecoverableFromJSONException {
        try {
            AppointmentTicker ticker = new AppointmentTicker();
            for (double t: FnJSON.toDoubleList(js.getJSONArray("queue"))) {
                ticker.makeAnAppointment(t);
            }
            if (js.has("t")) ticker.initialise(js.getDouble("t"));
            return ticker;
        } catch (NullPointerException e) {
            throw new NotRecoverableFromJSONException("Ill-defined ticker");
        }
    }

    private ClockTicker createClockTicker(JSONObject js) throws NotRecoverableFromJSONException {
        try {
            ClockTicker ticker = new ClockTicker(js.getDouble("dt"));
            if (js.has("t")) ticker.initialise(js.getDouble("t"));
            return ticker
        } catch (NullPointerException e) {
            throw new NotRecoverableFromJSONException("Ill-defined ticker");
        }
    }


}
