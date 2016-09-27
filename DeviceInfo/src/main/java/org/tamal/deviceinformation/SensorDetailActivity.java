package org.tamal.deviceinformation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SensorDetailActivity extends BaseActivity implements SensorEventListener {

    private static final Map<Integer, String> sensorTypeMap = Utils.reverseMap(Utils.findConstants(Sensor.class, int.class, "TYPE_.*"));

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView accuracyView;
    private TextView timestampView;
    private TextView valuesLabel;
    private TextView valuesView;
    private TextView valueLabel;
    private TextView valueView;
    private TextView valueLikeLabel;
    private TextView valueLikeView;
    private TextView rawValueView;
    private long timestamp;
    private String unit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor_detail);
        super.onCreate(savedInstanceState);
        accuracyView = (TextView) findViewById(R.id.accuracy);
        timestampView = (TextView) findViewById(R.id.timestamp);
        valuesLabel = (TextView) findViewById(R.id.values_label);
        valuesView = (TextView) findViewById(R.id.values);
        valueLabel = (TextView) findViewById(R.id.value_label);
        valueView = (TextView) findViewById(R.id.value);
        valueLikeLabel = (TextView) findViewById(R.id.value_like_label);
        valueLikeView = (TextView) findViewById(R.id.value_like);
        rawValueView = (TextView) findViewById(R.id.value_raw);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        String sensorId = getIntent().getStringExtra("sensorId");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int id = Integer.valueOf(sensorId);
            for (Sensor item : sensorList) {
                if (id == item.getId()) {
                    sensor = item;
                    break;
                }
            }
        } else {
            for (Sensor item : sensorList) {
                String id = item.getName() + item.getType() + item.getVendor() + item.getVersion();
                if (id.equals(sensorId)) {
                    sensor = item;
                    break;
                }
            }
        }
        String unknown = getString(R.string.sensor_type_unknown, sensor.getType());
        String sensorType = Utils.getOrDefault(sensorTypeMap, sensor.getType(), unknown);
        unit = getUnit(sensor.getType());
        TextView view;
        view = (TextView) findViewById(R.id.id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(sensor.getId());
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.id_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.name);
        view.setText(sensor.getName());
        view = (TextView) findViewById(R.id.type);
        view.setText(sensorType);
        view = (TextView) findViewById(R.id.type_id);
        view.setText(String.valueOf(sensor.getType()));
        view = (TextView) findViewById(R.id.vendor);
        view.setText(sensor.getVendor());
        view = (TextView) findViewById(R.id.version);
        view.setText(String.valueOf(sensor.getVersion()));
        view = (TextView) findViewById(R.id.type_internal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            view.setText(sensor.getStringType());
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.type_internal_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.power);
        view.setText(getString(R.string.sensor_power_unit, sensor.getPower()));
        view = (TextView) findViewById(R.id.min_delay);
        view.setText(getString(R.string.sensor_delay_unit, sensor.getMinDelay()));
        view = (TextView) findViewById(R.id.max_delay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setText(getString(R.string.sensor_delay_unit, sensor.getMaxDelay()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.max_delay_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.resolution);
        view.setText(getString(R.string.sensor_resolution_unit, sensor.getResolution(), unit));
        view = (TextView) findViewById(R.id.max_range);
        view.setText(getString(R.string.sensor_value_unit, sensor.getMaximumRange(), unit));
        view = (TextView) findViewById(R.id.reserved_event);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setText(getString(R.string.sensor_event_unit, sensor.getFifoReservedEventCount()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.reserved_event_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.max_event);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setText(getString(R.string.sensor_event_unit, sensor.getFifoMaxEventCount()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.max_event_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.dynamic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(String.valueOf(sensor.isDynamicSensor()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.dynamic_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.wake_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(String.valueOf(sensor.isWakeUpSensor()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.wake_up_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.additional_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(String.valueOf(sensor.isAdditionalInfoSupported()));
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.additional_info_label).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accuracyView.setText(String.valueOf(event.accuracy));
        long delayMs = (event.timestamp - timestamp) / 1_000_000;
        timestampView.setText(getString(R.string.sensor_delay_unit, delayMs));
        timestamp = event.timestamp;
        String value = null;
        String values = null;
        String valueLike = null;
        float magnitude, x, y, z;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                magnitude = (float) Math.sqrt(x * x + y * y + z * z);
                values = getString(R.string.sensor_values_xyz_unit, x, y, z, unit);
                value = getString(R.string.sensor_value_unit, magnitude, unit);
                valueLike = findNearest("GRAVITY_", magnitude);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                magnitude = (float) Math.sqrt(x * x + y * y + z * z);
                values = getString(R.string.sensor_values_xyz_unit, x, y, z, unit);
                value = getString(R.string.sensor_value_unit, magnitude, unit);
                valueLike = findNearest("MAGNETIC_FIELD_", magnitude);
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
                valueLike = findNearest("LIGHT_", event.values[0]);
                break;
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                value = getString(R.string.sensor_values_xyz_unit, x, y, z, unit);
                break;
            case Sensor.TYPE_ORIENTATION:
                z = event.values[0];
                x = event.values[1];
                y = event.values[2];
                value = getString(R.string.sensor_values_degree, x, y, z);
                break;
        }
        if (value == null) {
            valueView.setVisibility(View.GONE);
            valueLabel.setVisibility(View.GONE);
        } else {
            valueView.setText(value);
        }
        if (values == null) {
            valuesView.setVisibility(View.GONE);
            valuesLabel.setVisibility(View.GONE);
        } else {
            valuesView.setText(values);
        }
        if (valueLike == null) {
            valueLikeView.setVisibility(View.GONE);
            valueLikeLabel.setVisibility(View.GONE);
        } else {
            valueLikeView.setText(valueLike);
        }
        StringBuilder sb = new StringBuilder();
        for (float val : event.values) {
            sb.append(String.format(Locale.getDefault(), ",%1$.2f", val));
        }
        sb.replace(0, 1, "(").append(')');
        rawValueView.setText(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        accuracyView.setText(String.valueOf(accuracy));
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

}
