package org.tamal.deviceinformation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.Map;

public class TelephonyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, Object> map = Utils.findProperties(telephonyManager);
        map.put("callState", Utils.findConstant(TelephonyManager.class, map.get("callState"), "CALL_STATE_(.+)"));
        map.put("dataActivity", Utils.findConstant(TelephonyManager.class, map.get("dataActivity"), "DATA_ACTIVITY_(.+)"));
        map.put("voiceNetworkType", Utils.findConstant(TelephonyManager.class, map.get("voiceNetworkType"), "NETWORK_TYPE_(.+)"));
        map.put("dataNetworkType", Utils.findConstant(TelephonyManager.class, map.get("dataNetworkType"), "NETWORK_TYPE_(.+)"));
        map.put("dataState", Utils.findConstant(TelephonyManager.class, map.get("dataState"), "DATA_(\\p{Alpha}+)$"));
        map.put("phoneType", Utils.findConstant(TelephonyManager.class, map.get("phoneType"), "PHONE_TYPE_(.+)"));
        map.put("simState", Utils.findConstant(TelephonyManager.class, map.get("simState"), "SIM_STATE_(.+)"));
        adapter.addMap(map);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
