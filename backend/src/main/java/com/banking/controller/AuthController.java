package com.banking.controller;

import com.banking.repository.RoleRepository;
import com.banking.repository.UserRepository;
import com.banking.service.JwtService;
import com.banking.service.UserService;
import com.banking.service.CustomUserDetailsService;
import com.banking.dto.UserRegistrationRequest;
import com.banking.model.Role;
import com.banking.model.User;
import com.banking.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import com.banking.dto.UserLoginRequest;
import com.banking.model.CustomerDetails;
import com.banking.dto.ForgotPasswordRequest;
import com.banking.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    //Registration Logic Starts
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody UserRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Email is already in use!"));
        }

        try {
            // Create new customer account
            User user = createUserFromRequest(request);

            // Add CUSTOMER role
            Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Error: Role CUSTOMER not found."));

            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            user.setRoles(roles);

            // Register user with the specified account type
            userService.registerCustomer(user, request.getAccountType());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Customer registered successfully with " + request.getAccountType() + " account!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserRegistrationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Email is already in use!"));
        }

        try {
            // Create new admin account
            User user = createUserFromRequest(request);

            // Add ADMIN role
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            user.setRoles(roles);

            // Save user
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Admin registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    private User createUserFromRequest(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setIsActive(true);
        return user;
    }
    //Registration Logic Ends

    //Login Logic Starts
    @PostMapping("/login/customer")
    public ResponseEntity<?> loginCustomer(@Valid @RequestBody UserLoginRequest request) {

        try {
            //Get user by username with roles eagerly fetched
            User user = userRepository.findByUsernameWithRoles(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            //Check if password is matching
            boolean isPasswordMatching = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!isPasswordMatching) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(false, "Invalid Credentials!"));
            }

            // Create claims for JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("roles", user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList()));
            claims.put("userId", user.getUserId());

            // Generate JWT token
            String token = jwtService.generateToken(claims, customUserDetailsService.loadUserByUsername(user.getUsername()));

            // Create response with token
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer logged in successfully!");
            response.put("token", token);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody UserLoginRequest request) {

        try {
            //Get user by username with roles eagerly fetched
            User user = userRepository.findByUsernameWithRoles(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            //Check if password is matching
            boolean isPasswordMatching = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!isPasswordMatching) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(false, "Invalid Credentials!"));
            }

            // Verify user has ADMIN role
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ADMIN"));

            if (!isAdmin) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(false, "User does not have admin privileges!"));
            }

            // Create claims for JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("roles", user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList()));
            claims.put("userId", user.getUserId());

            // Generate JWT token
            String token = jwtService.generateToken(claims, customUserDetailsService.loadUserByUsername(user.getUsername()));

            // Create response with token
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin logged in successfully!");
            response.put("token", token);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("email", user.getEmail());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred: " + e.getMessage()));
        }
    }
    //Login Logic Ends

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Verification code sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getVerificationCode(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(true, "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
