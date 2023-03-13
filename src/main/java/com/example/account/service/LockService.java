package com.example.account.service;


import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    // RedisRepositoryConfig의 Bean명 하고 이름이 같으면 RedisRepositoryConfig의 Bean이 자동으로 주입이 됨.
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            // 1초동안 기다리면서 찾압보고, unlock을 해주지 않으면 15초 동안 가지고 있다 풀어줌
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            if (!isLock) {
                log.error("===== Lock  acquisition failed=====");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed", e);
        }
    }


    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }


    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
