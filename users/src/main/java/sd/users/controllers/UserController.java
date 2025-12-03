package sd.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.users.dtos.UserDTO;
import sd.users.dtos.UserOperationEvent;
import sd.users.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(
        name = "Users",
        description = "Endpoints for managing user profiles, including creation, updates, retrieval, and deletion."
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users stored in the system."
    )
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves details of a specific user based on their unique ID."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(
            summary = "Create a new user (called internally by auth service)",
            description = "Creates a new user profile in the database using the provided information."
    )
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserOperationEvent userCreateDTO) {
        return ResponseEntity.ok(userService.createUser(userCreateDTO));
    }

    @Operation(
            summary = "Update existing user (called internally by auth service)",
            description = "Updates information for a specific user identified by their ID."
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserOperationEvent updatedUser) {
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }

    @Operation(
            summary = "Delete user (called internally by auth service)",
            description = "Removes a user profile from the system by their unique ID."
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
