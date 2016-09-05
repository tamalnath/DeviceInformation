package org.tamal.deviceinformation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);
        String sensorName = getIntent().getStringExtra("name");
        TextView sensorView = (TextView) findViewById(R.id.sensorName);
        sensorView.setText(sensorName);
    }
}
