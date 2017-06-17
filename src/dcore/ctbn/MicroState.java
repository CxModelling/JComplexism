package dcore.ctbn;

/**
 *
 * Created by TimeWz on 2017/2/8.
 */
public class MicroState {
    public static MicroState NullState = new MicroState("_");

    private String Desc;

    public MicroState(String desc) {
        Desc = desc;
    }

    public String toString() {
        return Desc;
    }
}
