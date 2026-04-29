package com.example.gestiondepenses.network;

import com.example.gestiondepenses.models.CategoryStat;
import com.example.gestiondepenses.models.Expense;
import com.example.gestiondepenses.models.Category;
import com.example.gestiondepenses.models.MonthlyStat;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("expenses")
    Call<List<Expense>> getExpenses(@Query("startDate") String startDate,
                                    @Query("endDate") String endDate,
                                    @Query("categoryId") Integer categoryId);

    @POST("expenses")
    Call<Void> createExpense(@Body ExpenseRequest expense);

    @PUT("expenses/{id}")
    Call<Void> updateExpense(@Path("id") int id, @Body ExpenseRequest expense);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") int id);

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("budgets/current-spending")
    Call<SpendingResponse> getCurrentSpending();

    @POST("budgets")
    Call<Void> setBudget(@Body BudgetRequest budget);

    @GET("budgets/{month_year}")
    Call<BudgetResponse> getBudget(@Path("month_year") String monthYear);

    @GET("stats/category-breakdown")
    Call<List<CategoryStat>> getCategoryStats(@Query("month_year") String monthYear);

    @POST("categories")
    Call<Void> createCategory(@Query("name") String name, @Query("color") String color);

    @PUT("categories/{id}")
    Call<Void> updateCategory(@Path("id") int id, @Query("name") String name, @Query("color") String color);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);
    @GET("stats/monthly")
    Call<List<MonthlyStat>> getMonthlyStats();


    @PUT("categories/{id}")
    Call<Void> updateCategory(@Path("id") int id, @Body Category category);
    @POST("categories")
    Call<Category> createCategory(@Body Category category);
}