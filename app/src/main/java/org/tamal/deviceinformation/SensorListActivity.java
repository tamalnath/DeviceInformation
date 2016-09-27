package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SensorListActivity extends BaseActivity {

    private List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor_list);
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        LinearLayout layout = (LinearLayout) findViewById(R.id.sensor_view);
        for (final Sensor sensor : sensors) {
            Button button = new Button(getApplicationContext());
            button.setText(sensor.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SensorDetailActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.putExtra("sensorId", String.valueOf(sensor.getId()));
                    } else {
                        intent.putExtra("sensorId", sensor.getName() + sensor.getType() + sensor.getVendor() + sensor.getVersion());
                    }
                    startActivity(intent);
                }
            });
            layout.addView(button);
        }
    }

}
