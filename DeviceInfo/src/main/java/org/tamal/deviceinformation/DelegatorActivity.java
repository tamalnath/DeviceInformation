package org.tamal.deviceinformation;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DelegatorActivity extends BaseActivity {

    static final String ID = "id";
    static final int WIFI_CONFIGURATION = 1;
    static final int WIFI_SCAN_RESULT = 2;
    static final int TELEPHONY_ALL_CELL_INFO = 3;
    static final int TELEPHONY_NEIGHBORING_CELL_INFO = 4;
    private int id;
    private Fragment[] fragments;
    private String[] titles;

    public void onCreate(Bundle savedInstanceState) {
        id = getIntent().getIntExtra(ID, -1);
        int titleId = -1;
        switch (id) {
            case WIFI_CONFIGURATION:
                titleId = R.string.wifi_configurations;
                showWifiConfiguration();
                break;
            case WIFI_SCAN_RESULT:
                titleId = R.string.wifi_scan_results;
                showWifiScanResult();
                break;
            case TELEPHONY_ALL_CELL_INFO:
                titleId = R.string.telephony_all_cell_info;
                showTelephonyAllCellInfo();
                break;
            case TELEPHONY_NEIGHBORING_CELL_INFO:
                titleId = R.string.telephony_neighboring_cell_info;
                showTelephonyNeighboringCellInfo();
                break;
            default:
                super.onCreate(savedInstanceState);
                return;
        }
        setTitle(titleId);
        super.onCreate(savedInstanceState, fragments, titles);
    }

    private void showWifiConfiguration() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        int size = configuredNetworks.size();
        fragments = new Fragment[size];
        titles = new String[size];
        for (int i = 0; i < size; i++) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(i);
            DelegatorFragment delegatorFragment = new DelegatorFragment();
            delegatorFragment.setWifiConfiguration(wifiConfiguration);
            fragments[i] = delegatorFragment;
            titles[i] = wifiConfiguration.SSID;
        }
    }

    private void showWifiScanResult() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        int size = scanResults.size();
        fragments = new Fragment[size];
        titles = new String[size];
        for (int i = 0; i < size; i++) {
            ScanResult scanResult = scanResults.get(i);
            DelegatorFragment delegatorFragment = new DelegatorFragment();
            delegatorFragment.setScanResult(scanResult);
            fragments[i] = delegatorFragment;
            titles[i] = scanResult.SSID;
        }
    }

    private void showTelephonyAllCellInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfos = telephonyManager.getAllCellInfo();
        } else {
            cellInfos = Collections.emptyList();
        }
        int size = cellInfos.size();
        fragments = new Fragment[size];
        titles = new String[size];
        for (int i = 0; i < size; i++) {
            CellInfo cellInfo = cellInfos.get(i);
            DelegatorFragment delegatorFragment = new DelegatorFragment();
            delegatorFragment.setCellInfo(cellInfo);
            fragments[i] = delegatorFragment;
            titles[i] = "CellInfo " + i;
        }
    }

    private void showTelephonyNeighboringCellInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<NeighboringCellInfo> neighboringCellInfos;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            neighboringCellInfos = telephonyManager.getNeighboringCellInfo();
        } else {
            neighboringCellInfos = Collections.emptyList();
        }
        int size = neighboringCellInfos.size();
        fragments = new Fragment[size];
        titles = new String[size];
        for (int i = 0; i < size; i++) {
            NeighboringCellInfo neighboringCellInfo = neighboringCellInfos.get(i);
            DelegatorFragment delegatorFragment = new DelegatorFragment();
            delegatorFragment.setNeighboringCellInfo(neighboringCellInfo);
            fragments[i] = delegatorFragment;
            titles[i] = "NeighboringCellInfo " + i;
        }
    }

    public static class DelegatorFragment extends Fragment {

        private WifiConfiguration wifiConfiguration;
        private ScanResult scanResult;
        private CellInfo cellInfo;
        private NeighboringCellInfo neighboringCellInfo;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Adapter adapter = new Adapter();
            Map<String, Object> map = null;
            if (wifiConfiguration != null) {
                map = Utils.findProperties(wifiConfiguration);
                map.putAll(Utils.findFields(wifiConfiguration));
            } else  if (scanResult != null) {
                map = Utils.findProperties(scanResult);
                map.putAll(Utils.findFields(scanResult));
            } else  if (cellInfo != null) {
                map = Utils.findProperties(cellInfo);
                Object cellIdentity = map.remove("CellIdentity");
                if (cellIdentity != null) {
                    map.putAll(Utils.findProperties(cellIdentity));
                }
                Object cellSignalStrength = map.remove("CellSignalStrength");
                if (cellSignalStrength != null) {
                    map.putAll(Utils.findProperties(cellSignalStrength));
                }
            } else  if (neighboringCellInfo != null) {
                map = Utils.findProperties(neighboringCellInfo);
            }
            adapter.addMap(map);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }

        public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
            this.wifiConfiguration = wifiConfiguration;
        }

        public void setScanResult(ScanResult scanResult) {
            this.scanResult = scanResult;
        }

        public void setCellInfo(CellInfo cellInfo) {
            this.cellInfo = cellInfo;
        }

        public void setNeighboringCellInfo(NeighboringCellInfo neighboringCellInfo) {
            this.neighboringCellInfo = neighboringCellInfo;
        }

    }
}
