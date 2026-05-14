package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.location.Location;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private DBHelper dbHelper;
    private Spinner distanceSpinner;
    private Button distanceSearchButton;
    private GoogleMap gMap;
    private LatLng userLocation;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            gMap = googleMap;

            dbHelper = new DBHelper(getContext());

            getCurrentLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        distanceSpinner = view.findViewById(R.id.distanceSpinner);
        distanceSearchButton = view.findViewById(R.id.distanceSearchButton);

        setupDistanceSpinner();

        distanceSearchButton.setOnClickListener(v -> {

            if (userLocation != null) {
                loadMarkersWithinDistance();
            }
        });
    }

    private void setupDistanceSpinner() {
        String[] distances = {"5 km", "10 km", "15 km", "25 km", "50 km", "100 km"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                distances
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1001
            );

            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        userLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        gMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(userLocation, 12f)
                        );

                        loadMarkersWithinDistance();
                    }
                });
    }

    private void loadMarkersWithinDistance() {

        gMap.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT name, latitude, longitude FROM items",
                null
        );

        double selectedDistanceKm = getSelectedDistance();

        while (cursor.moveToNext()) {

            String name = cursor.getString(0);
            double lat = cursor.getDouble(1);
            double lng = cursor.getDouble(2);

            float[] results = new float[1];

            Location.distanceBetween(
                    userLocation.latitude,
                    userLocation.longitude,
                    lat,
                    lng,
                    results
            );

            float distanceKm = results[0] / 1000f;

            if (distanceKm <= selectedDistanceKm) {
                LatLng position = new LatLng(lat, lng);

                gMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(name)
                );
            }
        }

        cursor.close();

        gMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("You are here"));
    }

    private double getSelectedDistance() {

        String selected = distanceSpinner.getSelectedItem().toString();

        return Double.parseDouble(selected.replace(" km", ""));
    }
}