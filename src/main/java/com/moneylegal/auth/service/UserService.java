package com.moneylegal.auth.service;

import com.moneylegal.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * UserService - Adicionar este método à classe existente
 * 
 * INSTRUÇÕES:
 * Adicione o método existsByEmail() na sua classe UserService existente
 * que está em: auth/service/UserService.java
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * ⭐ ADICIONAR ESTE MÉTODO na sua classe UserService existente
     * 
     * Verifica se existe um usuário com o email fornecido
     * 
     * @param email Email a ser verificado (já normalizado em lowercase)
     * @return true se o email já existe, false caso contrário
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
