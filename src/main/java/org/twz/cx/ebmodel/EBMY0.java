package org.twz.cx.ebmodel;

import com.sun.xml.internal.ws.wsdl.writer.document.Definitions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.cx.abmodel.ABMY0;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchY0;
import org.twz.cx.mcore.IY0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EBMY0 extends BranchY0 {
    @Override
    public void matchModelInfo(AbsSimModel model) {
        EquationBasedModel ebm = (EquationBasedModel) model;
        List<String> ys = Entries.stream().map(m->m.getString("y")).collect(Collectors.toList());
        String[] yns = ebm.getYNames();
        for (String yn : yns) {
            if (!ys.contains(yn)) {
                append("{'y':'" + yn + "', 'n': 0}");
            }
        }
    }

    @Override
    public void append(JSONObject ent) {
        if (ent.has("n") && ent.has("y")) {
            Entries.add(ent);
        }
    }

}
