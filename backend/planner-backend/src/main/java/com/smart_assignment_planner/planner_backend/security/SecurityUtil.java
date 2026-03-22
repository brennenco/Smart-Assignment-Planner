package com.smart_assignment_planner.planner_backend.security;

import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static UserPrincipal requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal up)) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        return up;
    }

    public static User currentUserEntity() {
        return requireCurrentUser().getUser();
    }

    public static Integer currentUserId() {
        return requireCurrentUser().getUserId();
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}
