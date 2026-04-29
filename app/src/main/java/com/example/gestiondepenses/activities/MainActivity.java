package com.example.gestiondepenses.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.adapters.ViewPagerAdapter;
import com.example.gestiondepenses.fragments.BudgetFragment;
import com.example.gestiondepenses.fragments.StatsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPagerAdapter adapter;
    private BudgetFragment budgetFragment;
    private StatsFragment statsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Dépenses"); break;
                case 1: tab.setText("Budget"); break;
                case 2: tab.setText("Stats"); break;
                case 3: tab.setText("Catégories"); break;
            }
        }).attach();

        // Récupérer les références après l'initialisation
        viewPager.post(() -> {
            budgetFragment = (BudgetFragment) adapter.getFragment(1);
            statsFragment = (StatsFragment) adapter.getFragment(2);
        });
    }

    // Méthode publique pour rafraîchir Budget et Stats
    public void refreshBudgetAndStats() {
        if (budgetFragment != null) budgetFragment.refreshData();
        if (statsFragment != null) statsFragment.refreshData();
    }
}