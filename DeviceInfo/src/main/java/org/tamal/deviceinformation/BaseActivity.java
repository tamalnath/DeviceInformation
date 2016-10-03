package org.tamal.deviceinformation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

abstract class BaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState, @LayoutRes int layoutResID) {
        setContentView(layoutResID);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void onCreate(Bundle savedInstanceState, Fragment[] fragments, String[] titles) {
        onCreate(savedInstanceState, R.layout.activity_default);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.fragments = fragments;
        adapter.titles = titles;
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
    }

    protected void onCreate(Bundle savedInstanceState, Fragment[] fragments, int[] titleIds) {
        String[] titles = new String[titleIds.length];
        for (int i = 0; i < titles.length; i++) {
            titles[i] = getString(titleIds[i]);
        }
        onCreate(savedInstanceState, fragments, titles);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = null;
        int iconId = -1;
        if (this instanceof MainActivity) {
            item = menu.findItem(R.id.action_main);
            iconId = R.drawable.ic_home;
        }
        if (item != null) {
            item.setVisible(false);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && iconId != -1) {
            actionBar.setIcon(iconId);
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

        Fragment[] fragments;
        String[] titles;

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
            return titles[position];
        }

    }
}
