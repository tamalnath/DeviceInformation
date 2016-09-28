package org.tamal.deviceinformation;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class FontFragment extends Fragment {

    private TextView selectedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_font, container, false);
        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        ViewGroup fontsLayout = (ViewGroup) rootView.findViewById(R.id.fonts);
        for (final File fontFile : new File("/system/fonts").listFiles()) {
            TextView fontView = new TextView(getContext());
            String fontName = fontFile.getName().split("\\.")[0];
            fontView.setText(fontName);
            Typeface typeface = Typeface.createFromFile(fontFile);
            fontView.setTypeface(typeface);
            fontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView fontView = (TextView) v;
                    int existingColor = fontView.getCurrentTextColor();
                    int color = ContextCompat.getColor(getContext(), R.color.colorSelectedFont);
                    fontView.setTextColor(color);
                    editText.setTypeface(fontView.getTypeface());
                    editText.requestFocus();
                    if (selectedView != null) {
                        selectedView.setTextColor(existingColor);
                    }
                    selectedView = fontView;
                }
            });
            fontsLayout.addView(fontView);
        }
        return rootView;
    }

}
