package org.tamal.deviceinformation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_BUTTON = 2;
    private static final int TYPE_KEY_VALUE = 3;

    private List<TypedData> dataList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new Holder(inflater.inflate(R.layout.card_header, parent, false));
            case TYPE_BUTTON:
                return new Holder(inflater.inflate(R.layout.card_button, parent, false));
            case TYPE_KEY_VALUE:
                return new KeyValueHolder(inflater.inflate(R.layout.card_key_value, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        dataList.get(position).decorate(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getViewType();
    }

    void addHeader(String header) {
        addHeader(header, null);
    }

    void addHeader(final String header, final Data data) {
        dataList.add(new TypedData() {

            public int getViewType() {
                return TYPE_HEADER;
            }

            @Override
            public void decorate(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(header);
                if (data != null) {
                    data.decorate(holder, position);
                }
            }
        });
    }

    void addButton(String button, View.OnClickListener listener) {
        addButton(button, listener, null);
    }

    void addButton(final String button, final View.OnClickListener listener, final Data data) {
        dataList.add(new TypedData() {

            @Override
            public int getViewType() {
                return TYPE_BUTTON;
            }

            @Override
            public void decorate(RecyclerView.ViewHolder holder, int position) {
                ((Button) holder.itemView).setText(button);
                holder.itemView.setOnClickListener(listener);
                if (data != null) {
                    data.decorate(holder, position);
                }
            }
        });
    }

    void addMap(Map<?, ?> map) {
        addMap(map, null);
    }

    void addMap(Map<?, ?> map, final Data data) {
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            dataList.add(new TypedData() {

                @Override
                public int getViewType() {
                    return TYPE_KEY_VALUE;
                }

                @Override
                public void decorate(RecyclerView.ViewHolder viewHolder, int position) {
                    KeyValueHolder holder = (KeyValueHolder) viewHolder;
                    holder.keyView.setText(Utils.toString(entry.getKey()));
                    holder.valueView.setText(Utils.toString(entry.getValue(), "\n", "", "", null));
                    if (data != null) {
                        data.decorate(holder, position);
                    }
                }
            });
        }
    }

    interface Data {
        void decorate(RecyclerView.ViewHolder viewHolder, int position);
    }

    interface TypedData extends Data {
        int getViewType();
    }

    static class Holder extends RecyclerView.ViewHolder {

        Holder(View itemView) {
            super(itemView);
        }
    }

    private static class KeyValueHolder extends RecyclerView.ViewHolder {

        TextView keyView;
        TextView valueView;

        KeyValueHolder(View itemView) {
            super(itemView);
            keyView = (TextView) itemView.findViewById(R.id.key);
            valueView = (TextView) itemView.findViewById(R.id.value);
        }
    }

}
