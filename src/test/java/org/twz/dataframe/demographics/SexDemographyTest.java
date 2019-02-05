package org.twz.dataframe.demographics;

import org.junit.Before;
import org.junit.Test;
import org.twz.dataframe.TimeSeries;
import org.twz.exception.TimeseriesException;

import static org.junit.Assert.*;

public class SexDemographyTest {

    SexDemography Demo;

    @Before
    public void setUp() throws Exception {
        Demo = SexDemography.readCSV("src/test/resources/SimFM.csv", "Year",
                "PopF", "PopM", "DeathF", "DeathM",
                "BirthF", "BirthM", "MigrationF", "MigrationM");
    }

    @Test
    public void getPopulation() throws TimeseriesException {
        double  pf = Demo.getPopulation(2000, "Female"),
                pm = Demo.getPopulation(2000, "Male"),
                p = Demo.getPopulation(2000);
        assertEquals(pf+pm, p);
    }

    @Test
    public void getDeathRate() throws TimeseriesException {
        double  pf = Demo.getDeathRate(2000, "Female"),
                pm = Demo.getDeathRate(2000, "Male"),
                p = Demo.getDeathRate(2000);
        assertEquals(pf+pm, p);
    }

    @Test
    public void getBirth() throws TimeseriesException {
        double  pf = Demo.getBirthRate(2000, "Female"),
                pm = Demo.getBirthRate(2000, "Male"),
                p = Demo.getBirthRate(2000);
        assertEquals(pf+pm, p);
    }

    @Test
    public void getMigration() throws TimeseriesException {
        double  pf = Demo.getMigration(2000, "Female"),
                pm = Demo.getMigration(2000, "Male"),
                p = Demo.getMigration(2000);
        assertEquals(pf+pm, p);
    }
}