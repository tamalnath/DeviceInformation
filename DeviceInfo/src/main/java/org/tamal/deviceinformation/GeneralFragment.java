package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralFragment extends Fragment {

    private static final Map<Integer, String> batteryStatusMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_STATUS_.*"));
    private static final Map<Integer, String> batteryHealthMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_HEALTH_.*"));
    private static final Map<Integer, String> batteryPluggedMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_PLUGGED_.*"));
    private static final Map<String, Integer> networkTransportMap = Utils.findConstants(NetworkCapabilities.class, int.class, "TRANSPORT_.*");
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_general, container, false);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            addBatteryDetails(batteryStatus);
        }
        addNetworkDetails();

        return rootView;
    }

    private void addProperty(int resId, Object value) {
        TextView textView = (TextView) rootView.findViewById(resId);
        textView.setText(Utils.toString(value, "\n", "", "", null));
    }

    private void addBatteryDetails(Intent batteryStatus) {
        ImageView imageView = (ImageView) rootView.findViewById(R.id.battery_icon);
        imageView.setImageResource(batteryStatus.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1));

        addProperty(R.id.battery_present, batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));

        int key = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (batteryStatusMap.containsKey(key)) {
            String status = batteryStatusMap.get(key).substring("BATTERY_STATUS_".length());
            addProperty(R.id.battery_status, status);
        }

        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        if (batteryHealthMap.containsKey(key)) {
            String health = batteryHealthMap.get(key).substring("BATTERY_HEALTH_".length());
            addProperty(R.id.battery_health, health);
        }

        String plugged = getString(R.string.unknown);
        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (batteryPluggedMap.containsKey(key)) {
            plugged = batteryPluggedMap.get(key).substring("BATTERY_PLUGGED_".length());
        } else if (key == 0) {
            plugged = getString(R.string.battery_plugged_unplugged);
        }
        addProperty(R.id.battery_plugged, plugged);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percent = 100 * level / scale;
        addProperty(R.id.battery_charge, percent + "%");

        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        addProperty(R.id.battery_voltage, (voltage / 1000f) + "V");

        float temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f;
        addProperty(R.id.battery_temperature, temperature + getString(R.string.sensor_unit_deg));

        String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        if (technology != null) {
            addProperty(R.id.battery_technology, technology);
        }
    }

    private void addNetworkDetails() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        addProperty(R.id.connectivity_type, info.getTypeName() + " (" + info.getSubtypeName() + ")");
        addProperty(R.id.connectivity_state, info.getState().name() + " / " + info.getDetailedState().name());
        String extra = info.getExtraInfo();
        if (extra != null) {
            addProperty(R.id.connectivity_extra, extra);
        }
        String reason = info.getReason();
        if (reason != null) {
            addProperty(R.id.connectivity_reason, reason);
        }
        addProperty(R.id.connectivity_available, info.isAvailable());
        addProperty(R.id.connectivity_connected, info.isConnected());
        addProperty(R.id.connectivity_failover, info.isFailover());
        addProperty(R.id.connectivity_roaming, info.isRoaming());
        addProperty(R.id.connectivity_metered, connectivityManager.isActiveNetworkMetered());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String active = getString(connectivityManager.isDefaultNetworkActive() ? R.string.active : R.string.inactive);
            addProperty(R.id.connectivity_default, active);
            Network network = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                network = connectivityManager.getActiveNetwork();
            }
            for (Network n : connectivityManager.getAllNetworks()) {
                if (info.toString().equals(connectivityManager.getNetworkInfo(n).toString())) {
                    network = n;
                }
            }
            if (network != null) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                addProperty(R.id.connectivity_dns, linkProperties.getDnsServers());
                addProperty(R.id.connectivity_domains, linkProperties.getDomains());
                addProperty(R.id.connectivity_interface, linkProperties.getInterfaceName());
                addProperty(R.id.connectivity_link, linkProperties.getLinkAddresses());
                addProperty(R.id.connectivity_route, linkProperties.getRoutes());
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                addProperty(R.id.connectivity_download, getString(R.string.speed_kbps, networkCapabilities.getLinkDownstreamBandwidthKbps()));
                addProperty(R.id.connectivity_upload, getString(R.string.speed_kbps, networkCapabilities.getLinkUpstreamBandwidthKbps()));
                List<String> transportCapabilities = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : networkTransportMap.entrySet()) {
                    if (networkCapabilities.hasCapability(entry.getValue())) {
                        transportCapabilities.add(entry.getKey().substring("TRANSPORT_".length()));
                    }
                }
                addProperty(R.id.connectivity_transport, transportCapabilities);
            }
        } else {
            rootView.findViewById(R.id.network_lollipop).setVisibility(View.GONE);
        }
    }

}
