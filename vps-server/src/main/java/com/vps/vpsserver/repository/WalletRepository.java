package com.vps.vpsserver.repository;

import com.vps.vpsserver.entity.Wallet;
import com.vps.vpsserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByUser(User user);
    boolean existsByUserId(Long userId);
}
