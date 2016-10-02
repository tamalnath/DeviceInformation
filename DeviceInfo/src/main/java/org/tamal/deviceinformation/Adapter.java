package org.tamal.deviceinformation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_KEY_VALUE = 2;

    List<Data> dataList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new Header(inflater.inflate(R.layout.card_header, parent, false));
            case TYPE_KEY_VALUE:
                return new KeyValue(inflater.inflate(R.layout.card_key_value, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Data data = dataList.get(position);
        if (holder instanceof Header) {
            ((TextView) holder.itemView).setText(data.header);
        } else if (holder instanceof KeyValue) {
            ((KeyValue) holder).keyView.setText(data.key);
            ((KeyValue) holder).valueView.setText(data.value);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).header != null) {
            return TYPE_HEADER;
        }
        return TYPE_KEY_VALUE;
    }

    void addHeader(String header) {
        Data data = new Data();
        data.header = header;
        dataList.add(data);
    }

    void addMap(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Data data = new Data();
            data.key = Utils.toString(entry.getKey());
            data.value = Utils.toString(entry.getValue(), "\n", "", "", null);
            dataList.add(data);
        }
    }

    private static class Data {
        String header;
        String key;
        String value;
    }

    private static class Header extends RecyclerView.ViewHolder {

        Header(View itemView) {
            super(itemView);
        }
    }

    private static class KeyValue extends RecyclerView.ViewHolder {

        TextView keyView;
        TextView valueView;

        KeyValue(View itemView) {
            super(itemView);
            keyView = (TextView) itemView.findViewById(R.id.key);
            valueView = (TextView) itemView.findViewById(R.id.value);
        }
    }
}
