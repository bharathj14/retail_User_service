package com.example.retail.services;

import com.example.retail.dtos.LoginUserDto;
import com.example.retail.dtos.RegisterUserDto;
import com.example.retail.entities.User;
import com.example.retail.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;


    public AuthenticationService(
            UserService userService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setEmail(input.getEmail());
        user.setName(input.getName());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));
        user.setActive(true);
        return userService.saveUser(user);
    }


    public User authenticate(LoginUserDto loginUserDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );
        return (User) authentication.getPrincipal();
    }
}