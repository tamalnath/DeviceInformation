package org.tamal.deviceinformation;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), getResources());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments = {
                new GeneralFragment(),
                new SensorsFragment()
        };

        private String[] fragmentTitles = new String[fragments.length];

        PagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            fragmentTitles[0] = resources.getString(R.string.fragment_general);
            fragmentTitles[1] = resources.getString(R.string.fragment_sensors);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles[position];
        }
    }

}
