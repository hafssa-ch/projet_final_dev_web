package com.example.gestiondepenses.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onDelete(Category category);
        void onUpdate(Category category, String newName, String newColor);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryActionListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvName.setText(category.getName() != null ? category.getName() : "");
        // Appliquer la couleur
        try {
            String colorStr = category.getColor();
            if (colorStr != null && !colorStr.isEmpty()) {
                int color = Color.parseColor(colorStr);
                holder.viewColor.setBackgroundColor(color);
            } else {
                holder.viewColor.setBackgroundColor(Color.parseColor("#6200EE"));
            }
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#6200EE"));
        }
        holder.btnEdit.setOnClickListener(v -> showEditDialog(category, holder.itemView.getContext()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(category));
    }

    private void showEditDialog(Category category, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null);
        EditText etName = view.findViewById(R.id.etEditCategoryName);
        EditText etColor = view.findViewById(R.id.etEditCategoryColor);
        etName.setText(category.getName());
        etColor.setText(category.getColor() != null ? category.getColor() : "#6200EE");
        builder.setTitle("Modifier catégorie")
                .setView(view)
                .setPositiveButton("Enregistrer", (d, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newColor = etColor.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        listener.onUpdate(category, newName, newColor);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    // Méthode pour mettre à jour la liste (optionnel)
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvName;
        ImageButton btnEdit, btnDelete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColor);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}