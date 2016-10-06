package org.tamal.deviceinformation;

import android.os.Bundle;

public class MainActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        BaseFragment[] fragments = {
                new GeneralFragment().setTitle(getString(R.string.fragment_general)),
                new ResourceFragment().setTitle(getString(R.string.fragment_resources)),
                new ActivityFragment().setTitle(getString(R.string.fragment_activity)),
                new LocationFragment().setTitle(getString(R.string.fragment_location)),
                new NetworkFragment().setTitle(getString(R.string.fragment_network)),
                new WifiFragment().setTitle(getString(R.string.fragment_wifi)),
                new TelephonyFragment().setTitle(getString(R.string.fragment_telephony)),
                new BatteryFragment().setTitle(getString(R.string.fragment_battery)),
                new SensorsFragment().setTitle(getString(R.string.fragment_sensors)),
                new BuildFragment().setTitle(getString(R.string.fragment_build)),
                new FontsFragment().setTitle(getString(R.string.fragment_fonts))
        };

        super.onCreate(savedInstanceState, fragments);
    }

}
