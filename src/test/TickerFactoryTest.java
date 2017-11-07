package test;

import junit.framework.TestCase;
import mcore.Ticker.*;
import org.json.JSONObject;
import org.junit.Test;
import utils.factory.Workshop;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class TickerFactoryTest {


    @Test
    public void createStepTicker() throws Exception {
        AbsTicker tick = TickerFactory.create(new JSONObject("{'Type': 'Step', 'Args': {'ts':[1,3,6], 't':0}}"));
        System.out.println("Ticker:");
        System.out.println(tick);

        double ti=0;
        tick.initialise(ti);
        while (ti < 10) {
            tick.update(ti);
            ti = tick.getNext();
            System.out.println("At: "+ti);
        }
        System.out.println(tick);

    }

    @Test
    public void createClockTicker() throws Exception {
        AbsTicker tick = TickerFactory.create(new JSONObject("{'Type': 'Clock', 'Args': {'dt':0.7}}"));
        System.out.println("Ticker:");
        System.out.println(tick);

        double ti=0;
        tick.initialise(ti);
        while (ti < 10) {
            tick.update(ti);
            ti = tick.getNext();
            System.out.println("At: "+ti);
        }
        System.out.println(tick);

    }

    @Test
    public void createAppointmentTicker() throws Exception {
        AbsTicker tick = TickerFactory.create(new JSONObject("{'Type': 'Appointment', 'Args': {'queue':[]}}"));
        ((AppointmentTicker) tick).makeAnAppointment(1);
        System.out.println("Ticker:");
        System.out.println(tick);

        double ti=0;
        tick.initialise(ti);
        while (ti < 10) {
            ((AppointmentTicker) tick).makeAnAppointment(ti+2.4);
            tick.update(ti);
            ti = tick.getNext();
            System.out.println("At: "+ti);
        }
        System.out.println(tick);

    }

}