package org.tamal.deviceinformation;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class FontsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        for (final File fontFile : new File("/system/fonts").listFiles()) {
            String fontName = fontFile.getName().split("\\.")[0];
            adapter.addHeader(fontName, new Adapter.Data() {

                @Override
                public void decorate(RecyclerView.ViewHolder holder, int position) {
                    Typeface typeface = Typeface.createFromFile(fontFile);
                    ((TextView) holder.itemView).setTextSize(16);
                    ((TextView) holder.itemView).setTypeface(typeface);
                }
            });
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
