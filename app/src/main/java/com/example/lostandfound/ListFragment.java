package com.example.lostandfound;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private Spinner filterSpinner;
    private DBHelper dbHelper;
    private RecyclerView itemRecyclerView;
    private String[] categories = {"All", "Electronics", "Pets", "Wallets", "Bags", "Other"};

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterSpinner = view.findViewById(R.id.filterSpinner);
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper(getContext());

        setupFilterSpinner();

        List<Item> items = getItems("All");
        itemRecyclerView.setAdapter(new ItemRecyclerViewAdapter(items));
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Item> items = getItems("All");
        itemRecyclerView.setAdapter(new ItemRecyclerViewAdapter(items));
    }

    private void setupFilterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Item> items = getItems(categories[position]);
                itemRecyclerView.setAdapter(new ItemRecyclerViewAdapter(items));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public List<Item> getItems(String categoryFilter) {
        List<Item> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        if (categoryFilter.equals("All")) {
            cursor = db.rawQuery(
                    "SELECT * FROM items ORDER BY createdAt DESC",
                    null);
        } else {
            cursor = db.rawQuery(
                    "SELECT * FROM items WHERE category=? ORDER BY createdAt DESC",
                    new String[]{categoryFilter});
        }

        while (cursor.moveToNext()) {
            list.add(new Item(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(5),
                    cursor.getString(7),
                    cursor.getString(6)
            ));
        }

        return list;
    }
}