package org.tamal.deviceinformation;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class BatteryFragment extends Fragment {

    private static final Map<Integer, String> batteryStatusMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_STATUS_.*"));
    private static final Map<Integer, String> batteryHealthMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_HEALTH_.*"));
    private static final Map<Integer, String> batteryPluggedMap = Utils.reverseMap(Utils.findConstants(BatteryManager.class, int.class, "BATTERY_PLUGGED_.*"));
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_battery, container, false);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            addBatteryDetails(batteryStatus);
        }
        return rootView;
    }

    private void addProperty(int resId, Object value) {
        TextView textView = (TextView) rootView.findViewById(resId);
        textView.setText(Utils.toString(value, "\n", "", "", null));
    }

    private void addBatteryDetails(Intent batteryStatus) {
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

}
