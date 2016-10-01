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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkFragment extends Fragment {

    private static final Map<String, Integer> networkTransportMap = Utils.findConstants(NetworkCapabilities.class, int.class, "TRANSPORT_(.+)");
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_network, container, false);
        addNetworkDetails();
        return rootView;
    }

    private void addProperty(int resId, Object value) {
        TextView textView = (TextView) rootView.findViewById(resId);
        textView.setText(Utils.toString(value, "\n", "", "", null));
    }

    private void addNetworkDetails() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        addProperty(R.id.connectivity_type, info.getTypeName() + " (" + info.getSubtypeName() + ")");
        addProperty(R.id.connectivity_state, info.getState().name() + " / " + info.getDetailedState().name());
        String extra = info.getExtraInfo();
        if (extra != null) {
            addProperty(R.id.connectivity_extra, extra);
        }
        String reason = info.getReason();
        if (reason != null) {
            addProperty(R.id.connectivity_reason, reason);
        }
        addProperty(R.id.connectivity_available, info.isAvailable());
        addProperty(R.id.connectivity_connected, info.isConnected());
        addProperty(R.id.connectivity_failover, info.isFailover());
        addProperty(R.id.connectivity_roaming, info.isRoaming());
        addProperty(R.id.connectivity_metered, connectivityManager.isActiveNetworkMetered());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String active = getString(connectivityManager.isDefaultNetworkActive() ? R.string.active : R.string.inactive);
            addProperty(R.id.connectivity_default, active);
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
                addProperty(R.id.connectivity_dns, linkProperties.getDnsServers());
                addProperty(R.id.connectivity_domains, linkProperties.getDomains());
                addProperty(R.id.connectivity_interface, linkProperties.getInterfaceName());
                addProperty(R.id.connectivity_link, linkProperties.getLinkAddresses());
                addProperty(R.id.connectivity_route, linkProperties.getRoutes());
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                addProperty(R.id.connectivity_download, getString(R.string.speed_kbps, networkCapabilities.getLinkDownstreamBandwidthKbps()));
                addProperty(R.id.connectivity_upload, getString(R.string.speed_kbps, networkCapabilities.getLinkUpstreamBandwidthKbps()));
                List<String> transportCapabilities = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : networkTransportMap.entrySet()) {
                    if (networkCapabilities.hasCapability(entry.getValue())) {
                        transportCapabilities.add(entry.getKey().substring("TRANSPORT_".length()));
                    }
                }
                addProperty(R.id.connectivity_transport, transportCapabilities);
            }
        } else {
            rootView.findViewById(R.id.network_lollipop).setVisibility(View.GONE);
        }
    }

}
