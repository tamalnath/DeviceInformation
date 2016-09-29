package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralFragment extends Fragment {

    private static final Map<Integer, String> batteryStatusMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_STATUS_.*"));
    private static final Map<Integer, String> batteryHealthMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_HEALTH_.*"));
    private static final Map<Integer, String> batteryPluggedMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_PLUGGED_.*"));
    private static final Map<String, Integer> networkTransportMap = Utils.findConstants(NetworkCapabilities.class, int.class, "TRANSPORT_.*");
    private ViewGroup viewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        viewGroup = (ViewGroup) rootView.findViewById(R.id.general);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            createHeading(getString(R.string.battery));
            addBatteryDetails(batteryStatus);
        }
        createHeading(getString(R.string.connectivity_active));
        addNetworkDetails();

        return rootView;
    }

    private void addProperty(int resId, Object value) {
        TextView keyView = new TextView(getContext());
        keyView.setTypeface(Typeface.DEFAULT_BOLD);
        keyView.setText(getString(resId));
        viewGroup.addView(keyView);
        TextView valueView = new TextView(getContext());
        valueView.setText(Utils.toString(value, "\n", "", "", null));
        viewGroup.addView(valueView);
    }

    private void addMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(entry.getKey());
            viewGroup.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(entry.getValue());
            viewGroup.addView(valueView);
        }
    }

    private void createHeading(String heading) {
        TextView headingView = new TextView(getContext());
        headingView.setText(heading);
        if (Build.VERSION.SDK_INT < 23) {
            headingView.setTextAppearance(getContext(), R.style.Heading);
        } else {
            headingView.setTextAppearance(R.style.Heading);
        }
        viewGroup.addView(headingView);
    }

    private void addBatteryDetails(Intent batteryStatus) {
        Boolean present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        addProperty(R.string.battery_present, present);

        String status = getString(R.string.unknown);
        int key = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (batteryStatusMap.containsKey(key)) {
            status = batteryStatusMap.get(key).substring("BATTERY_STATUS_".length());
        }
        addProperty(R.string.battery_status, status);

        String health = getString(R.string.unknown);
        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        if (batteryHealthMap.containsKey(key)) {
            health = batteryHealthMap.get(key).substring("BATTERY_HEALTH_".length());
        }
        addProperty(R.string.battery_health, health);

        String plugged = getString(R.string.unknown);
        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (batteryPluggedMap.containsKey(key)) {
            plugged = batteryPluggedMap.get(key).substring("BATTERY_PLUGGED_".length());
        } else if (key == 0) {
            plugged = getString(R.string.battery_plugged_unplugged);
        }
        addProperty(R.string.battery_plugged, plugged);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percent = 100 * level / scale;
        addProperty(R.string.battery_charge_level, percent + "%");

        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        addProperty(R.string.battery_voltage, (voltage / 1000f) + "V");

        float temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f;
        addProperty(R.string.battery_temperature, temperature + getString(R.string.sensor_unit_deg));

        String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        if (technology == null) {
            technology = getString(R.string.unknown);
        }
        addProperty(R.string.battery_technology, technology);
    }

    private void addNetworkDetails() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        addProperty(R.string.connectivity_type, info.getTypeName() + " (" + info.getSubtypeName() + ")");
        addProperty(R.string.connectivity_state, info.getState().name() + " / " + info.getDetailedState().name());
        String extra = info.getExtraInfo();
        if (extra != null) {
            addProperty(R.string.connectivity_extra, extra);
        }
        String reason = info.getReason();
        if (reason != null) {
            addProperty(R.string.connectivity_reason, reason);
        }
        addProperty(R.string.connectivity_available, info.isAvailable());
        addProperty(R.string.connectivity_connected, info.isConnected());
        addProperty(R.string.connectivity_failover, info.isFailover());
        addProperty(R.string.connectivity_roaming, info.isRoaming());
        addProperty(R.string.connectivity_metered, connectivityManager.isActiveNetworkMetered());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String active = getString(connectivityManager.isDefaultNetworkActive() ? R.string.active : R.string.inactive);
            addProperty(R.string.connectivity_default, active);
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
                addProperty(R.string.connectivity_dns, linkProperties.getDnsServers());
                addProperty(R.string.connectivity_domains, linkProperties.getDomains());
                addProperty(R.string.connectivity_interface, linkProperties.getInterfaceName());
                addProperty(R.string.connectivity_link, linkProperties.getLinkAddresses());
                addProperty(R.string.connectivity_route, linkProperties.getRoutes());
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                addProperty(R.string.connectivity_download, getString(R.string.speed_kbps, networkCapabilities.getLinkDownstreamBandwidthKbps()));
                addProperty(R.string.connectivity_upload, getString(R.string.speed_kbps, networkCapabilities.getLinkUpstreamBandwidthKbps()));
                List<String> transportCapabilities = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : networkTransportMap.entrySet()) {
                    if (networkCapabilities.hasCapability(entry.getValue())) {
                        transportCapabilities.add(entry.getKey().substring("TRANSPORT_".length()));
                    }
                }
                addProperty(R.string.connectivity_transport, transportCapabilities);
            }
        }
    }

}
