package com.banking.service;

import com.banking.model.CustomerDetails;
import com.banking.model.Role;
import com.banking.model.User;
import com.banking.model.Account;
import com.banking.repository.CustomerDetailsRepository;
import com.banking.repository.RoleRepository;
import com.banking.repository.UserRepository;
import com.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Random;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        
        // Generate 12-digit account number
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        // Check if account number already exists
        while (accountRepository.findByAccountNumber(accountNumber.toString()).isPresent()) {
            accountNumber = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                accountNumber.append(random.nextInt(10));
            }
        }
        
        return accountNumber.toString();
    }

    @Transactional
    public User registerCustomer(User user, Account.AccountType accountType) {
        // Password is already encoded in the controller
        
        // Set the CUSTOMER role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role CUSTOMER not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        
        // Save the user
        User savedUser = userRepository.save(user);
        
        // Initialize customer details
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomer(savedUser);
        customerDetailsRepository.save(customerDetails);

        // Create account based on the requested type
        Account account = new Account();
        account.setUser(savedUser);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        
        // Set interest rate based on account type
        if (accountType == Account.AccountType.SAVINGS) {
            account.setInterestRate(new BigDecimal("4.0")); // 4% interest rate for savings account
        } else {
            account.setInterestRate(BigDecimal.ZERO); // No interest for current account
        }
        
        account.setIsActive(true);
        accountRepository.save(account);
        
        return savedUser;
    }

    @Transactional
    public User registerAdmin(User user) {
        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set the ADMIN role
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);
        
        // Save the user
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateUserDetails(Long userId, User updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    user.setAddress(updatedUser.getAddress());
                    // Don't update sensitive fields like username, email, or password here
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    @Transactional
    public User changePassword(Long userId, String newPassword) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Generate a 6-digit verification code
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // Store the code with a 10-minute expiration
        verificationCodes.put(email, code);
        scheduler.schedule(() -> verificationCodes.remove(email), 10, TimeUnit.MINUTES);

        // Send the verification code via email
        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public void resetPassword(String email, String verificationCode, String newPassword) {
        // Verify the code
        String storedCode = verificationCodes.get(email);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new RuntimeException("Invalid or expired verification code");
        }

        // Find the user and update the password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the used verification code
        verificationCodes.remove(email);
    }
}