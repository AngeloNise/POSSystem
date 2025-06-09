package com.fiberhome.authservice.service;

import com.fiberhome.authservice.model.Account;

import java.util.Optional;

public interface UserService {
   Optional<Account> getByUsername(String username);
}
