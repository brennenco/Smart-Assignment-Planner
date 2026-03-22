package com.smart_assignment_planner.planner_backend.controller;

import com.smart_assignment_planner.planner_backend.dto.LoginRequest;
import com.smart_assignment_planner.planner_backend.dto.RegisterRequest;
import com.smart_assignment_planner.planner_backend.dto.UserResponse;
import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.security.SecurityUtil;
import com.smart_assignment_planner.planner_backend.security.UserPrincipal;
import com.smart_assignment_planner.planner_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    /** Required so the session actually stores the login (otherwise /api/auth/me returns 401 after redirect). */
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            httpRequest.getSession(true);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);
            UserPrincipal up = (UserPrincipal) auth.getPrincipal();
            return ResponseEntity.ok(UserResponse.from(up.getUser()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            User user = userService.register(request.name(), request.email(), request.password());
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            httpRequest.getSession(true);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    public UserResponse me() {
        return UserResponse.from(SecurityUtil.currentUserEntity());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, auth);
        return ResponseEntity.noContent().build();
    }
}
