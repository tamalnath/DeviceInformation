package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SensorActivity extends BaseActivity {

    private static Map<Integer, String> sensorTypeMap = new TreeMap<>();
    static {
        for (Field field : Sensor.class.getDeclaredFields()) {
            try {
                if (int.class == field.getType()
                        && field.getName().startsWith("TYPE_")) {
                    sensorTypeMap.put(field.getInt(null), field.getName().substring(5));
                }
            } catch (IllegalAccessException e) {
                //
            }
        }
    }

    private List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor);
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        Collections.sort(sensors, new Comparator<Sensor>() {
            @Override
            public int compare(Sensor s1, Sensor s2) {
                String t1 = sensorTypeMap.get(s1.getType());
                String t2 = sensorTypeMap.get(s2.getType());
                if (t1 == null) {
                    return 1;
                }
                if (t2 == null) {
                    return -1;
                }
                return t1.compareTo(t2);
            }
        });
        RecyclerView sensorView = (RecyclerView) findViewById(R.id.sensor_view);
        sensorView.setHasFixedSize(true);
        sensorView.setLayoutManager(new LinearLayoutManager(this));
        SensorAdapter sensorAdapter = new SensorAdapter();
        sensorView.setAdapter(sensorAdapter);
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {

        @Override
        public SensorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_sensor, parent, false);
            return new SensorHolder(v);
        }

        @Override
        public void onBindViewHolder(SensorHolder holder, int position) {
            Sensor sensor = sensors.get(position);
            holder.sensorName.setText(sensor.getName());
            int typeId = sensor.getType();
            String type = sensorTypeMap.get(typeId);
            if (type == null) {
                type = getString(R.string.sensor_type_unknown);
            }
            holder.sensorType.setText(type);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }

    private static class SensorHolder extends RecyclerView.ViewHolder {

        private TextView sensorName;
        private TextView sensorType;

        public SensorHolder(View itemView) {
            super(itemView);
            sensorName = (TextView) itemView.findViewById(R.id.sensorName);
            sensorType = (TextView) itemView.findViewById(R.id.sensorType);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SensorDetailActivity.class);
                    intent.putExtra("sensorName", sensorName.getText());
                    intent.putExtra("sensorType", sensorType.getText());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }

}
