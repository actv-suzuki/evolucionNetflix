// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.internal;

import java.util.Iterator;
import android.content.Context;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public final class az
{
    public static List<String> a(final JSONObject jsonObject, final String s) throws JSONException {
        final JSONArray optJSONArray = jsonObject.optJSONArray(s);
        if (optJSONArray != null) {
            final ArrayList<String> list = new ArrayList<String>(optJSONArray.length());
            for (int i = 0; i < optJSONArray.length(); ++i) {
                list.add(optJSONArray.getString(i));
            }
            return (List<String>)Collections.unmodifiableList((List<?>)list);
        }
        return null;
    }
    
    public static void a(final Context context, final String s, final cj cj, final String s2, final boolean b, final List<String> list) {
        String s3;
        if (b) {
            s3 = "1";
        }
        else {
            s3 = "0";
        }
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String s4 = iterator.next().replaceAll("@gw_adlocid@", s2).replaceAll("@gw_adnetrefresh@", s3).replaceAll("@gw_qdata@", cj.is.fN).replaceAll("@gw_sdkver@", s).replaceAll("@gw_sessid@", ck.iu).replaceAll("@gw_seqnum@", cj.hu);
            if (cj.gb != null) {
                s4 = s4.replaceAll("@gw_adnetid@", cj.gb.fD).replaceAll("@gw_allocid@", cj.gb.fF);
            }
            new cr(context, s, s4).start();
        }
    }
}
