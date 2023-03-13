package com.example.account.repository;

import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

//                                                           조회할 테이블, PK의 타입
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}
