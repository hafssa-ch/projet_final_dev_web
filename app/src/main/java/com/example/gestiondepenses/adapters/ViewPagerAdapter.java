package com.example.gestiondepenses.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.gestiondepenses.fragments.BudgetFragment;
import com.example.gestiondepenses.fragments.CategoriesFragment;
import com.example.gestiondepenses.fragments.ExpensesFragment;
import com.example.gestiondepenses.fragments.StatsFragment;
import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0: fragment = new ExpensesFragment(); break;
            case 1: fragment = new BudgetFragment(); break;
            case 2: fragment = new StatsFragment(); break;
            case 3: fragment = new CategoriesFragment(); break;
            default: fragment = new ExpensesFragment(); break;
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() { return 4; }

    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }
}