package com.reclaim.app;

import com.reclaim.app.user.Role;
import com.reclaim.app.user.User;
import com.reclaim.app.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Starting Database Seeding & Enforcement ---");

        // 1. Enforce ONLY ONE Admin: admin@reclaim.app
        String adminEmail = "admin@reclaim.app";
        User admin = users.findByEmail(adminEmail).orElseGet(() -> {
            User u = new User();
            u.setEmail(adminEmail);
            u.setName("System Admin");
            return u;
        });

        admin.setPasswordHash(encoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        users.save(admin);

        // 2. Safeguard: Downgrade any other accounts that might have ROLE_ADMIN
        users.findAll().forEach(u -> {
            if (u.getRole() == Role.ADMIN && !u.getEmail().equalsIgnoreCase(adminEmail)) {
                System.out.println("Enforcement: Downgrading unauthorized admin account: " + u.getEmail());
                u.setRole(Role.USER);
                users.save(u);
            }
        });

        System.out.println("--- DB ENFORCEMENT COMPLETE (Single Admin: " + adminEmail + ") ---");
    }
}
