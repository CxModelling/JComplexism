package mcore.Ticker;

import hgm.utils.FnJSON;
import hgm.utils.NotRecoverableFromJSONException;
import org.json.JSONObject;


/**
 * Factory object providing interface to create tickers
 * Created by TimeWz on 2017/10/13.
 */
public class TickerFactory {

    public AbsTicker creatTicker(JSONObject js) throws NotRecoverableFromJSONException {
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
            return new StepTicker(FnJSON.toDoubleList(js.getJSONArray("ts")));
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
            return ticker;
        } catch (NullPointerException e) {
            throw new NotRecoverableFromJSONException("Ill-defined ticker");
        }
    }

    private ClockTicker createClockTicker(JSONObject js) throws NotRecoverableFromJSONException {
        try {
            return new ClockTicker(js.getDouble("dt"));
        } catch (NullPointerException e) {
            throw new NotRecoverableFromJSONException("Ill-defined ticker");
        }
    }


}
