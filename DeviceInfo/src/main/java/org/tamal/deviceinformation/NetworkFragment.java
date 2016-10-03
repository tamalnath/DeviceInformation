package org.tamal.deviceinformation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.Map;

public class NetworkFragment extends Fragment {

    private static final Map<String, Integer> NETWORK_CAPABILITIES = Utils.findConstants(NetworkCapabilities.class, int.class, "NET_CAPABILITY_(.+)");
    private static final Map<String, Integer> NETWORK_TRANSPORT = Utils.findConstants(NetworkCapabilities.class, int.class, "TRANSPORT_(.+)");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        adapter.addHeader(getString(R.string.network_active));
        adapter.addMap(Utils.findProperties(info));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network network = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                network = connectivityManager.getActiveNetwork();
            }
            for (Network n : connectivityManager.getAllNetworks()) {
                if (info.toString().equals(connectivityManager.getNetworkInfo(n).toString())) {
                    network = n;
                }
            }
            if (network != null) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                adapter.addMap(Utils.findProperties(linkProperties));
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                Map<String, Object> map = Utils.findProperties(networkCapabilities);
                expandArray(map, "Capabilities", NETWORK_CAPABILITIES);
                expandArray(map, "TransportTypes", NETWORK_TRANSPORT);
                adapter.addMap(map);
            }
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private static void expandArray(Map<String, Object> map, String key, Map<String, Integer> lookupMap) {
        Object object = map.get(key);
        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                int item = Array.getInt(object, i);
                for (Map.Entry<String, Integer> entry : lookupMap.entrySet()) {
                    if (entry.getValue().equals(item)) {
                        array[i] = entry.getKey();
                        break;
                    }
                }
            }
            map.put(key, array);
        }
    }
}
