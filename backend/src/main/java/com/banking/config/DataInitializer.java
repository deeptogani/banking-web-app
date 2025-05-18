package com.banking.config;

import com.banking.model.Role;
import com.banking.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize CUSTOMER role if it doesn't exist
        if (!roleRepository.findByRoleName("CUSTOMER").isPresent()) {
            Role customerRole = new Role();
            customerRole.setRoleName("CUSTOMER");
            customerRole.setDescription("Customer role");
            roleRepository.save(customerRole);
        }

        // Initialize ADMIN role if it doesn't exist
        if (!roleRepository.findByRoleName("ADMIN").isPresent()) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            adminRole.setDescription("Administrator role");
            roleRepository.save(adminRole);
        }
    }
} 