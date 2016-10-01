package org.tamal.deviceinformation;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Map;

public class BuildFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_build, container, false);
        ViewGroup buildLayout = (ViewGroup) rootView.findViewById(R.id.build_build);
        final Map<String, Object> build = Utils.findConstants(Build.class, null, null);
        for (Map.Entry<String, Object> entry : build.entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(entry.getKey());
            buildLayout.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(Utils.toString(entry.getValue()));
            buildLayout.addView(valueView);
        }
        return rootView;
    }

}
