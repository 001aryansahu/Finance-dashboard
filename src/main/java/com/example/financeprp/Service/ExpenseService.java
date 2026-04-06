package com.example.financeprp.Service;

import com.example.financeprp.Repository.ExpensesRepository;
import com.example.financeprp.exception.ResourceNotFoundException;
import com.example.financeprp.model.Expenses;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpensesRepository expenseRepository;

    public ExpenseService(ExpensesRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expenses saveExpense(Expenses expense) {
        normalizeExpense(expense);
        return expenseRepository.save(expense);
    }

    public List<Expenses> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Page<Expenses> getFilteredExpenses(String category, String type, LocalDate date, Pageable pageable) {
        return expenseRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("type")), type.trim().toUpperCase()));
            }
            if (date != null) {
                predicates.add(cb.equal(root.get("date"), date));
            }

            query.orderBy(cb.desc(root.get("date")), cb.desc(root.get("id")));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public Optional<Expenses> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public Expenses getRequiredExpense(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found."));
    }

    public void deleteExpense(Long id) {
        Expenses expense = getRequiredExpense(id);
        expenseRepository.delete(expense);
    }

    public Expenses updateExpense(Expenses expense) {
        normalizeExpense(expense);
        return expenseRepository.save(expense);
    }

    private void normalizeExpense(Expenses expense) {
        expense.setCategory(expense.getCategory() == null ? null : expense.getCategory().trim());
        expense.setType(expense.getType() == null ? null : expense.getType().trim().toUpperCase());
        expense.setNotes(expense.getNotes() == null ? null : expense.getNotes().trim());
    }
}
