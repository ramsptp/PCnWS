package com.sk.PCnWS.repository;

import com.sk.PCnWS.model.User; // This import should now work perfectly
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // Tells Spring this is a Repository
// We extend JpaRepository, giving it our Model (User) and the type of its Primary Key (Long)
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically creates a query from this method name:
    // "Find a User by their username"
    Optional<User> findByUsername(String username);

    // "Find a User by their email"
    Optional<User> findByEmail(String email);
}