package org.tamal.deviceinformation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class NetworksActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        int size = networks.length;
        Fragment[] fragments = new Fragment[size];
        String[] titles = new String[size];
        for (int i = 0; i < size; i++) {
            NetworkDetailsFragment networkDetailsFragment = new NetworkDetailsFragment();
            networkDetailsFragment.setConfiguration(connectivityManager, networks[i]);
            fragments[i] = networkDetailsFragment;
            titles[i] = networks[i].toString();
        }
        super.onCreate(savedInstanceState, fragments, titles);
    }

    public static class NetworkDetailsFragment extends Fragment {

        ConnectivityManager connectivityManager;
        Network network;
        public void setConfiguration(ConnectivityManager connectivityManager, Network network) {
            this.connectivityManager = connectivityManager;
            this.network = network;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return null;
            }
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Adapter adapter = new Adapter();
            adapter.addMap(Utils.findProperties(connectivityManager.getNetworkInfo(network)));
            LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
            adapter.addMap(Utils.findProperties(linkProperties));
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            Map<String, Object> map = Utils.findProperties(networkCapabilities);
            Utils.expand(map, "Capabilities", NetworkCapabilities.class, "NET_CAPABILITY_(.+)");
            Utils.expand(map, "TransportTypes", NetworkCapabilities.class, "TRANSPORT_(.+)");
            adapter.addMap(map);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }
    }
}
