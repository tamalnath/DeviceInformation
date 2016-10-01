package org.tamal.deviceinformation;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class EnvFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_env, container, false);
        ViewGroup environmentLayout = (ViewGroup) rootView.findViewById(R.id.env_environment);
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(entry.getKey());
            environmentLayout.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(entry.getValue());
            environmentLayout.addView(valueView);
        }
        ViewGroup propertiesLayout = (ViewGroup) rootView.findViewById(R.id.env_properties);
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(Utils.toString(entry.getKey()));
            propertiesLayout.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(Utils.toString(entry.getValue()));
            propertiesLayout.addView(valueView);
        }
        return rootView;
    }

}
