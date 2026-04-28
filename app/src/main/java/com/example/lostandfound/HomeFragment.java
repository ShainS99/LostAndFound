package com.example.lostandfound;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    private Button createAdvertButton;
    private Button showItemsButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createAdvertButton = view.findViewById(R.id.createAdvertButton);
        showItemsButton = view.findViewById(R.id.showItemsButton);

        createAdvertButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.createAdvertFragment)
        );

        showItemsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.listFragment)
        );

        return view;
    }
}