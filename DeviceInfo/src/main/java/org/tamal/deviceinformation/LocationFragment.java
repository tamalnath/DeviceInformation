package org.tamal.deviceinformation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class LocationFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            boolean coarse = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            boolean fine = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (coarse || fine) {
                adapter.addHeader(getString(R.string.access_denied, "ACCESS_COARSE_LOCATION / ACCESS_FINE_LOCATION"));
            } else {
                String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), perms, 0);
            }
        } else {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            List<String> names = locationManager.getAllProviders();
            for (String name : names) {
                adapter.addHeader(name);
                LocationProvider provider = locationManager.getProvider(name);
                adapter.addMap(Utils.findProperties(provider, "(?:get|is|has|requires|supports)(.*)"));
                Location location = locationManager.getLastKnownLocation(name);
                if (location == null) {
                    continue;
                }
                Map<String, Object> map = Utils.findProperties(location, "(?:get|is|has)(.*)");
                long t = (long) map.get("Time");
                CharSequence time = DateUtils.getRelativeDateTimeString(getContext(), t, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
                map.put("Time", time);
                map.remove("Extras");
                adapter.addMap(map);
            }
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
