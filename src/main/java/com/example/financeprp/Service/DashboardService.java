package com.example.financeprp.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

import org.springframework.stereotype.Service;

import com.example.financeprp.Repository.ExpensesRepository;
import com.example.financeprp.model.Expenses;

@Service
public class DashboardService {

    private final ExpensesRepository repo;

    public DashboardService(ExpensesRepository repo) {
        this.repo = repo;
    }

    public Map<String, Object> getDashboardData() {
        Double income = Optional.ofNullable(repo.getTotalIncome()).orElse(0.0);
        Double expense = Optional.ofNullable(repo.getTotalExpense()).orElse(0.0);
        Double balance = income - expense;

        List<Object[]> categorySummary = repo.getCategorySummary();
        List<Expenses> recentTransactions = repo.getRecentTransactions();
        List<Object[]> monthlySummary = repo.getMonthlySummary();

        Map<String, Object> map = new HashMap<>();
        map.put("income", income);
        map.put("expense", expense);
        map.put("balance", balance);
        map.put("category", categorySummary);
        map.put("recent", recentTransactions.stream().limit(5).toList());
        map.put("monthly", monthlySummary);
        map.put("topCategory", extractTopCategory(categorySummary));
        map.put("totalRecords", recentTransactions.size());
        map.put("currentMonthExpense", calculateCurrentMonthExpense(recentTransactions));
        map.put("avgTransaction", recentTransactions.isEmpty() ? 0.0 : roundTwoDecimals((income + expense) / recentTransactions.size()));
        map.put("monthlyLabels", buildMonthlyLabels(monthlySummary));
        map.put("monthlyTotals", buildMonthlyTotals(monthlySummary));
        return map;
    }

    private String extractTopCategory(List<Object[]> categorySummary) {
        if (categorySummary == null || categorySummary.isEmpty()) {
            return "N/A";
        }
        Object category = categorySummary.get(0)[0];
        return category == null ? "N/A" : String.valueOf(category);
    }

    private double calculateCurrentMonthExpense(List<Expenses> recentTransactions) {
        YearMonth currentMonth = YearMonth.now();
        return roundTwoDecimals(recentTransactions.stream()
                .filter(tx -> tx.getDate() != null && YearMonth.from(tx.getDate()).equals(currentMonth))
                .filter(tx -> "EXPENSE".equalsIgnoreCase(tx.getType()))
                .mapToDouble(tx -> tx.getAmount() == null ? 0.0 : tx.getAmount())
                .sum());
    }

    private List<String> buildMonthlyLabels(List<Object[]> monthlySummary) {
        List<String> labels = new ArrayList<>();
        for (Object[] item : monthlySummary) {
            int month = ((Number) item[0]).intValue();
            labels.add(LocalDate.of(LocalDate.now().getYear(), month, 1)
                    .getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return labels;
    }

    private List<Double> buildMonthlyTotals(List<Object[]> monthlySummary) {
        List<Double> totals = new ArrayList<>();
        for (Object[] item : monthlySummary) {
            totals.add(roundTwoDecimals(((Number) item[1]).doubleValue()));
        }
        return totals;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
