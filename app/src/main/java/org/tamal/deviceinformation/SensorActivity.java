package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends BaseActivity {

    private SensorAdapter sensorAdapter;
    private SensorManager sensorManager;
    private List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor);
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        RecyclerView sensorView = (RecyclerView) findViewById(R.id.sensor_view);
        sensorView.setHasFixedSize(true);
        sensorView.setLayoutManager(new LinearLayoutManager(this));
        sensorAdapter = new SensorAdapter();
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
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    holder.sensorType.setImageResource(R.drawable.ic_accelerometer);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    holder.sensorType.setImageResource(R.drawable.ic_magnetometer);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    holder.sensorType.setImageResource(R.drawable.ic_gyroscope);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    holder.sensorType.setImageResource(R.drawable.ic_proximity);
                    break;
                case Sensor.TYPE_LIGHT:
                    holder.sensorType.setImageResource(R.drawable.ic_light);
                    break;
                case Sensor.TYPE_GRAVITY:
                    holder.sensorType.setImageResource(R.drawable.ic_gravity);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }

    private static class SensorHolder extends RecyclerView.ViewHolder {

        private TextView sensorName;
        private ImageView sensorType;

        public SensorHolder(View itemView) {
            super(itemView);
            sensorName = (TextView) itemView.findViewById(R.id.sensorName);
            sensorType = (ImageView) itemView.findViewById(R.id.sensorType);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SensorDetailActivity.class);
                    intent.putExtra("name", sensorName.getText());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }

}
