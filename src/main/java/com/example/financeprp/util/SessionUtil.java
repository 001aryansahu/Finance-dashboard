package com.example.financeprp.util;

import com.example.financeprp.exception.AccessDeniedException;
import com.example.financeprp.model.User;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() {
    }

    public static User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInUser(session) != null;
    }

    public static boolean hasRole(HttpSession session, String role) {
        User user = getLoggedInUser(session);
        return user != null && role.equalsIgnoreCase(user.getRole()) && user.isActive();
    }

    public static boolean isAdmin(HttpSession session) {
        return hasRole(session, "ADMIN");
    }

    public static boolean canManageRecords(HttpSession session) {
        User user = getLoggedInUser(session);
        return user != null && user.isActive()
                && ("ADMIN".equalsIgnoreCase(user.getRole()) || "ANALYST".equalsIgnoreCase(user.getRole()));
    }

    public static boolean canViewInsights(HttpSession session) {
        return canManageRecords(session);
    }

    public static void requireLogin(HttpSession session) {
        if (!isLoggedIn(session)) {
            throw new AccessDeniedException("Please sign in first.");
        }
    }

    public static void requireAdmin(HttpSession session) {
        requireLogin(session);
        if (!isAdmin(session)) {
            throw new AccessDeniedException("Only admin can perform this action.");
        }
    }

    public static void requireRecordManager(HttpSession session) {
        requireLogin(session);
        if (!canManageRecords(session)) {
            throw new AccessDeniedException("Only analyst or admin can manage records.");
        }
    }

    public static void requireInsightsAccess(HttpSession session) {
        requireLogin(session);
        if (!canViewInsights(session)) {
            throw new AccessDeniedException("Only analyst or admin can access insights.");
        }
    }
}
