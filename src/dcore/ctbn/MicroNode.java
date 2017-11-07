package dcore.ctbn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public MicroState get(String s) {
        for (MicroState ms: MicroStates) {
            if (ms.toString().equals(s)) {
                return ms;
            }
        }
        return MicroState.NullState;
    }

    public List<MicroState> getMicroStates() {
        return MicroStates;
    }

    public Set<MicroState> getSpace() {
        Set<MicroState> sp = new HashSet<>(MicroStates);
        sp.add(MicroState.NullState);
        return sp;
    }

    public String toString() {
        return this.Desc;
    }
}
