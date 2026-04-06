package com.example.financeprp.controller;

import com.example.financeprp.exception.AccessDeniedException;
import com.example.financeprp.exception.InvalidOperationException;
import com.example.financeprp.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     RedirectAttributes redirectAttributes) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        if (request.getRequestURI().startsWith("/signin") || request.getSession(false) == null) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/signin";
        }
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        if (request.getRequestURI().startsWith("/users")) {
            return "redirect:/dashboard";
        }
        return "redirect:/expenses";
    }

    @ExceptionHandler(InvalidOperationException.class)
    public String handleInvalidOperation(InvalidOperationException ex,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         RedirectAttributes redirectAttributes) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        if (request.getRequestURI().startsWith("/users")) {
            return "redirect:/dashboard";
        }
        return "redirect:/expenses";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         Model model) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("status", response.getStatus());
        model.addAttribute("error", "Unexpected Error");
        model.addAttribute("message", ex.getMessage() == null ? "Something went wrong." : ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }
}
