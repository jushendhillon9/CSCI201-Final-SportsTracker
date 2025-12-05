package com.example.cfbtracker.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

    private String email;
    private String display_name;
    private String google_id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime created_at;

    public enum Role {
        guest, user, premium, admin
    }

    // getters and setters
}
