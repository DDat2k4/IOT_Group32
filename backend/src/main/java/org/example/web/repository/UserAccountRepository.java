package org.example.web.repository;

import org.example.web.data.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount,Long>, JpaSpecificationExecutor<UserAccount> {
    @Override
    Optional<UserAccount> findById(Long aLong);

    Optional<UserAccount> findByUsername (String username);

    Optional<UserAccount> findByEmail (String email);

    Boolean existsByEmail(String email);
}
