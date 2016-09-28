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
        ViewGroup environmentLayout = (ViewGroup) rootView.findViewById(R.id.build_environment);
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(entry.getKey());
            environmentLayout.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(entry.getValue());
            environmentLayout.addView(valueView);
        }
        ViewGroup propertiesLayout = (ViewGroup) rootView.findViewById(R.id.build_properties);
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            TextView keyView = new TextView(getContext());
            keyView.setTypeface(Typeface.DEFAULT_BOLD);
            keyView.setText(Utils.toString(entry.getKey()));
            propertiesLayout.addView(keyView);
            TextView valueView = new TextView(getContext());
            valueView.setText(Utils.toString(entry.getValue()));
            propertiesLayout.addView(valueView);
        }
        ViewGroup fontsLayout = (ViewGroup) rootView.findViewById(R.id.build_fonts);
        for (final File fontFile : new File("/system/fonts").listFiles()) {
            TextView fontView = new TextView(getContext());
            final String fontName = fontFile.getName().split("\\.")[0];
            fontView.setText(fontName);
            fontsLayout.addView(fontView);
            fontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = new EditText(getContext());
                    editText.setTypeface(Typeface.createFromFile(fontFile));
                    editText.setText(R.string.build_font_try);
                    editText.setMaxLines(5);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(fontName).setView(editText).show();
                }
            });
        }
        return rootView;
    }

}
