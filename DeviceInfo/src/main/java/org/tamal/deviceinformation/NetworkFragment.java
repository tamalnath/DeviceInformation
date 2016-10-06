package org.tamal.deviceinformation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class NetworkFragment extends BaseFragment {

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
            final Network[] networks = connectivityManager.getAllNetworks();
            if (network == null) {
                for (Network n : networks) {
                    if (info.toString().equals(connectivityManager.getNetworkInfo(n).toString())) {
                        network = n;
                    }
                }
            }
            if (network != null) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                adapter.addMap(Utils.findProperties(linkProperties));
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                Map<String, Object> map = Utils.findProperties(networkCapabilities);
                Utils.expand(map, "Capabilities", NetworkCapabilities.class, "NET_CAPABILITY_(.+)");
                Utils.expand(map, "TransportTypes", NetworkCapabilities.class, "TRANSPORT_(.+)");
                adapter.addMap(map);
                adapter.addButton(getString(R.string.activity_networks), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DelegatorActivity.class);
                        intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.activity_networks);
                        intent.putExtra(DelegatorActivity.PARCELABLES, networks);
                        startActivity(intent);
                    }
                });
            }
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
