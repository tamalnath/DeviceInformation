package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Map;

public class WifiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        Map<String, Object> wifiManagerMap = Utils.findProperties(wifiManager);
        wifiManagerMap.remove("ChannelList");
        WifiInfo wifiInfo = (WifiInfo) wifiManagerMap.remove("ConnectionInfo");
        DhcpInfo dhcpInfo = (DhcpInfo) wifiManagerMap.remove("DhcpInfo");
        Object configuredNetworks = wifiManagerMap.remove("ConfiguredNetworks");
        Object scanResults = wifiManagerMap.remove("ScanResults");
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
        if (configuredNetworks != null) {
            adapter.addButton(getString(R.string.activity_wifi_configuration), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), WifiConfigurationActivity.class));
                }
            });
        }
        if (scanResults != null) {
            adapter.addButton(getString(R.string.activity_wifi_scan), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), WifiScanResultActivity.class));
                }
            });
        }
        adapter.addHeader(getString(R.string.wifi_static));
        adapter.addMap(wifiManagerMap);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private void updateIp(Map<String, Object> map, String key) {
        int ip = (int) map.get(key);
        String addr = String.format("%d.%d.%d.%d", 0xff & ip, 0xff & (ip >> 8), 0xff & (ip >> 16), 0xff & (ip >> 24));
        map.put(key, addr);
    }

}
