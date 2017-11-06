package utils.dataframe;

import junit.framework.TestCase;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */
public class DataTableTest extends TestCase {
    public void testOutputCSV() throws Exception {
        DataTable dt = new DataTable();
        DataEntry de = new DataEntry();
        de.put("A", 1.0);
        de.put("B", 20.0);
        dt.put(1.0, de);
        de = new DataEntry();
        de.put("A", 4.5);
        de.put("B", 20.0);
        dt.put(1.3, de);
        dt.outputCSV("S.csv");
    }

    public void testOutputJSON() throws Exception {
        DataTable dt = new DataTable();
        DataEntry de = new DataEntry();
        de.put("A", 1.0);
        de.put("B", 20.0);
        dt.put(1.0, de);
        de = new DataEntry();
        de.put("A", 4.5);
        de.put("B", 20.0);
        dt.put(1.3, de);
        dt.outputJSON("S.json");
    }

}