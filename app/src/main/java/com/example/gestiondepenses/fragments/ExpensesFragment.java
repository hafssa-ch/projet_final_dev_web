package com.example.gestiondepenses.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.activities.MainActivity;
import com.example.gestiondepenses.adapters.ExpenseAdapter;
import com.example.gestiondepenses.models.Category;
import com.example.gestiondepenses.models.Expense;
import com.example.gestiondepenses.network.ApiClient;
import com.example.gestiondepenses.network.ApiService;
import com.example.gestiondepenses.network.ExpenseRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private ApiService apiService;
    private List<Expense> expenseList = new ArrayList<>();
    private Spinner spinnerCategory;
    private EditText etStartDate, etEndDate;
    private Button btnFilter;
    private int selectedCategoryId = 0;
    private List<Category> categoriesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        apiService = ApiClient.getApiService();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(expenseList, this::onDeleteExpense, this::onEditExpense);
        recyclerView.setAdapter(adapter);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        btnFilter = view.findViewById(R.id.btnFilter);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        loadCategories();
        loadExpenses();

        btnFilter.setOnClickListener(v -> loadExpenses());
        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        return view;
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.clear();
                    categoriesList.addAll(response.body());
                    // Mise à jour du spinner de filtre
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategoryId = categoriesList.get(position).getId();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { selectedCategoryId = 0; }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur chargement catégories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenses() {
        String start = etStartDate.getText().toString();
        String end = etEndDate.getText().toString();
        Integer catId = selectedCategoryId == 0 ? null : selectedCategoryId;
        apiService.getExpenses(start.isEmpty() ? null : start, end.isEmpty() ? null : end, catId)
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            expenseList.clear();
                            expenseList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {
                        Toast.makeText(requireContext(), "Erreur chargement dépenses", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Rafraîchir le budget et les statistiques (appel à MainActivity)
    private void refreshBudgetAndStats() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).refreshBudgetAndStats();
        }
    }

    private void showAddEditDialog(Expense expense) {
        // Recharger les catégories à jour avant d'ouvrir le dialogue
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showDialogWithCategories(expense, response.body());
                } else {
                    Toast.makeText(requireContext(), "Impossible de charger les catégories", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogWithCategories(Expense expense, List<Category> categories) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_expense, null);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etNote = dialogView.findViewById(R.id.etNote);
        Spinner spinnerCat = dialogView.findViewById(R.id.spinnerCategory);

        ArrayAdapter<Category> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(catAdapter);

        if (expense != null) {
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDate.setText(expense.getDate());
            etNote.setText(expense.getNote());
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == expense.getCategory_id()) {
                    spinnerCat.setSelection(i);
                    break;
                }
            }
        } else {
            etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        }

        builder.setView(dialogView)
                .setTitle(expense == null ? "Ajouter dépense" : "Modifier dépense")
                .setPositiveButton("Enregistrer", null)
                .setNegativeButton("Annuler", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            posButton.setOnClickListener(v -> {
                String amountStr = etAmount.getText().toString();
                if (amountStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Montant requis", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(requireContext(), "Montant > 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                String date = etDate.getText().toString();
                String note = etNote.getText().toString();
                Category selected = (Category) spinnerCat.getSelectedItem();
                int catId = selected.getId();

                ExpenseRequest request = new ExpenseRequest(amount, date, note, catId);
                Call<Void> call;
                if (expense == null) {
                    call = apiService.createExpense(request);
                } else {
                    call = apiService.updateExpense(expense.getId(), request);
                }
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            loadExpenses();               // recharge la liste des dépenses
                            refreshBudgetAndStats();       // rafraîchit budget et stats
                            dialog.dismiss();
                            // Optionnel : recharger les catégories si nécessaire
                            loadCategories();
                        } else {
                            Toast.makeText(requireContext(), "Erreur", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        dialog.show();
    }

    private void onDeleteExpense(Expense expense) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Supprimer")
                .setMessage("Supprimer cette dépense ?")
                .setPositiveButton("Oui", (d, which) -> {
                    apiService.deleteExpense(expense.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            loadExpenses();
                            refreshBudgetAndStats();    // rafraîchit budget et stats après suppression
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void onEditExpense(Expense expense) {
        showAddEditDialog(expense);
    }
}