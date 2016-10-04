package org.tamal.deviceinformation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class WifiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(container, getString(R.string.access_denied, "ACCESS_WIFI_STATE"), Snackbar.LENGTH_LONG).show();
            return recyclerView;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        Map<String, Object> wifiManagerMap = Utils.findProperties(wifiManager);
        wifiManagerMap.remove("ChannelList");
        WifiInfo wifiInfo = (WifiInfo) wifiManagerMap.remove("ConnectionInfo");
        DhcpInfo dhcpInfo = (DhcpInfo) wifiManagerMap.remove("DhcpInfo");
        if (wifiManagerMap.remove("ConfiguredNetworks") != null) {
            adapter.addButton(getString(R.string.wifi_configurations), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ID, DelegatorActivity.WIFI_CONFIGURATION);
                    startActivity(intent);
                }
            });
        }
        if (wifiManagerMap.remove("ScanResults") != null) {
            adapter.addButton(getString(R.string.wifi_scan_results), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ID, DelegatorActivity.WIFI_SCAN_RESULT);
                    startActivity(intent);
                }
            });
        }
        adapter.addHeader(getString(R.string.wifi_info));
        Map<String, Object> map = Utils.findProperties(wifiInfo);
        updateIp(map, "IpAddress");
        adapter.addMap(map);
        adapter.addHeader(getString(R.string.wifi_dhcp));
        map = Utils.findFields(dhcpInfo);
        updateIp(map, "dns1");
        updateIp(map, "dns2");
        updateIp(map, "gateway");
        updateIp(map, "ipAddress");
        updateIp(map, "netmask");
        updateIp(map, "serverAddress");
        adapter.addMap(map);
        adapter.addHeader(getString(R.string.wifi_static));
        adapter.addMap(wifiManagerMap);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private void updateIp(Map<String, Object> map, String key) {
        Integer ip = (Integer) map.get(key);
        if (ip != null) {
            map.put(key, String.format("%d.%d.%d.%d", 0xff & ip, 0xff & (ip >> 8), 0xff & (ip >> 16), 0xff & (ip >> 24)));
        }
    }

}
