package dcore.ctbn;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/8.
 */
public class MicroNode {
    private String Desc;
    private List<MicroState> MicroStates;

    public MicroNode(String desc, List<String> arr) {
        Desc = desc;
        MicroStates = arr.stream().map(MicroState::new).collect(Collectors.toList());
    }

    public int index(MicroState ms) {
        return MicroStates.indexOf(ms);
    }

    public MicroState get(int i) {
        try {
            return MicroStates.get(i);
        } catch (NullPointerException e) {
            return MicroState.NullState;
        }
    }

    public List<MicroState> getMicroStates() {
        return MicroStates;
    }

    public String toString() {
        return this.Desc;
    }
}
