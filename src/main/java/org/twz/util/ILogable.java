package org.twz.util;

import java.util.logging.Logger;

public interface ILogable {
    void onLog(Logger logger);
    void onLog();
    void offLog();
    void info(String msg);
    void warning(String msg);
    void error(String msg);
}
