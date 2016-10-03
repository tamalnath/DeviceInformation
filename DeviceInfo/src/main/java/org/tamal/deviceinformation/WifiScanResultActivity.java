package org.tamal.deviceinformation;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class WifiScanResultActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        int size = scanResults.size();
        Fragment[] fragments = new Fragment[size];
        String[] titles = new String[size];
        for (int i = 0; i < size; i++) {
            ScanResult scanResult = scanResults.get(i);
            WifiScanResultFragment wifiScanResultFragment = new WifiScanResultFragment();
            wifiScanResultFragment.setWifiConfiguration(scanResult);
            fragments[i] = wifiScanResultFragment;
            titles[i] = scanResult.SSID;
        }
        super.onCreate(savedInstanceState, fragments, titles);
    }

    public static class WifiScanResultFragment extends Fragment {

        private ScanResult ScanResult;

        public void setWifiConfiguration(ScanResult ScanResult) {
            this.ScanResult = ScanResult;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Adapter adapter = new Adapter();
            Map<String, Object> map = Utils.findProperties(ScanResult);
            map.putAll(Utils.findFields(ScanResult));
            adapter.addMap(map);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }
    }

}
