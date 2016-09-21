package org.tamal.deviceinformation;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ShowDataHelperActivity extends BaseActivity {

    private Map<Object, Object> map = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_data_helper);
        super.onCreate(savedInstanceState);
        int ref = getIntent().getIntExtra("ref", -1);
        int titleId;
        switch (ref) {
            case R.id.build_information:
                addBuildInformation();
                titleId = R.string.build_information;
                break;
            case R.id.environment_variables:
                map.putAll(System.getenv());
                titleId = R.string.environment_variables;
                break;
            case R.id.system_properties:
                map.putAll(System.getProperties());
                titleId = R.string.system_properties;
                break;
            default:
                titleId = R.string.activity_show_data_helper;
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleId);
        }
        GridLayout gridLayout = (GridLayout) findViewById(R.id.data_grid);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            TextView key = new TextView(getApplicationContext());
            key.setText(String.valueOf(entry.getKey()));
            gridLayout.addView(key);
            TextView value = new TextView(getApplicationContext());
            value.setText(String.valueOf(entry.getValue()));
            gridLayout.addView(value);
        }
    }

    private void addBuildInformation() {
        Map<String, Object> build = Utils.findConstants(Build.class, null, null);
        for (Map.Entry<String, Object> entry : build.entrySet()) {
            map.put(entry.getKey(), Utils.toString(entry.getValue()));
        }
        Map<String, Object> version = Utils.findConstants(Build.VERSION.class, null, null);
        for (Map.Entry<String, Object> entry : version.entrySet()) {
            map.put("VERSION." + entry.getKey(), Utils.toString(entry.getValue()));
        }
    }
}
