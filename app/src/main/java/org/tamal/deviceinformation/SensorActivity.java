package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
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

    private static final Map<Integer, String> sensorTypeMap = Utils.reverseMap(Utils.findConstants(Sensor.class, int.class, "TYPE_.*"));
    private static String unknown;

    private List<Sensor> sensors;

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
                String t1 = Utils.getOrDefault(sensorTypeMap, s1.getType(), unknown);
                String t2 = Utils.getOrDefault(sensorTypeMap, s2.getType(), unknown);
                return t1.compareTo(t2);
            }
        });
        RecyclerView sensorView = (RecyclerView) findViewById(R.id.sensor_view);
        sensorView.setHasFixedSize(true);
        sensorView.setLayoutManager(new LinearLayoutManager(this));
        SensorAdapter sensorAdapter = new SensorAdapter();
        sensorView.setAdapter(sensorAdapter);
    }

    private static class SensorHolder extends RecyclerView.ViewHolder {

        private Sensor sensor;
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.putExtra("sensorId", String.valueOf(sensor.getId()));
                    } else {
                        intent.putExtra("sensorId", sensor.getName() + sensor.getType() + sensor.getVendor() + sensor.getVersion());
                    }
                    intent.putExtra("sensorType", sensorType.getText());
                    view.getContext().startActivity(intent);
                }
            });
        }

        public void update(Sensor sensor) {
            this.sensor = sensor;
            sensorName.setText(sensor.getName());
            String type = Utils.getOrDefault(sensorTypeMap, sensor.getType(), unknown);
            sensorType.setText(type);
        }
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
            holder.update(sensors.get(position));
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }

}
