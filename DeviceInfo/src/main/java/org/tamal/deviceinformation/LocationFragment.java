package org.tamal.deviceinformation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class LocationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(container, getString(R.string.access_denied, "ACCESS_COARSE_LOCATION"), Snackbar.LENGTH_LONG).show();
            return recyclerView;
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(container, getString(R.string.access_denied, "ACCESS_FINE_LOCATION"), Snackbar.LENGTH_LONG).show();
            return recyclerView;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> names = locationManager.getAllProviders();
        for (String name : names) {
            adapter.addHeader(name);
            LocationProvider provider = locationManager.getProvider(name);
            adapter.addMap(Utils.findProperties(provider, "(?:get|is|has|requires|supports)(.*)"));
            Location location = locationManager.getLastKnownLocation(name);
            adapter.addMap(Utils.findProperties(location, "(?:get|is|has)(.*)"));
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
