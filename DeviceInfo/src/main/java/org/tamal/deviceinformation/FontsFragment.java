package org.tamal.deviceinformation;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class FontsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fonts, container, false);
        ViewGroup fontsLayout = (ViewGroup) rootView.findViewById(R.id.fonts);
        for (final File fontFile : new File("/system/fonts").listFiles()) {
            TextView fontView = new TextView(getContext());
            String fontName = fontFile.getName().split("\\.")[0];
            fontView.setText(fontName);
            Typeface typeface = Typeface.createFromFile(fontFile);
            fontView.setTypeface(typeface);
            fontsLayout.addView(fontView);
        }
        return rootView;
    }

}
