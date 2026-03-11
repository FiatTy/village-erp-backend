package com.villageserp.villageerpbackend.security;

import com.villageserp.villageerpbackend.entity.UserEntity;
import com.villageserp.villageerpbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "ไม่พบ User: " + username));

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                ))
        );
    }
}
