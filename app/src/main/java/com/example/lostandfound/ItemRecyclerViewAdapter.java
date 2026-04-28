package com.example.lostandfound;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private List<Item> list;

    public ItemRecyclerViewAdapter(List<Item> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, name, category, date;

        public ViewHolder(@NonNull View view) {
            super(view);
            type = view.findViewById(R.id.rowTypeTextView);
            name = view.findViewById(R.id.rowNameTextView);
            category = view.findViewById(R.id.rowCategoryTextView);
            date = view.findViewById(R.id.rowDateTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = list.get(position);

        holder.type.setText(item.type);
        holder.name.setText(item.name);
        holder.category.setText(item.category);
        holder.date.setText(item.date);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", item.id);

            Navigation.findNavController(v)
                    .navigate(R.id.detailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}