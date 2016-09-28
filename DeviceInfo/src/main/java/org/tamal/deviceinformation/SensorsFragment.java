package org.tamal.deviceinformation;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorsFragment extends Fragment implements SensorEventListener {

    private static final Map<Integer, String> sensorTypeMap = Utils.reverseMap(Utils.findConstants(Sensor.class, int.class, "TYPE_.*"));

    SensorManager sensorManager;
    List<Sensor> sensors;
    Map<Sensor, TextView> sensorValuesMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        ViewGroup layout = (ViewGroup) rootView.findViewById(R.id.id_sensors);
        for (Sensor sensor : sensors) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView sensorNameView = new TextView(getContext());
            sensorNameView.setTypeface(Typeface.DEFAULT_BOLD);
            sensorNameView.setText(sensor.getName());
            linearLayout.addView(sensorNameView);
            TextView sensorValueView = new TextView(getContext());
            linearLayout.addView(sensorValueView);
            linearLayout.setOnClickListener(new SensorDetailsClickListener(sensor, inflater));
            layout.addView(linearLayout);
            sensorValuesMap.put(sensor, sensorValueView);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Sensor sensor : sensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Sensor sensor : sensors) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        String unit = getUnit(sensor.getType());
        String value;
        float[] v = event.values;
        float magnitude;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
                magnitude = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                value += " (" + findNearest("GRAVITY_", magnitude) + ")";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                magnitude = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                value += " (" + findNearest("MAGNETIC_FIELD_", magnitude) + ")";
                break;
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                break;
            case Sensor.TYPE_ORIENTATION:
                value = getString(R.string.sensor_values_xyz_unit, v[2], v[0], v[1], unit);
                break;
            case Sensor.TYPE_PROXIMITY:
                if (event.values[0] == 0) {
                    value = getString(R.string.sensor_value_near);
                } else if (event.values[0] == sensor.getMaximumRange()) {
                    value = getString(R.string.sensor_value_far);
                } else {
                    value = getString(R.string.sensor_value_unit, event.values[0], unit);
                }
                break;
            case Sensor.TYPE_LIGHT:
                value = getString(R.string.sensor_value_unit, event.values[0], unit);
                value += " (" + findNearest("LIGHT_", event.values[0]) + ")";
                break;
            default:
                value = Arrays.toString(v);
        }
        TextView valueView = sensorValuesMap.get(sensor);
        valueView.setText(value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getUnit(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
                return getString(R.string.sensor_unit_ms2);
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return getString(R.string.sensor_unit_ut);
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return getString(R.string.sensor_unit_rad);
            case Sensor.TYPE_ORIENTATION:
                return getString(R.string.sensor_unit_deg);
            case Sensor.TYPE_PROXIMITY:
                return getString(R.string.sensor_unit_cm);
            case Sensor.TYPE_LIGHT:
                return getString(R.string.sensor_unit_lx);
            case Sensor.TYPE_PRESSURE:
                return getString(R.string.sensor_unit_pascal);
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return getString(R.string.sensor_unit_percent);
        }
        return "";
    }

    private static String findNearest(String prefix, float value) {
        Map<String, Float> map = Utils.findConstants(SensorManager.class, float.class, prefix + ".*");
        float absValue = Math.abs(value);
        String name = null;
        float minDelta = Float.MAX_VALUE;
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            float delta = Math.abs(entry.getValue() - absValue);
            if (delta < minDelta) {
                minDelta = delta;
                name = entry.getKey();
            }
        }
        if (name == null) {
            return null;
        }
        name = name.substring(prefix.length());
        return name;
    }

    private class SensorDetailsClickListener implements View.OnClickListener {

        private Sensor sensor;
        private LayoutInflater inflater;

        SensorDetailsClickListener(Sensor sensor, LayoutInflater inflater) {
            this.sensor = sensor;
            this.inflater = inflater;
        }

        @Override
        public void onClick(View v) {
            View viewGroup = inflater.inflate(R.layout.sensor_details, null);

            String unknown = getString(R.string.sensor_type_unknown, sensor.getType());
            String sensorType = Utils.getOrDefault(sensorTypeMap, sensor.getType(), unknown);
            String unit = getUnit(sensor.getType());
            TextView view;
            view = (TextView) viewGroup.findViewById(R.id.sensor_id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(sensor.getId());
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_id_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_type);
            view.setText(sensorType);
            view = (TextView) viewGroup.findViewById(R.id.sensor_vendor);
            view.setText(sensor.getVendor());
            view = (TextView) viewGroup.findViewById(R.id.sensor_version);
            view.setText(String.valueOf(sensor.getVersion()));
            view = (TextView) viewGroup.findViewById(R.id.sensor_power);
            view.setText(getString(R.string.sensor_power_unit, sensor.getPower()));
            view = (TextView) viewGroup.findViewById(R.id.sensor_delay);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setText(getString(R.string.sensor_delay_unit, sensor.getMinDelay(), sensor.getMaxDelay()));
            } else {
                view.setText(getString(R.string.sensor_min_delay_unit, sensor.getMinDelay()));
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_resolution);
            view.setText(getString(R.string.sensor_resolution_unit, sensor.getResolution(), unit));
            view = (TextView) viewGroup.findViewById(R.id.sensor_max_range);
            view.setText(getString(R.string.sensor_value_unit, sensor.getMaximumRange(), unit));
            view = (TextView) viewGroup.findViewById(R.id.sensor_reserved_event);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setText(getString(R.string.sensor_event_unit, sensor.getFifoReservedEventCount()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_reserved_event_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_max_event);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setText(getString(R.string.sensor_event_unit, sensor.getFifoMaxEventCount()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_max_event_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_dynamic);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(String.valueOf(sensor.isDynamicSensor()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_dynamic_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_wake_up);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(String.valueOf(sensor.isWakeUpSensor()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_wake_up_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_additional_info);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(String.valueOf(sensor.isAdditionalInfoSupported()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_additional_info_label).setVisibility(View.GONE);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(sensor.getName()).setView(viewGroup).show();
        }
    }
}
