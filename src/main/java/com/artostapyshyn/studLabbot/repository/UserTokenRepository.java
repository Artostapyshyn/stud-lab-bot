package com.artostapyshyn.studLabbot.repository;

import com.artostapyshyn.studLabbot.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    Optional<UserToken> findByEmail(String email);

    UserToken findByChatId(Long chatId);
}