package com.example.lostandfound;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    private TextView nameTextView, descTextView, phoneTextView, dateTextView;
    private ImageView itemImageView;
    private Button deleteButton;

    private DBHelper dbHelper;
    private int itemId;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.nameTextView);
        descTextView = view.findViewById(R.id.descTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        itemImageView = view.findViewById(R.id.itemImageView);
        deleteButton = view.findViewById(R.id.deleteButton);

        dbHelper = new DBHelper(getContext());

        if (getArguments() != null) {
            itemId = getArguments().getInt("id");
            loadItem();
        }

        deleteButton.setOnClickListener(v -> {
            deleteItem();
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void loadItem() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM items WHERE id=?",
                new String[]{String.valueOf(itemId)}
        );

        if (cursor.moveToFirst()) {

            nameTextView.setText(cursor.getString(2));
            phoneTextView.setText(cursor.getString(3));
            descTextView.setText(cursor.getString(4));
            dateTextView.setText(cursor.getString(7));

            String imagePath = cursor.getString(6);

            if (imagePath != null) {
                itemImageView.setImageURI(Uri.parse(imagePath));
            }
        }

        cursor.close();
    }

    private void deleteItem() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("items", "id=?", new String[]{String.valueOf(itemId)});
    }
}