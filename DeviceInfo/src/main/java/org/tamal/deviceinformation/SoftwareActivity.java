package org.tamal.deviceinformation;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class SoftwareActivity extends BaseActivity {

    private Fragment[] fragments = {
            new BuildFragment(),
            new NetworkFragment(),
            new FontsFragment()
    };

    private int[] titles = {
            R.string.fragment_build,
            R.string.fragment_network,
            R.string.fragment_fonts
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, fragments, titles);
    }

}
