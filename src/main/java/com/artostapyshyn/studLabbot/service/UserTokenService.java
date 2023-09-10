package com.artostapyshyn.studLabbot.service;

import com.artostapyshyn.studLabbot.model.UserToken;
import com.artostapyshyn.studLabbot.repository.UserTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    public UserToken save(UserToken userToken){
        return userTokenRepository.save(userToken);
    }

    public Optional<UserToken> findByEmail(String email){
        return userTokenRepository.findByEmail(email);
    }

    public void delete(UserToken userToken){
        userTokenRepository.delete(userToken);
    }
}
