package pcore;

/**
 *
 * Created by TimeWz on 2017/4/20.
 */
public class ScriptException extends Exception {
    private String Info;

    public ScriptException(String v) {
        Info = v;
    }

    public String toString() {
        return Info + " contains illegal syntax";
    }
}
