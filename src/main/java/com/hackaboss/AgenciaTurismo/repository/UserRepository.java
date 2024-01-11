package com.hackaboss.AgenciaTurismo.repository;

import com.hackaboss.AgenciaTurismo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Encontrar al usuario con el email
    User findByEmail(String email);
}
