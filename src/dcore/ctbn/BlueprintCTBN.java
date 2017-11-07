package dcore.ctbn;

import dcore.IBlueprintDCore;
import dcore.State;
import dcore.Transition;

import utils.dataframe.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import pcore.ParameterCore;
import pcore.distribution.IDistribution;
import utils.json.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/8.
 */
public class BlueprintCTBN implements IBlueprintDCore<CTBayesianNetwork> {
    private class PseudoTransition implements AdapterJSONObject {
        String To, Dist;
        PseudoTransition(String to, String dist) {
            To = to;
            Dist = dist;
        }

        @Override
        public JSONObject toJSON() {
            return new JSONObject("{'To':"+To+", 'Dist':"+Dist+"}");
        }
    }


    private String Name;
    private LinkedHashMap<String, String[]> Microstates;
    private Map<String, Map<String, String>> States;
    private Map<String, PseudoTransition> Transitions;
    private Map<String, List<String>>Targets;

    public BlueprintCTBN(String name) {
        Name = name;
        Microstates = new LinkedHashMap<>();
        States = new TreeMap<>();
        Transitions = new TreeMap<>();
        Targets = new TreeMap<>();
    }

    public boolean addMicrostate(String mst, String[] arr) {
        if (Microstates.containsKey(mst)) return false;

        Microstates.put(mst, arr);
        return true;
    }

    public boolean addState(String state, Map<String, String> sts) {
        if (States.containsKey(state)) return false;

        Map<String, String> mss = new HashMap<>();
        String v;
        for (Map.Entry<String, String[]> ent: Microstates.entrySet()) {
            if (sts.containsKey(ent.getKey())) {
                v = sts.get(ent.getKey());
                if (Arrays.asList(ent.getValue()).indexOf(v) >= 0) {
                    mss.put(ent.getKey(), v);
                }
            }
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
        Transitions.put(tr, new PseudoTransition(to, dist));
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

    public boolean linkStateTransitions(String state, String[] trs) {
        boolean all = true;
        for (String tr: trs) {
            all &= linkStateTransition(state, tr);
        }
        return all;
    }

    public boolean linkStatesTransition(String[] sts, String tr) {
        boolean all = true;
        for (String state: sts) {
            all &= linkStateTransition(state, tr);
        }
        return all;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public boolean isCompatible(ParameterCore pc) {
        IDistribution dist;
        for (Map.Entry<String, PseudoTransition> ent: Transitions.entrySet()) {
            try {
                dist = pc.getDistribution(ent.getValue().Dist);
                if (dist.getLower() < 0) {
                    System.out.println("Distribution "+ ent.getValue().Dist +
                        " is not non-negative");
                    return false;
                }
            } catch (NullPointerException e) {
                System.out.println("Distribution "+ ent.getValue().Dist +
                        " does not exist");
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getRequiredDistributions() {
        Set<String> dis = Transitions.values().stream()
                .map(e -> e.Dist)
                .collect(Collectors.toSet());

        return (String[]) dis.toArray();
    }

    @Override
    public CTBayesianNetwork generateModel(ParameterCore pc) {
        Map<String, MicroNode> mss = makeMicro();

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

    private Map<String, MicroNode> makeMicro() {
        Map<String, MicroNode> mss = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> ent: Microstates.entrySet()) {
            mss.put(ent.getKey(), new MicroNode(ent.getKey(), Arrays.asList(ent.getValue())));
        }
        return mss;
    }

    private Map<String, int[]> makeStateMap(Map<String, MicroNode> mss) {
        Map<String, int[]> sts = new HashMap<>();
        for (List<MicroState> ind: product(mss)) {
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

    private Set<List<MicroState>> product(Map<String, MicroNode> mss) {
        List<Set<MicroState>> ser = mss.values().stream()
                .map(MicroNode::getSpace)
                .collect(Collectors.toList());

        List<MicroState> lms;
        Set<List<MicroState>> prod = new HashSet<>();
        for (MicroState ms: ser.remove(0)) {
            lms = new ArrayList<>();
            lms.add(ms);
            prod.add(lms);
        }
        while (ser.size() > 0) {
            prod = expand(ser.remove(0), prod);
        }
        return prod;
    }

    private Set<List<MicroState>> expand(Set<MicroState> sms, Set<List<MicroState>> prod) {
        Set<List<MicroState>> slms = new HashSet<>();
        List<MicroState> lms;

        for (List<MicroState> pro: prod) {
            for (MicroState ms: sms) {
                lms = new ArrayList<>(pro);
                lms.add(ms);
                slms.add(lms);
            }
        }
        return slms;
    }


    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();

        js.put("ModelType", "CTBN");
        js.put("ModelName", Name);
        js.put("Microstates", Microstates);

        js.put("States", States);
        js.put("Transitions", Transitions.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e-> e.getValue().toJSON())));
        js.put("Targets", Targets);
        js.put("Order", Microstates.keySet());

        return js;
    }
}
