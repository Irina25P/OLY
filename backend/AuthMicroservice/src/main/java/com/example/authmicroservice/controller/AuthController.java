package com.example.authmicroservice.controller;

import com.example.authmicroservice.dtos.AuthResponse;
import com.example.authmicroservice.dtos.RegisterAdminDTO;
import com.example.authmicroservice.dtos.RegisterClientDTO;
import com.example.authmicroservice.dtos.RegisterSellerDTO;
import com.example.authmicroservice.dtos.mappers.AuthRequest;
import com.example.authmicroservice.entities.User;
import com.example.authmicroservice.services.JwtService;
import com.example.authmicroservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${security.username.header}")
    private String securityUsernameHeader;

    @Value("${security.role.header}")
    private String securityRoleHeader;

    @Value("${security.user.id.header}")
    private String securityUserIdHeader;
    
    
    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Register an admin", description = "Creates a new admin user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Admin registered successfully"),
        @ApiResponse(responseCode = "400", description = "Registration error")
    })
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterAdminDTO dto) {
        try {
            String result = userService.addAdmin(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering admin: " + e.getMessage());
        }
    }

    @Operation(summary = "Register a seller", description = "Creates a new seller user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Seller registered successfully"),
        @ApiResponse(responseCode = "400", description = "Registration error")
    })
    @PostMapping("/register/seller")
    public ResponseEntity<String> registerSeller(@Valid @RequestBody RegisterSellerDTO dto) {
        try {
            String result = userService.addSeller(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering seller: " + e.getMessage());
        }
    }

    @Operation(summary = "Register a client", description = "Creates a new client user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client registered successfully"),
        @ApiResponse(responseCode = "400", description = "Registration error")
    })
    @PostMapping("/register/client")
    public ResponseEntity<String> registerClient(@Valid @RequestBody RegisterClientDTO dto) {
        try {
            String result = userService.addClient(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering client: " + e.getMessage());
        }
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                User user = userService.findByUsername(authRequest.getUsername());
                AuthResponse result = jwtService.generateToken(user.getId(), authRequest.getUsername());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Verify token", description = "Validates a JWT token and returns user info in headers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            String token = authorizationHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null && jwtService.validateToken(token, username)) {

                String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
                String userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
                HttpHeaders headers = new HttpHeaders();

                headers.add(securityUsernameHeader, username);
                headers.add(securityRoleHeader, role);
                headers.add(securityUserIdHeader, userId);

                return new ResponseEntity<>(headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

