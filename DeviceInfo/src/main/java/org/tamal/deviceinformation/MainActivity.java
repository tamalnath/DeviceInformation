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
    };

    private int[] titles = {
            R.string.fragment_general,
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, fragments, titles);
    }

}
