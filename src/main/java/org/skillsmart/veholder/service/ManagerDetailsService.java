package org.skillsmart.veholder.service;

import org.skillsmart.veholder.entity.Manager;
import org.skillsmart.veholder.entity.dto.ManagerDTO;
import org.skillsmart.veholder.repository.ManagerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerDetailsService implements UserDetailsService {

    private ManagerRepository repo;

    public ManagerDetailsService(ManagerRepository repository) {
        this.repo = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found"));
        return new Manager(manager.getUsername(), manager.getPassword(), manager.getFullName());
    }

    public List<ManagerDTO> getManagersList() {
        return repo.getManagersList();
    }
}
