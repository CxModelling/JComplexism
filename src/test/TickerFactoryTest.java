package test;

import junit.framework.TestCase;
import mcore.Ticker.AbsTicker;
import mcore.Ticker.AppointmentTicker;
import mcore.Ticker.TickerFactory;
import org.json.JSONObject;
import org.junit.Test;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class TickerFactoryTest {
    private TickerFactory Fact = new TickerFactory();

    @Test
    public void createStepTicker() throws Exception {
        AbsTicker tick = Fact.creatTicker(new JSONObject("{'Type': 'StepTicker', 'Args': {'ts':[1,3,6]}}"));
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
        AbsTicker tick = Fact.creatTicker(new JSONObject("{'Type': 'ClockTicker', 'Args': {'dt':0.7}}"));
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
        AbsTicker tick = Fact.creatTicker(new JSONObject("{'Type': 'AppointmentTicker', 'Args': {'queue':[]}}"));
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