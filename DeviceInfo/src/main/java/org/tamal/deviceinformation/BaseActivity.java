package org.tamal.deviceinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

abstract class BaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState, @LayoutRes int layoutResID) {
        setContentView(layoutResID);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void onCreate(Bundle savedInstanceState, BaseFragment[] fragments) {
        onCreate(savedInstanceState, R.layout.activity_default);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.fragments = fragments;
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = null;
        if (this instanceof MainActivity) {
            item = menu.findItem(R.id.action_main);
        }
        if (item != null) {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        BaseFragment[] fragments;

        PagerAdapter(FragmentManager fm) {
            super(fm);
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
            return fragments[position].getTitle();
        }

    }

}
