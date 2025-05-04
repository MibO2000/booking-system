package com.mibo2000.codigo.codetest.config;

import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] userArr = username.split(",");
        username = userArr[0];
        Optional<UserEntity> user = userRepo.findByUsername(username);
        if (user.isPresent() && user.get().getVerified()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
