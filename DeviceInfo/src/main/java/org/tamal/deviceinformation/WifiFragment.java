package org.tamal.deviceinformation;

import android.content.Context;
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

import java.util.List;
import java.util.Map;

public class WifiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        Map<String, Object> map = Utils.findProperties(wifiManager);
        map.remove("channelList");
        WifiInfo wifiInfo = (WifiInfo) map.remove("connectionInfo");
        DhcpInfo dhcpInfo = (DhcpInfo) map.remove("dhcpInfo");
        List<?> configuredNetworks = (List<?>) map.remove("configuredNetworks");
        List<?> scanResults = (List<?>) map.remove("scanResults");
        adapter.addHeader(getString(R.string.wifi_static));
        adapter.addMap(map);
        adapter.addHeader(getString(R.string.wifi_info));
        adapter.addMap(Utils.findProperties(wifiInfo));
        adapter.addHeader(getString(R.string.wifi_dhcp));
        adapter.addMap(Utils.findProperties(dhcpInfo));
        int i = 1;
        if (configuredNetworks != null) {
            for(Object wifiConfiguration : configuredNetworks) {
                adapter.addHeader(getString(R.string.wifi_configuration, i++));
                map = Utils.findProperties(wifiConfiguration);
                map.putAll(Utils.findFields(wifiConfiguration));
                adapter.addMap(map);
            }
            i = 1;
        }
        if (scanResults != null) {
            for(Object scanResult : scanResults) {
                adapter.addHeader(getString(R.string.wifi_scan, i++));
                adapter.addMap(Utils.findProperties(scanResult));
            }
        }
        wifiManager.getConnectionInfo();
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
