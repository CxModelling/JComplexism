package org.twz.dataframe.demographics;

import org.twz.dataframe.TimeSeries;
import org.twz.exception.TimeseriesException;

import java.util.HashMap;
import java.util.Map;

public class SexDemography extends AbsDemography {
    private final String IPopF, IPopM, IDeathF, IDeathM, IBirthF, IBirthM, IMigF, IMigM;

    public SexDemography(TimeSeries ts, String iPopF, String iPopM, String iDeathF, String iDeathM,
                         String iBirthF, String iBirthM, String iMigF, String iMigM) {
        super(ts);
        IPopF = iPopF;
        IPopM = iPopM;
        IDeathF = iDeathF;
        IDeathM = iDeathM;
        IBirthF = iBirthF;
        IBirthM = iBirthM;
        IMigF = iMigF;
        IMigM = iMigM;
    }

    @Override
    public double getPopulation(double time) throws TimeseriesException {
        return Ts.getDouble(time, IPopF) + Ts.getDouble(time, IPopM);
    }

    public double getPopulation(double time, String sex) throws TimeseriesException {
        return getBySex(time, sex, IPopF, IPopM);
    }

    @Override
    public double getDeathRate(double time) throws TimeseriesException {
        return Ts.getDouble(time, IDeathF) + Ts.getDouble(time, IDeathM);
    }

    public double getDeathRate(double time, String sex) throws TimeseriesException {
        return getBySex(time, sex, IDeathF, IDeathM);
    }

    @Override
    public double getBirthRate(double time) throws TimeseriesException {
        return Ts.getDouble(time, IBirthF) + Ts.getDouble(time, IBirthM);
    }

    public double getBirthRate(double time, String sex) throws TimeseriesException {
        return getBySex(time, sex, IBirthF, IBirthM);
    }

    @Override
    public double getMigration(double time) throws TimeseriesException {
        return Ts.getDouble(time, IMigF) + Ts.getDouble(time, IMigM);
    }

    public double getMigration(double time, String sex) throws TimeseriesException {
        return getBySex(time, sex, IMigF, IMigM);
    }

    @Override
    public ISampler getPopulationSampler(double time) throws TimeseriesException {
        double pf = getPopulation(time, "Female") / getPopulation(time);
        return () -> {
            Map<String, Object> x = new HashMap<>();
            x.put("Sex", (pf < Math.random())? "Female":"Male");
            return x;
        };
    }

    private double getBySex(double time, String sex, String iDeathF, String iDeathM) throws TimeseriesException {
        switch (sex) {
            case "Female":
                return Ts.getDouble(time, iDeathF);
            case "Male":
                return Ts.getDouble(time, iDeathM);
            default:
                throw new TimeseriesException("sex should be Female/Male");
        }
    }

    public static SexDemography readCSV(String file_path, String iYear,
                                        String iPopF, String iPopM, String iDeathF, String iDeathM,
                                        String iBirthF, String iBirthM, String iMigF, String iMigM) {
        return new SexDemography(TimeSeries.readCSV(file_path, iYear),
                iPopF, iPopM, iDeathF, iDeathM, iBirthF, iBirthM, iMigF, iMigM);
    }
}
