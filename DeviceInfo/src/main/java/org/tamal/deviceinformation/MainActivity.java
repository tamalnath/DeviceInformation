package org.tamal.deviceinformation;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends BaseActivity {

    private Fragment[] fragments = {
            new GeneralFragment(),
            new ResourceFragment(),
            new LocationFragment(),
            new NetworkFragment(),
            new WifiFragment(),
            new TelephonyFragment(),
            new BatteryFragment(),
            new SensorsFragment(),
            new BuildFragment(),
            new FontsFragment()
    };

    private int[] titles = {
            R.string.fragment_general,
            R.string.fragment_resources,
            R.string.fragment_location,
            R.string.fragment_network,
            R.string.fragment_wifi,
            R.string.fragment_telephony,
            R.string.fragment_battery,
            R.string.fragment_sensors,
            R.string.fragment_build,
            R.string.fragment_fonts
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, fragments, titles);
    }

}
