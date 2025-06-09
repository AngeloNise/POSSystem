package com.fiberhome.authservice.service;

import com.fiberhome.authservice.model.Account;
import com.fiberhome.authservice.model.LoginUserDetails;
import com.fiberhome.authservice.repository.LoginUserDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginUserDetailsService implements UserDetailsService {

    private final LoginUserDetailsMapper accountMapper;

    public Account getAccount(String username) {
        Account acct = accountMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return acct;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account acct = accountMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return LoginUserDetails.build(acct);

    }
}
