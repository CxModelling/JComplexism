package dcore.ctbn;

import dcore.IBlueprintDCore;
import dcore.State;
import dcore.Transition;

import hgm.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import pcore.ParameterCore;
import pcore.distribution.IDistribution;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/8.
 */
public class BlueprintCTBN implements IBlueprintDCore<CTBayesianNetwork> {
    private String Name;
    private LinkedHashMap<String, String[]> Microstates;
    private Map<String, int[]> States;
    private Map<String, Pair<String, String>> Transitions;
    private Map<String, List<String>>Targets;
    private boolean MicLock;

    public BlueprintCTBN(String name) {
        Name = name;
        Microstates = new LinkedHashMap<>();
        States = new TreeMap<>();
        Transitions = new TreeMap<>();
        Targets = new TreeMap<>();
        MicLock = false;
    }

    public boolean addMicrostate(String mst, String[] arr) {
        if (Microstates.containsKey(mst)) return false;
        if (MicLock) return false;

        Microstates.put(mst, arr);
        return true;
    }

    public boolean addState(String state, int[] sts) {
        if (States.containsKey(state)) return false;
        if (sts.length != Microstates.size()) return false;

        int[] mss = new int[Microstates.size()];
        int i = 0;
        for (String[] val: Microstates.values()) {
            if (sts[i] < val.length) {
                mss[i] = sts[i];
            } else {
                mss[i] = -1;
            }
            i ++;
        }

        States.put(state, mss);
        Targets.put(state, new ArrayList<>());
        return true;
    }

    public boolean addState(String state, String[] sts) {
        if (States.containsKey(state)) return false;
        if (sts.length != Microstates.size()) return false;

        int[] mss = new int[Microstates.size()];
        int i = 0;
        for (String[] val: Microstates.values()) {
            mss[i] = Arrays.asList(val).indexOf(sts[i]);

            i ++;
        }

        States.put(state, mss);
        Targets.put(state, new ArrayList<>());
        return true;
    }


    public boolean addTransition(String tr, String to) {
        return addTransition(tr, to, tr);
    }

    public boolean addTransition(String tr, String to, String dist) {
        if (Transitions.containsKey(tr)) {
            return false;
        }
        if (!States.containsKey(to)) {
            return false;
        }
        Transitions.put(tr, new Pair<>(to, dist));
        return true;
    }

    public boolean linkStateTransition(String state, String tr) {
        if (!Transitions.containsKey(tr)) {
            return false;
        }
        if (!States.containsKey(state)) {
            return false;
        }
        Targets.get(state).add(tr);
        return true;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public boolean isCompatible(ParameterCore pc) {
        IDistribution dist;
        for (Map.Entry<String, Pair<String, String>> ent: Transitions.entrySet()) {
            try {
                dist = pc.getDistribution(ent.getValue().getSecond());
                if (dist.getLower() < 0) {
                    System.out.println("Distribution "+ ent.getValue().getSecond() +
                        " is not non-negative");
                    return false;
                }
            } catch (NullPointerException e) {
                System.out.println("Distribution "+ ent.getValue().getSecond() +
                        " does not exist");
                return false;
            }
        }
        return true;
    }

    @Override
    public CTBayesianNetwork generateModel(ParameterCore pc) {
        Map<String, MicroNode> mss = makeMic();

        Map<String, int[]> stm = makeStateMap(mss);

        Map<String, String> mst = stm.entrySet().stream()
                .collect(Collectors.toMap(e -> Arrays.toString(e.getValue()), Map.Entry::getKey));

        Map<String, State> sts = stm.keySet().stream()
                .map(State::new)
                .collect(Collectors.toMap(State::getName, s->s));

        Map<String, State> wds = findWellDefined(stm, sts);

        Map<State, List<State>> sub = makeSubsets(stm, sts, wds);

        Map<String, Transition> trs = Transitions.entrySet().stream()
                .map(tr-> new Transition(tr.getKey(), sts.get(tr.getValue().getKey()),
                        pc.getDistribution(tr.getValue().getValue())))
                .collect(Collectors.toMap(Transition::getName, tr->tr));

        Map<State, List<Transition>> tas = makeTargets(sts, sub, trs);
        Map<State, Map<State, State>> links = makeLinks(sts, wds, stm, mst);

        CTBayesianNetwork model = new CTBayesianNetwork(Name, sts, trs, wds, sub, tas, links);
        sts.values().forEach(st -> st.setModel(model));
        return model;
    }

    private Map<State,Map<State, State>> makeLinks(Map<String, State> sts, Map<String, State> wds,
                                                   Map<String, int[]> stm, Map<String, String> mst) {
        Map<State,Map<State, State>> lks = new LinkedHashMap<>();
        for (Map.Entry<String, State> fe: wds.entrySet()) {
            Map<State, State> lik = new HashMap<>();
            for (Map.Entry<String, State> be: sts.entrySet()) {
                State to = sts.get(mst.get(transit(stm.get(fe.getKey()), stm.get(be.getKey()))));
                lik.put(be.getValue(), to);
            }
            lks.put(fe.getValue(), lik);
        }
        return lks;
    }

    private Map<State,List<Transition>> makeTargets(Map<String, State> sts,
                                                    Map<State, List<State>> sub, Map<String, Transition> trs) {
        Map<State, List<Transition>> tas = sub.keySet().stream()
                .collect(Collectors.toMap(e-> e, e-> new ArrayList<Transition>()));

        for (Map.Entry<String, List<String>> ent: Targets.entrySet()) {
            sub.entrySet().stream().filter(es -> es.getValue().contains(sts.get(ent.getKey())))
                    .forEach(es -> tas.get(es.getKey()).addAll(ent.getValue().stream()
                                    .map(trs::get).collect(Collectors.toList())));
        }
        return tas;
    }

    private Map<String, MicroNode> makeMic() {
        Map<String, MicroNode> mss = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> ent: Microstates.entrySet()) {
            mss.put(ent.getKey(), new MicroNode(ent.getKey(), Arrays.asList(ent.getValue())));
        }
        return mss;
    }

    private Map<String, int[]> makeStateMap(Map<String, MicroNode> mss) {
        Map<String, int[]> sts = new HashMap<>();
        for (int[] ind: product(mss)) {
            sts.put(Arrays.toString(ind), ind);
        }
        String ind;
        for (Map.Entry<String, int[]> ent: States.entrySet()) {
            ind = Arrays.toString(ent.getValue());
            sts.put(ent.getKey(), sts.get(ind));
            sts.remove(ind);
        }
        return sts;
    }

    private Map<String, State> findWellDefined(Map<String, int[]> stm, Map<String, State> sts) {
        Map<String, State> wd = new HashMap<>();
        boolean b;
        for (Map.Entry<String, int[]> ent: stm.entrySet()) {
            b = true;
            for (int i: ent.getValue()) {
                if (i < 0) b = false;

            }
            if (b) wd.put(ent.getKey(), sts.get(ent.getKey()));
        }
        return wd;
    }

    private Map<State, List<State>> makeSubsets(Map<String, int[]> stm, Map<String, State> sts, Map<String, State> wds) {
        List<State> sub;
        Map<State, List<State>> subs = new HashMap<>();
        for (Map.Entry<String, State> ent: wds.entrySet()) {
            sub = new ArrayList<>();
            sub.addAll(stm.entrySet().stream()
                    .filter(bs -> matchMic(stm.get(ent.getKey()), bs.getValue()))
                    .map(bs -> sts.get(bs.getKey()))
                    .collect(Collectors.toList()));
            subs.put(ent.getValue(), sub);
        }
        return subs;
    }

    private String transit(int[] fr, int[] by) {
        int[] to = fr.clone();
        for (int i = 0; i < to.length; i++) {
            to[i] = (by[i] >= 0)? by[i]: to[i];
        }
        return Arrays.toString(to);
    }

    private boolean matchMic(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (b[i] < 0) continue;
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    private Set<int[]> product(Map<String, MicroNode> mss){
        List<Set<Integer>> Ser = new ArrayList<>();
        for (Map.Entry<String, MicroNode> mic: mss.entrySet()){
            Set<Integer> s = new HashSet<>();
            for (int i = 0; i < mic.getValue().getMicroStates().size(); i++) {
                s.add(i);
            }
            s.add(-1);
            Ser.add(s);
        }

        Set<int[]> Pro = new HashSet<>();
        for (int i: Ser.remove(0)) {
            Pro.add(new int[]{i});
        }
        while (Ser.size() > 0){
            Pro = product(Ser.remove(0), Pro);
        }
        return Pro;
    }

    private Set<int[]> product(Set<Integer> ser, Set<int[]> pro){
        Set<int[]> S2 = new HashSet<>();
        for (int[] pr: pro){
            for(int s: ser){
                int[] p = new int[pr.length+1];
                System.arraycopy(pr, 0, p, 0, pr.length);
                p[pr.length] = s;
                S2.add(p);
            }
        }
        return S2;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        try {
            js.put("ModelType", "CTBN");
            js.put("ModelName", Name);
            js.put("Microstates", Microstates);
            js.put("States", States);
            js.put("Transitions", Transitions);
            js.put("Targets", Targets);
            js.put("Order", Microstates.keySet());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return js;
    }

    public void println() {

    }
}
