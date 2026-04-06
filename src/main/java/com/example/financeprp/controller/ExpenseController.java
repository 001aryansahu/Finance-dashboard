package com.example.financeprp.controller;

import com.example.financeprp.Service.ExpenseService;
import com.example.financeprp.model.Expenses;
import com.example.financeprp.model.User;
import com.example.financeprp.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String listExpenses(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                               Model model,
                               HttpSession session) {
        SessionUtil.requireLogin(session);

        User sessionUser = SessionUtil.getLoggedInUser(session);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 20), Sort.by(Sort.Direction.DESC, "date", "id"));
        Page<Expenses> expensePage = expenseService.getFilteredExpenses(category, type, date, pageable);

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("canManageRecords", SessionUtil.canManageRecords(session));
        model.addAttribute("isAdmin", SessionUtil.isAdmin(session));
        model.addAttribute("expensePage", expensePage);
        model.addAttribute("expenses", expensePage.getContent());
        model.addAttribute("currentPage", expensePage.getNumber());
        model.addAttribute("totalPages", expensePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("category", category);
        model.addAttribute("type", type);
        model.addAttribute("date", date);
        return "expenses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        SessionUtil.requireRecordManager(session);
        model.addAttribute("expense", new Expenses());
        model.addAttribute("sessionUser", SessionUtil.getLoggedInUser(session));
        return "expenses/create";
    }

    @PostMapping
    public String saveExpense(@Valid @ModelAttribute("expense") Expenses expense,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        SessionUtil.requireRecordManager(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("sessionUser", SessionUtil.getLoggedInUser(session));
            return "expenses/create";
        }
        expenseService.saveExpense(expense);
        redirectAttributes.addFlashAttribute("success", "Record added successfully.");
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               HttpSession session) {
        SessionUtil.requireRecordManager(session);
        Expenses expense = expenseService.getRequiredExpense(id);
        model.addAttribute("expense", expense);
        model.addAttribute("sessionUser", SessionUtil.getLoggedInUser(session));
        return "expenses/edit";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable Long id,
                                @Valid @ModelAttribute("expense") Expenses expense,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        SessionUtil.requireRecordManager(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("sessionUser", SessionUtil.getLoggedInUser(session));
            return "expenses/edit";
        }
        expense.setId(id);
        expenseService.getRequiredExpense(id);
        expenseService.updateExpense(expense);
        redirectAttributes.addFlashAttribute("success", "Record updated successfully.");
        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        SessionUtil.requireAdmin(session);
        expenseService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("success", "Record deleted successfully.");
        return "redirect:/expenses";
    }
}
