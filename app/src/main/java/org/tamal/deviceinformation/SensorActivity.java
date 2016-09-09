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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SensorActivity extends BaseActivity {

    private Map<String, Integer> sensorTypeMap = Utils.findConstants(Sensor.class, int.class, "TYPE_.*");

    private List<Sensor> sensors;
    private String unknown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor);
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        unknown = getString(R.string.sensor_type_unknown);
        Collections.sort(sensors, new Comparator<Sensor>() {
            @Override
            public int compare(Sensor s1, Sensor s2) {
                String t1 = Utils.findKey(sensorTypeMap, s1.getType(), unknown);
                String t2 = Utils.findKey(sensorTypeMap, s2.getType(), unknown);
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
            String type = Utils.findKey(sensorTypeMap, sensor.getType(), unknown);
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
