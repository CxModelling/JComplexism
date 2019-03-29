package org.twz.datastructure;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;
import org.twz.prob.Sample;

import java.util.ArrayList;
import java.util.List;

public class SexAgeProbability implements AdapterJSONObject {
    private double[] Probability;
    private String[] Labels;

    public SexAgeProbability(String[] labels, double[] probability) {
        Probability = probability;
        Labels = labels;
    }

    public String sample() {
        return Labels[Sample.sample(Probability)];
    }

    public String[] sample(int n) {
        String[] sam = new String[n];
        int j = 0;
        for (int i: Sample.sample(Probability, n)) {
            sam[j] = Labels[i];
            j++;
        }
        return sam;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        for (int i = 0; i < Probability.length; i++) {
            js.put(Labels[i], Probability[i]);
        }
        return js;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        List<String> sh = new ArrayList<>();
        for (int i = 0; i < Labels.length; i++) {
            sh.add(Labels[i] + "=" + Probability[i]);
        }
        sb.append(String.join(",", sh));
        sb.append("}");
        return sb.toString();
    }
}
