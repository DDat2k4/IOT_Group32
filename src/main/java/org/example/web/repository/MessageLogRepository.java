package org.example.web.repository;

import org.example.web.data.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<UserAccount,Long> {
}
