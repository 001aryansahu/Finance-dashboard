package com.example.financeprp.Repository;

import com.example.financeprp.model.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {

    @Query("SELECT SUM(e.amount) FROM Expenses e WHERE e.type='INCOME'")
    Double getTotalIncome();

    @Query("SELECT SUM(e.amount) FROM Expenses e WHERE e.type='EXPENSE'")
    Double getTotalExpense();

    @Query("SELECT e.category, SUM(e.amount) FROM Expenses e GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategorySummary();

    @Query("SELECT e FROM Expenses e ORDER BY e.date DESC, e.id DESC")
    List<Expenses> getRecentTransactions();

    @Query("SELECT MONTH(e.date), SUM(e.amount) FROM Expenses e GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)")
    List<Object[]> getMonthlySummary();
}
