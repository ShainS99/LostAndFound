package com.example.lostandfound;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.GREEN;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CreateAdvertFragment extends Fragment {
    private RadioGroup typeRadioGroup;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descEditText;
    private EditText dateEditText;
    private EditText locationEditText;
    private Button imageButton;
    private Button saveButton;
    private Spinner categorySpinner;
    private TextView imageStatusTextView;
    private String imagePath;
    private DBHelper dbHelper;

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

        dbHelper = new DBHelper(getContext());

        imageButton = view.findViewById(R.id.imageButton);
        saveButton = view.findViewById(R.id.saveButton);

        setupListeners();

        typeRadioGroup = view.findViewById(R.id.typeRadioGroup);
        nameEditText = view.findViewById(R.id.nameInputEditText);
        phoneEditText = view.findViewById(R.id.phoneInputEditText);
        descEditText = view.findViewById(R.id.descriptionInputEditText);
        dateEditText = view.findViewById(R.id.dateInputEditText);
        locationEditText = view.findViewById(R.id.locationInputEditText);
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
        values.put("location", locationEditText.getText().toString());

        long result = db.insert("items", null, values);

        if (result == -1) {
            Log.e("DB_ERROR", "Insert failed. Values: " + values.toString());
        }

        Navigation.findNavController(getView()).navigate(R.id.homeFragment);

        Toast.makeText(getContext(), "Advert Saved!", Toast.LENGTH_SHORT).show();
    }
}