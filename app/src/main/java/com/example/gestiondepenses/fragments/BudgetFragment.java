package com.example.gestiondepenses.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.network.ApiClient;
import com.example.gestiondepenses.network.ApiService;
import com.example.gestiondepenses.network.BudgetRequest;
import com.example.gestiondepenses.network.BudgetResponse;
import com.example.gestiondepenses.network.SpendingResponse;
import com.example.gestiondepenses.utils.NotificationHelper;
import java.text.SimpleDateFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment {
    private static final String TAG = "BudgetFragment";
    private TextView tvBudget, tvSpent, tvRemaining;
    private ProgressBar progressBar;
    private Button btnSetBudget;
    private ApiService apiService;
    private String currentMonth;
    private double currentBudget = 0;
    private double currentSpent = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        apiService = ApiClient.getApiService();

        tvBudget = view.findViewById(R.id.tvBudget);
        tvSpent = view.findViewById(R.id.tvSpent);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        progressBar = view.findViewById(R.id.progressBar);
        btnSetBudget = view.findViewById(R.id.btnSetBudget);

        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new java.util.Date());

        refreshData();

        btnSetBudget.setOnClickListener(v -> showSetBudgetDialog());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData(); // Force la mise à jour dès que l'utilisateur voit ce fragment
    }
    public void refreshData() {
        apiService.getBudget(currentMonth).enqueue(new Callback<BudgetResponse>() {
            @Override
            public void onResponse(Call<BudgetResponse> call, Response<BudgetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBudget = response.body().getAmount();
                    Log.d("BudgetDebug", "Budget reçu: " + currentBudget);
                } else {
                    Log.e("BudgetDebug", "Erreur getBudget: " + response.code());
                }
                fetchCurrentSpending();
            }

            private void fetchCurrentSpending() {
            }

            @Override
            public void onFailure(Call<BudgetResponse> call, Throwable t) {
                Log.e(TAG, "Échec getBudget", t);
                currentBudget = 0;
                updateUI();
            }
        });

        // Récupérer les dépenses du mois
        apiService.getCurrentSpending().enqueue(new Callback<SpendingResponse>() {
            @Override
            public void onResponse(Call<SpendingResponse> call, Response<SpendingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentSpent = response.body().getSpent();
                    Log.d("BudgetDebug", "Dépenses reçues: " + currentSpent);
                } else {
                    Log.e("BudgetDebug", "Erreur getCurrentSpending: " + response.code());
                }
                updateUI();
            }
            @Override
            public void onFailure(Call<SpendingResponse> call, Throwable t) {
                Log.e(TAG, "Échec getCurrentSpending", t);
                currentSpent = 0;
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (!isAdded()) return;
        tvBudget.setText(String.format(Locale.getDefault(), "%,.2f €", currentBudget));
        tvSpent.setText(String.format(Locale.getDefault(), "%,.2f €", currentSpent));
        double remaining = currentBudget - currentSpent;
        tvRemaining.setText(String.format(Locale.getDefault(), "%,.2f €", remaining));

        if (currentBudget > 0) {
            int percent = (int) ((currentSpent / currentBudget) * 100);
            progressBar.setProgress(Math.min(percent, 100));
            if (percent > 100) {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_bar_red));
            } else {
                progressBar.setProgressDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_bar_green));
            }
        } else {
            progressBar.setProgress(0);
        }

        if (remaining < 0) {
            tvRemaining.setTextColor(Color.RED);
            NotificationHelper.showNotification(requireContext(), "Dépassement de budget",
                    String.format(Locale.getDefault(), "Vous avez dépassé votre budget de %.2f €", -remaining));
        } else {
            tvRemaining.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary));
        }
    }

    private void showSetBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_budget, null);
        EditText etAmount = dialogView.findViewById(R.id.etBudgetAmount);
        etAmount.setText(String.valueOf(currentBudget));
        builder.setView(dialogView)
                .setTitle("Définir budget mensuel")
                .setPositiveButton("Enregistrer", null)
                .setNegativeButton("Annuler", null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            posButton.setOnClickListener(v -> {
                String amountStr = etAmount.getText().toString();
                if (amountStr.isEmpty()) {
                    Toast.makeText(getContext(), "Veuillez saisir un montant", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    Toast.makeText(getContext(), "Montant invalide", Toast.LENGTH_SHORT).show();
                    return;
                }
                apiService.setBudget(new BudgetRequest(currentMonth, amount)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Budget enregistré", Toast.LENGTH_SHORT).show();
                            refreshData();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        dialog.show();
    }
}