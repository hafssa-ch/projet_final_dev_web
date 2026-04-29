package com.example.gestiondepenses.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.models.CategoryStat;
import com.example.gestiondepenses.models.MonthlyStat;
import com.example.gestiondepenses.network.ApiClient;
import com.example.gestiondepenses.network.ApiService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {
    private PieChart pieChart;
    private BarChart barChart;
    private ApiService apiService;
    private String currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        apiService = ApiClient.getApiService();
        currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        refreshData();
        return view;
    }

    public void refreshData() {
        loadCategoryStats();
        loadMonthlyStats();
    }

    private void loadCategoryStats() {
        apiService.getCategoryStats(currentMonth).enqueue(new Callback<List<CategoryStat>>() {
            @Override
            public void onResponse(Call<List<CategoryStat>> call, Response<List<CategoryStat>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<PieEntry> entries = new ArrayList<>();
                    for (CategoryStat stat : response.body()) {
                        entries.add(new PieEntry((float) stat.getTotal(), stat.getName()));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "Dépenses par catégorie");
                    dataSet.setColors(new int[]{Color.parseColor("#0f4c5c"), Color.parseColor("#2c6e7e"), Color.parseColor("#b8e1e9"), Color.parseColor("#ffb6c1")});
                    pieChart.setData(new PieData(dataSet));
                    pieChart.invalidate();
                } else if (isAdded()) {
                    pieChart.setNoDataText("Aucune dépense ce mois-ci");
                    pieChart.invalidate();
                }
            }
            @Override public void onFailure(Call<List<CategoryStat>> call, Throwable t) {}
        });
    }

    private void loadMonthlyStats() {
        apiService.getMonthlyStats().enqueue(new Callback<List<MonthlyStat>>() {
            @Override
            public void onResponse(Call<List<MonthlyStat>> call, Response<List<MonthlyStat>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<BarEntry> entries = new ArrayList<>();
                    List<String> months = new ArrayList<>();
                    for (int i = 0; i < response.body().size(); i++) {
                        MonthlyStat stat = response.body().get(i);
                        entries.add(new BarEntry(i, (float) stat.getTotal()));
                        months.add(stat.getMonth());
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Dépenses mensuelles");
                    dataSet.setColor(Color.parseColor("#006D77"));
                    barChart.setData(new BarData(dataSet));
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                    xAxis.setGranularity(1f);
                    barChart.invalidate();
                } else if (isAdded()) {
                    barChart.setNoDataText("Aucune donnée mensuelle");
                    barChart.invalidate();
                }
            }
            @Override public void onFailure(Call<List<MonthlyStat>> call, Throwable t) {}
        });
    }
}