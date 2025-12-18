package com.reclaim.app.auth;

import com.reclaim.app.auth.dto.*;
import com.reclaim.app.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public User register(RegisterRequest req) {
        String email = req.email.trim().toLowerCase();
        if (users.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered");
        User u = new User(req.name.trim(), email, encoder.encode(req.password));
        return users.save(u);
    }

    /**
     * @deprecated Separate admin registration is abolished.
     *             The only admin is admin@reclaim.app.
     */
    @Deprecated
    public User adminRegister(AdminRegisterRequest req) {
        throw new UnsupportedOperationException(
                "Direct admin registration is strictly prohibited. Access is restricted to the system administrator.");
    }

    public User login(LoginRequest req) {
        String email = req.email.trim().toLowerCase();
        User u = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!encoder.matches(req.password, u.getPasswordHash()))
            throw new IllegalArgumentException("Invalid email or password");
        if (u.isBlocked())
            throw new IllegalArgumentException("Your account has been blocked. Please contact support.");
        return u;
    }
}
