package org.tamal.deviceinformation;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HardwareActivity extends BaseActivity {

    private Fragment[] fragments = {
            new BatteryFragment(),
            new TelephonyFragment(),
            new SensorsFragment(),
    };

    private int[] titles = {
            R.string.fragment_battery,
            R.string.fragment_telephony,
            R.string.fragment_sensors
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, fragments, titles);
    }
}
