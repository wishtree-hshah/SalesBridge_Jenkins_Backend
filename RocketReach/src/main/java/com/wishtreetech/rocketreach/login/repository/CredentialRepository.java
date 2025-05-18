package com.wishtreetech.rocketreach.login.repository;

import com.wishtreetech.rocketreach.login.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByEmail(String email);
}
