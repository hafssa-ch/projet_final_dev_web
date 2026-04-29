package com.example.gestiondepenses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.models.Expense;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenses;
    private OnItemClickListener deleteListener;
    private OnItemClickListener editListener;

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenses, OnItemClickListener deleteListener, OnItemClickListener editListener) {
        this.expenses = expenses;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense e = expenses.get(position);
        holder.tvAmount.setText(String.format("%.2f €", e.getAmount()));
        holder.tvCategory.setText(e.getCategory_name());
        holder.tvDate.setText(e.getDate());
        holder.tvNote.setText(e.getNote());
        holder.btnEdit.setOnClickListener(v -> editListener.onItemClick(e));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onItemClick(e));
    }

    @Override
    public int getItemCount() { return expenses.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCategory, tvDate, tvNote;
        View btnEdit, btnDelete;
        ViewHolder(View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}