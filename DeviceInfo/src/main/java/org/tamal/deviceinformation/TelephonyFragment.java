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
        map.put("CallState", Utils.findConstant(TelephonyManager.class, map.get("CallState"), "CALL_STATE_(.+)"));
        map.put("DataActivity", Utils.findConstant(TelephonyManager.class, map.get("DataActivity"), "DATA_ACTIVITY_(.+)"));
        map.put("VoiceNetworkType", Utils.findConstant(TelephonyManager.class, map.get("VoiceNetworkType"), "NETWORK_TYPE_(.+)"));
        map.put("DataNetworkType", Utils.findConstant(TelephonyManager.class, map.get("DataNetworkType"), "NETWORK_TYPE_(.+)"));
        map.put("DataState", Utils.findConstant(TelephonyManager.class, map.get("DataState"), "DATA_(\\p{Alpha}+)$"));
        map.put("PhoneType", Utils.findConstant(TelephonyManager.class, map.get("PhoneType"), "PHONE_TYPE_(.+)"));
        map.put("SimState", Utils.findConstant(TelephonyManager.class, map.get("SimState"), "SIM_STATE_(.+)"));
        adapter.addMap(map);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
