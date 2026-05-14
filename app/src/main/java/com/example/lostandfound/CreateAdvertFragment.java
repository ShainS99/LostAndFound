package com.example.lostandfound;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.GREEN;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfound.BuildConfig;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertFragment extends Fragment {
    private RadioGroup typeRadioGroup;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descEditText;
    private EditText dateEditText;
    private Button imageButton;
    private Button saveButton;
    private Spinner categorySpinner;
    private TextView imageStatusTextView;
    private String imagePath;
    private DBHelper dbHelper;
    private AutocompleteSupportFragment autocompleteFragment;
    private String selectedLocation;
    private LatLng selectedLatLng;
    private Button getCurrentLocationButton;

    public CreateAdvertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_advert, container, false);

        categorySpinner = view.findViewById(R.id.categorySpinner);
        setupCategorySpinner();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // maps

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        }

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
        ));

        autocompleteFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {

                        selectedLocation = place.getAddress();

                        selectedLatLng = place.getLatLng();
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                    }
                });

        // previous code

        dbHelper = new DBHelper(getContext());

        imageButton = view.findViewById(R.id.imageButton);
        saveButton = view.findViewById(R.id.saveButton);
        getCurrentLocationButton = view.findViewById(R.id.currentLocationButton);

        setupListeners();

        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        nameEditText = view.findViewById(R.id.nameInputEditText);
        phoneEditText = view.findViewById(R.id.phoneInputEditText);
        descEditText = view.findViewById(R.id.descriptionInputEditText);
        dateEditText = view.findViewById(R.id.dateInputEditText);
        imageStatusTextView = view.findViewById(R.id.imageStatusTextView);
    }

    private void setupCategorySpinner() {
        String[] categories = {"Electronics", "Pets", "Wallets", "Bags", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        imageButton.setOnClickListener(v -> uploadImage());
        saveButton.setOnClickListener(v -> saveAdvert());
        getCurrentLocationButton.setOnClickListener(v -> getCurrentLocation());
    }

    private void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");

        // need these flags so i can access the image between sessions
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            // persistent permission
            requireContext().getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            imagePath = imageUri.toString();

            // update the image status text
            imageStatusTextView.setText("Img Saved");
            imageStatusTextView.setTextColor(GREEN);
        }
    }

    private void saveAdvert() {
        String postType;
        int selectedId = typeRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.lostRadioButton) {
            postType = "Lost";
        } else if (selectedId == R.id.foundRadioButton) {
            postType = "Found";
        } else {
            Toast.makeText(getContext(), "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("type", postType);
        values.put("name", nameEditText.getText().toString());
        values.put("phone", phoneEditText.getText().toString());
        values.put("description", descEditText.getText().toString());
        values.put("category", categorySpinner.getSelectedItem().toString());
        values.put("imagePath", imagePath);
        values.put("date", dateEditText.getText().toString());

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = now.format(formatter);

        values.put("createdAt", formatDateTime);

        values.put("location", selectedLocation);
        values.put("latitude", selectedLatLng.latitude);
        values.put("longitude", selectedLatLng.longitude);

        long result = db.insert("items", null, values);

        if (result == -1) {
            Log.e("DB_ERROR", "Insert failed. Values: " + values.toString());
        }

        Navigation.findNavController(getView()).navigate(R.id.homeFragment);

        Toast.makeText(getContext(), "Advert Saved!", Toast.LENGTH_SHORT).show();
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1001
            );
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        selectedLatLng = new LatLng(latitude, longitude);

                        getAddressFromLocation(latitude, longitude);
                    }
                });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        try {

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

            List<Address> addresses =
                    geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                String fullAddress =
                        address.getAddressLine(0);

                selectedLocation = fullAddress;

                autocompleteFragment.setText(selectedLocation);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}