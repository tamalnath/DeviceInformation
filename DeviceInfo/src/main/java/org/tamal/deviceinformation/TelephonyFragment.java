package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

public class TelephonyFragment extends BaseFragment {

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, Object> map = Utils.findProperties(telephonyManager);
        final ArrayList<CellInfo> cellInfos = (ArrayList<CellInfo>) map.remove("AllCellInfo");
        if (cellInfos != null) {
            adapter.addButton(getString(R.string.telephony_all_cell_info), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.telephony_all_cell_info);
                    intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, cellInfos);
                    startActivity(intent);
                }
            });
        }
        final ArrayList<NeighboringCellInfo> neighboringCellInfos = (ArrayList<NeighboringCellInfo>) map.remove("NeighboringCellInfo");
        if (neighboringCellInfos != null) {
            adapter.addButton(getString(R.string.telephony_neighboring_cell_info), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.telephony_neighboring_cell_info);
                    intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, neighboringCellInfos);
                    startActivity(intent);
                }
            });
        }
        Utils.expand(map, "CallState", TelephonyManager.class, "CALL_STATE_(.+)");
        Utils.expand(map, "DataActivity", TelephonyManager.class, "DATA_ACTIVITY_(.+)");
        Utils.expand(map, "VoiceNetworkType", TelephonyManager.class, "NETWORK_TYPE_(.+)");
        Utils.expand(map, "DataNetworkType", TelephonyManager.class, "NETWORK_TYPE_(.+)");
        Utils.expand(map, "DataState", TelephonyManager.class, "DATA_(\\p{Alpha}+)$");
        Utils.expand(map, "PhoneType", TelephonyManager.class, "PHONE_TYPE_(.+)");
        Utils.expand(map, "SimState", TelephonyManager.class, "SIM_STATE_(.+)");
        adapter.addMap(map);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
