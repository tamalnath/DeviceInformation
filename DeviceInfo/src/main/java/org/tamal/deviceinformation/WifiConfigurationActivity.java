package org.tamal.deviceinformation;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
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

public class WifiConfigurationActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        int size = configuredNetworks.size();
        Fragment[] fragments = new Fragment[size];
        String[] titles = new String[size];
        for (int i = 0; i < size; i++) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(i);
            WifiConfigurationFragment wifiConfigurationFragment = new WifiConfigurationFragment();
            wifiConfigurationFragment.setWifiConfiguration(wifiConfiguration);
            fragments[i] = wifiConfigurationFragment;
            titles[i] = wifiConfiguration.SSID;
        }
        super.onCreate(savedInstanceState, fragments, titles);
    }

    public static class WifiConfigurationFragment extends Fragment {

        private WifiConfiguration wifiConfiguration;

        public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
            this.wifiConfiguration = wifiConfiguration;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Adapter adapter = new Adapter();
            Map<String, Object> map = Utils.findProperties(wifiConfiguration);
            map.putAll(Utils.findFields(wifiConfiguration));
            adapter.addMap(map);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }
    }
}
