package com.moneylegal.security;

import com.moneylegal.auth.entity.User;
import com.moneylegal.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getId())
            .password(user.getPasswordHash())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .accountExpired(false)
            .accountLocked(!user.getIsActive())
            .credentialsExpired(false)
            .disabled(!user.getIsActive())
            .build();
    }
}