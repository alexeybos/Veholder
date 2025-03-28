package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.User;
import org.skillsmart.veholder.repository.UserRepository;
import org.skillsmart.veholder.security.UserDetailsClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.getByLogin(username);
        return new UserDetailsClass(user.orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
