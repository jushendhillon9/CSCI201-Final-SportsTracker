package com.sportstracker.backend.repository;

import com.sportstracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity
 * Provides database operations for Users table
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Find user by email address
     * Used for login and authentication
     * 
     * @param email User's email address
     * @return User object if found, null otherwise
     */
    User findByEmail(String email);
    
    /**
     * Find all users with a specific role
     * 
     * @param role User role (e.g., "guest", "user", "admin")
     * @return List of users with the specified role
     */
    java.util.List<User> findByRole(String role);
    
    /**
     * Check if a user exists with the given email
     * 
     * @param email Email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}
