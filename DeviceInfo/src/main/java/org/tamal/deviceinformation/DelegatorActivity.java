package org.tamal.deviceinformation;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DelegatorActivity extends BaseActivity {

    static final String PARCELABLES = "PARCELABLES";
    static final String ACTIVITY_TITLE_ID = "ACTIVITY_TITLE_ID";

    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        setTitle(intent.getIntExtra(ACTIVITY_TITLE_ID, R.string.app_name));
        List<Parcelable> list = intent.getParcelableArrayListExtra(PARCELABLES);
        if (list == null) {
            list = Arrays.asList(intent.getParcelableArrayExtra(PARCELABLES));
        }
        int size = list.size();
        BaseFragment[] fragments = new BaseFragment[size];
        for (int i = 0; i < size; i++) {
            Parcelable parcelable = list.get(i);
            DelegatorFragment delegatorFragment = new DelegatorFragment();
            delegatorFragment.setParcelable(parcelable);
            fragments[i] = delegatorFragment;
        }
        super.onCreate(savedInstanceState, fragments);
    }

    public static class DelegatorFragment extends BaseFragment {

        private Parcelable parcelable;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Adapter adapter = new Adapter();
            Map<String, Object> map = Utils.findProperties(parcelable);
            map.putAll(Utils.findFields(parcelable));
            if (parcelable instanceof CellInfo) {
                Object cellIdentity = map.remove("CellIdentity");
                adapter.addHeader("Cell Identity");
                adapter.addMap(Utils.findProperties(cellIdentity));
                Object cellSignalStrength = map.remove("CellSignalStrength");
                adapter.addHeader("Cell Signal Strength");
                adapter.addMap(Utils.findProperties(cellSignalStrength));
                adapter.addHeader("Cell Information");
            }
            adapter.addMap(map);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }

        void setParcelable(Parcelable parcelable) {
            this.parcelable = parcelable;
        }

        @Override
        String getTitle() {
            if (parcelable instanceof WifiConfiguration) {
                return ((WifiConfiguration) parcelable).SSID;
            } else if (parcelable instanceof ScanResult) {
                ScanResult scanResult = (ScanResult) parcelable;
                String title = scanResult.SSID;
                if (title == null || title.isEmpty()) {
                    title = scanResult.BSSID;
                }
                return title;
            } else {
                return null;
            }
        }
    }
}
