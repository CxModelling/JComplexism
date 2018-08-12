package org.twz.statespace.ctbn;

import org.twz.dag.actor.Sampler;
import org.twz.statespace.IBlueprintDCore;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.dag.ParameterCore;
import org.twz.io.AdapterJSONObject;
import org.twz.io.FnJSON;

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
    private LinkedHashMap<String, String[]> MicroStates;
    private Map<String, Map<String, String>> States;
    private Map<String, PseudoTransition> Transitions;
    private Map<String, List<String>>Targets;
    private JSONObject JS;

    public BlueprintCTBN(String name) {
        Name = name;
        MicroStates = new LinkedHashMap<>();
        States = new TreeMap<>();
        Transitions = new TreeMap<>();
        Targets = new TreeMap<>();
        JS = null;
    }

    public BlueprintCTBN(JSONObject js) {
        this(js.getString("ModelName"));
        JSONObject sub, temp;
        JSONArray order;
        Iterator<?> keys;

        order = js.getJSONArray("Order");

        sub = js.getJSONObject("Microstates");
        for (int i = 0; i < order.length(); i++) {
            String key = order.getString(i);
            addMicroState(key, FnJSON.toStringArray(sub.getJSONArray(key)));
        }

        sub = js.getJSONObject("States");
        keys = sub.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            addState(key, FnJSON.toStringMap(sub.getJSONObject(key)));
        }

        sub = js.getJSONObject("Transitions");
        keys = sub.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            temp = sub.getJSONObject(key);
            addTransition(key, temp.getString("To"), temp.getString("Dist"));
        }

        sub = js.getJSONObject("Targets");
        keys = sub.keys();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            linkStateTransitions(key, FnJSON.toStringArray(sub.getJSONArray(key)));
        }

        JS = js;
    }

    public boolean addMicroState(String mst, String[] arr) {
        if (MicroStates.containsKey(mst)) return false;

        MicroStates.put(mst, arr);
        return true;
    }

    public boolean addState(String state, Map<String, String> sts) {
        if (States.containsKey(state)) return false;

        Map<String, String> mss = new HashMap<>();
        String v;
        for (Map.Entry<String, String[]> ent: MicroStates.entrySet()) {
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
        for (Map.Entry<String, PseudoTransition> ent: Transitions.entrySet()) {
            try {
                pc.getSampler(ent.getValue().Dist);
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

        Map<String, List<MicroState>> stm = makeStateMap(mss);

        Map<String, String> mst = stm.entrySet().stream()
                .collect(Collectors.toMap(e -> formName(e.getValue()), Map.Entry::getKey));

        Map<String, State> sts = stm.keySet().stream()
                .map(State::new)
                .collect(Collectors.toMap(State::getName, s->s));

        Map<String, State> wds = findWellDefined(stm, sts);

        Map<State, List<State>> sub = makeSubsets(stm, sts, sts);

        Map<String, Transition> trs = new HashMap<>();
        for (Map.Entry<String, PseudoTransition> entry : Transitions.entrySet()) {
            Sampler samp = pc.getSampler(entry.getValue().Dist);
            Transition tr = new Transition(entry.getKey(), sts.get(entry.getValue().To), samp);
            trs.put(tr.getName(), tr);
        }


        Map<State, List<Transition>> tas = makeTargets(sts, sub, trs);
        Map<State, Map<State, State>> links = makeLinks(sts, wds, stm, mst);

        CTBayesianNetwork model = new CTBayesianNetwork(Name, sts, trs, wds, sub, tas, links, toJSON());
        sts.values().forEach(st -> st.setModel(model));
        return model;
    }

    private Map<State,Map<State, State>> makeLinks(Map<String, State> sts, Map<String, State> wds,
                                                   Map<String, List<MicroState>> stm, Map<String, String> mst) {
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
        for (Map.Entry<String, String[]> ent: MicroStates.entrySet()) {
            mss.put(ent.getKey(), new MicroNode(ent.getKey(), Arrays.asList(ent.getValue())));
        }
        return mss;
    }

    private Map<String, List<MicroState>> makeStateMap(Map<String, MicroNode> mss) {
        Map<String, List<MicroState>> sts = new HashMap<>();
        for (List<MicroState> ind: product(mss)) {
            sts.put(formName(ind), ind);
        }
        String ind;
        for (Map.Entry<String, Map<String, String>> ent: States.entrySet()) {
            ind = formName(ent.getValue());
            sts.put(ent.getKey(), sts.get(ind));
            sts.remove(ind);
        }
        return sts;
    }

    private Map<String, State> findWellDefined(Map<String, List<MicroState>> stm, Map<String, State> sts) {
        Map<String, State> wd = new HashMap<>();
        stm.entrySet().stream()
                .filter(ent -> ent.getValue().stream().filter(e-> e==MicroState.NullState).count() == 0)
                .forEach(ent -> wd.put(ent.getKey(), sts.get(ent.getKey())));
        return wd;
    }

    private Map<State, List<State>> makeSubsets(Map<String, List<MicroState>> stm, Map<String, State> sts, Map<String, State> wds) {
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

    private String transit(List<MicroState> fr, List<MicroState> by) {
        List<MicroState> to = new ArrayList<>(fr);

        for (int i = 0; i < to.size(); i++) {
            if (by.get(i) != MicroState.NullState) {
                to.set(i, by.get(i));
            }
        }
        return formName(to);
    }

    private boolean matchMic(List<MicroState> a, List<MicroState> b) {
        for (int i = 0; i < a.size(); i++) {
            if (b.get(i) == MicroState.NullState) continue;
            if (a.get(i) != b.get(i)) return false;
        }
        return true;
    }

    private String formName(List<MicroState> lms) {
        List<String> pairs = new ArrayList<>();
        int i = 0;
        MicroState ms;
        for (String k: MicroStates.keySet()) {
            ms = lms.get(i);
            if (ms != MicroState.NullState) {
                pairs.add(k+"="+ms.toString());
            }
            i++;
        }
        return "[" + pairs.stream().collect(Collectors.joining(", ")) + "]";
    }

    private String formName(Map<String, String> lms) {
        List<String> pairs = MicroStates.keySet().stream()
                .filter(lms::containsKey).map(k -> k + "=" + lms.get(k))
                .collect(Collectors.toList());

        return "[" + pairs.stream().collect(Collectors.joining(", ")) + "]";

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
        if (JS == null) {
            buildJSON();
        }
        return JS;
    }

    public void buildJSON() {
        JS  = new JSONObject();

        JS.put("ModelType", "CTBN");
        JS.put("ModelName", Name);
        JS.put("Microstates", MicroStates);

        JS.put("States", States);
        JS.put("Transitions", Transitions.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e-> e.getValue().toJSON())));
        JS.put("Targets", Targets);
        JS.put("Order", MicroStates.keySet());
    }
}
