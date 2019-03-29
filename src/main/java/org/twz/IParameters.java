package org.twz;

import org.twz.dataframe.IEntries;

public interface IParameters extends IEntries {
    double getDouble(String s);
    boolean has(String s);
}
