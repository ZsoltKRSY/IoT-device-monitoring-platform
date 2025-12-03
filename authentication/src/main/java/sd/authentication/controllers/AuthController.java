package sd.authentication.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.authentication.dtos.*;
import sd.authentication.services.AuthService;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and credential management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Get all authentication users",
            description = "Fetches all users from the authentication database, including admins."
    )
    @GetMapping()
    public ResponseEntity<List<AuthUserDTO>> getAllAuthUsers() {
        return ResponseEntity.ok(authService.getAllAuthUsers());
    }

    @Operation(
            summary = "Get all non-admin users",
            description = "Fetches all users who are not marked as administrators."
    )
    @GetMapping("/non-admin")
    public ResponseEntity<List<AuthUserDTO>> getAllNonAdminAuthUsers() {
        return ResponseEntity.ok(authService.getAllNonAdminAuthUsers());
    }

    @Operation(
            summary = "Get user credentials by ID",
            description = "Fetch details for a specific authentication user based on their ID."
    )
    @GetMapping("/{authUserId}")
    public ResponseEntity<AuthUserDTO> getAuthUserById(@PathVariable Long authUserId) {
        return ResponseEntity.ok(authService.getAuthUserById(authUserId));
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user with provided credentials and returns a JWT token upon success."
    )
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates the user and returns a JWT token for session management."
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Create authentication user (admin use)",
            description = "Adds a new user record to the authentication database. Typically used by admins."
    )
    @PostMapping()
    public ResponseEntity<AuthUserDTO> createUser(@RequestBody UserOperationDTO user) {
        return ResponseEntity.ok(authService.createUser(user));
    }

    @Operation(
            summary = "Update user",
            description = "Updates details for an existing user by ID."
    )
    @PutMapping("/{userId}")
    public ResponseEntity<AuthUserDTO> updateUser(@PathVariable Long userId, @RequestBody UserOperationDTO user) {
        return ResponseEntity.ok(authService.updateUser(userId, user));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user from the database by ID."
    )
    @DeleteMapping("/{authUserId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long authUserId) {
        authService.deleteUser(authUserId);
        return ResponseEntity.noContent().build();
    }
}
