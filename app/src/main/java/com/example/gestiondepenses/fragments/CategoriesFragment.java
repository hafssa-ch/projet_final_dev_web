package com.example.gestiondepenses.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.adapters.CategoryAdapter;
import com.example.gestiondepenses.models.Category;
import com.example.gestiondepenses.network.ApiClient;
import com.example.gestiondepenses.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {
    private static final String TAG = "CategoriesFragment";
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private ApiService apiService;
    private EditText etNewCategoryName;
    private Button btnAddCategory;
    private Button btnPickColor;                  // ← déclaration ajoutée
    private String selectedColor = "#6200EE";     // ← variable ajoutée

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        apiService = ApiClient.getApiService();

        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onDelete(Category category) { deleteCategory(category); }
            @Override
            public void onUpdate(Category category, String newName, String newColor) { updateCategory(category, newName, newColor); }
        });
        rvCategories.setAdapter(adapter);

        etNewCategoryName = view.findViewById(R.id.etNewCategoryName);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        btnPickColor = view.findViewById(R.id.btnPickColor);   // ← initialisation
        if (btnPickColor != null) {
            btnPickColor.setOnClickListener(v -> showColorPicker());
            btnPickColor.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(selectedColor)));
        }

        btnAddCategory.setOnClickListener(v -> addCategory());

        loadCategories();
        return view;
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                Log.d(TAG, "Réponse reçue, code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Nombre de catégories = " + response.body().size());
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    // Forcer l'affichage
                    if (adapter != null) {

                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Nombre d'éléments dans l'adaptateur : " + adapter.getItemCount());
                        rvCategories.post(() -> rvCategories.requestLayout());
                        rvCategories.invalidate();
                    }
                } else {
                    Log.e(TAG, "Erreur ou body null");
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Erreur réseau", t);
            }
        });
    }

    private void addCategory() {
        String name = etNewCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nom requis", Toast.LENGTH_SHORT).show();
            return;
        }
        Category cat = new Category();
        cat.setName(name);
        cat.setColor(selectedColor);              // ← utilise la couleur choisie
        apiService.createCategory(cat).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Catégorie ajoutée", Toast.LENGTH_SHORT).show();
                    etNewCategoryName.setText("");
                    loadCategories();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Erreur", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                if (isAdded()) Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Supprimer")
                .setMessage("Supprimer " + category.getName() + " ?")
                .setPositiveButton("Oui", (d, which) -> {
                    apiService.deleteCategory(category.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (isAdded() && response.isSuccessful()) {
                                loadCategories();
                            } else if (isAdded()) {
                                Toast.makeText(requireContext(), "Impossible, catégorie utilisée", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            if (isAdded()) Toast.makeText(requireContext(), "Erreur suppression", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void updateCategory(Category category, String newName, String newColor) {
        category.setName(newName);
        category.setColor(newColor);
        apiService.updateCategory(category.getId(), category).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Catégorie modifiée", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Erreur modification", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (isAdded()) Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showColorPicker() {
        final String[] colors = {
                "#FF5252", "#FF4081", "#E040FB", "#536DFE", "#448AFF", "#00E5FF",
                "#00BFA5", "#69F0AE", "#FFEB3B", "#FFC107", "#FF9800", "#FF6E40",
                "#6200EE", "#03DAC5", "#B388FF", "#8D6E63", "#F48FB1", "#212121"
        };

        LinearLayout mainLayout = new LinearLayout(requireContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        // Aperçu
        final View previewView = new View(requireContext());
        previewView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        previewView.setBackgroundColor(Color.parseColor(selectedColor));
        previewView.setPadding(8, 8, 8, 8);
        mainLayout.addView(previewView);

        // Espacement
        Space spacer = new Space(requireContext());
        spacer.setMinimumHeight(24);
        mainLayout.addView(spacer);

        // Grille
        GridLayout gridLayout = new GridLayout(requireContext());
        gridLayout.setColumnCount(3);
        for (final String color : colors) {
            Button colorButton = new Button(requireContext());
            colorButton.setBackgroundColor(Color.parseColor(color));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 120;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            colorButton.setLayoutParams(params);
            colorButton.setOnClickListener(v -> {
                selectedColor = color;
                previewView.setBackgroundColor(Color.parseColor(selectedColor));
                if (btnPickColor != null) {
                    btnPickColor.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(selectedColor)));
                }
            });
            gridLayout.addView(colorButton);
        }
        mainLayout.addView(gridLayout);

        new AlertDialog.Builder(requireContext())
                .setTitle("Choisissez une couleur")
                .setView(mainLayout)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}