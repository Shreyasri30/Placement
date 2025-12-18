package com.reclaim.app.auth;

import com.reclaim.app.auth.dto.*;
import com.reclaim.app.user.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.core.context.SecurityContext;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final AuthenticationManager authenticationManager;
    private final com.reclaim.app.user.UserRepository users;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(AuthService auth, AuthenticationManager authenticationManager,
            com.reclaim.app.user.UserRepository users, SecurityContextRepository securityContextRepository) {
        this.auth = auth;
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = auth.register(req);
        return ResponseEntity.ok(Map.of("message", "Registered successfully", "userId", u.getId()));
    }

    @PostMapping("/admin-register")
    public ResponseEntity<?> adminRegister(@Valid @RequestBody AdminRegisterRequest req) {
        User u = auth.adminRegister(req);
        return ResponseEntity.ok(Map.of("message", "Admin registered successfully", "userId", u.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email.trim().toLowerCase(), req.password));

        // In Spring Security 6, with requireExplicitSave(false) in config,
        // this is sufficient as the filter will save it at the end of the request.
        SecurityContextHolder.getContext().setAuthentication(authResult);

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        com.reclaim.app.user.User u = users.findByEmail(userDetails.getUsername()).orElseThrow();

        // Required session attributes
        HttpSession session = request.getSession(true);
        session.setAttribute("USER_ID", u.getId());
        session.setAttribute("USER_NAME", u.getName());
        session.setAttribute("USER_ROLE", u.getRole().name());

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "name", u.getName(),
                "role", u.getRole().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("message", "Not logged in"));
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        com.reclaim.app.user.User u = users.findByEmail(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "userId", u.getId(),
                "name", u.getName(),
                "role", u.getRole().name()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> bad(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> global(Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Unknown error"));
    }
}
