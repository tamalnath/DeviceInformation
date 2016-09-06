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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SensorDetailActivity extends BaseActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView accuracyView;
    private TextView timestampView;
    private TextView valuesView;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);
        accuracyView = (TextView) findViewById(R.id.accuracy);
        timestampView = (TextView) findViewById(R.id.timestamp);
        valuesView = (TextView) findViewById(R.id.values);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        String sensorName = getIntent().getStringExtra("sensorName");
        String sensorType = getIntent().getStringExtra("sensorType");
        for (Sensor sensorItem : sensorList) {
            if (sensorItem.getName().equals(sensorName)) {
                sensor = sensorItem;
                break;
            }
        }
        TextView view;
        view = (TextView) findViewById(R.id.id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.setText(sensor.getId());
        } else {
            view.setVisibility(View.GONE);
            findViewById(R.id.id_label).setVisibility(View.GONE);
        }
        view = (TextView) findViewById(R.id.name);
        view.setText(sensorName);
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
        view.setText(sensor.getResolution() + " unit");
        view = (TextView) findViewById(R.id.max_range);
        view.setText(sensor.getMaximumRange() + " unit");
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        accuracyView.setText(String.valueOf(sensorEvent.accuracy));
        long delay = (sensorEvent.timestamp - timestamp) / 1_000_000;
        timestampView.setText(getString(R.string.sensor_delay_unit, delay));
        timestamp = sensorEvent.timestamp;
        valuesView.setText(Arrays.toString(sensorEvent.values));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        accuracyView.setText(String.valueOf(accuracy));
    }
}
