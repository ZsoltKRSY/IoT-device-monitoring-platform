package sd.authentication.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sd.authentication.dtos.*;
import sd.authentication.dtos.mappers.AuthUserMapper;
import sd.authentication.entities.AuthUser;
import sd.authentication.handlers.model.ResourceNotFoundException;
import sd.authentication.repositories.AuthRepository;

import java.util.List;
import java.util.Optional;

import static sd.authentication.config.RabbitMQConfig.SYNC_EXCHANGE;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AuthService(AuthRepository authRepository, JwtService jwtService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.authRepository = authRepository;
        encoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    private void publishSyncEvent(String type, String payload) {
        SyncEvent event = new SyncEvent(type, payload);
        rabbitTemplate.convertAndSend(SYNC_EXCHANGE, "", event);
    }

    public List<AuthUserDTO> getAllAuthUsers() {
        return authRepository.findAll().stream()
                .map(AuthUserMapper::toAuthUserDTO)
                .toList();
    }

    public List<AuthUserDTO> getAllNonAdminAuthUsers() {
        return authRepository.findAll().stream()
                .filter(authUser -> !authUser.isAdmin())
                .map(AuthUserMapper::toAuthUserDTO)
                .toList();
    }

    public AuthUserDTO getAuthUserById(Long id) {
        Optional<AuthUser> authUserOptional = authRepository.findById(id);
        if (authUserOptional.isEmpty()) {
            LOGGER.error("User credentials with id {} were not found in db", id);
            throw new ResourceNotFoundException(AuthUser.class.getSimpleName() + " with id: " + id);
        }
        return AuthUserMapper.toAuthUserDTO(authUserOptional.get());
    }

    public JwtResponse register(RegisterRequest request) {
        if (authRepository.getByUsername(request.getUsername()).isPresent()) {
            LOGGER.error("Credentials having username {} are already present in db", request.getUsername());
            throw new DataIntegrityViolationException("Username is already taken");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(request.getUsername());
        authUser.setPassword(encoder.encode(request.getPassword()));
        authRepository.save(authUser);

        UserOperationEvent userCreated = UserOperationEvent.builder()
                .userId(authUser.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();

        try {
            String payloadJson = objectMapper.writeValueAsString(userCreated);
            publishSyncEvent("USER_CREATED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send create user sync message {}", userCreated, e);
        }

        String token = jwtService.generateToken(
                authUser.getId(),
                authUser.getUsername(),
                authUser.isAdmin()
        );

        return new JwtResponse(token);
    }

    public JwtResponse login(LoginRequest request) {
        Optional<AuthUser> authUserOptional = authRepository.getByUsername((request.getUsername()));

        if (authUserOptional.isEmpty()) {
            LOGGER.error("Invalid username");
            throw new ResourceNotFoundException("Invalid username or password");
        }

        AuthUser authUser = authUserOptional.get();

        if (!encoder.matches(request.getPassword(), authUser.getPassword())) {
            LOGGER.error("Invalid password");
            throw new ResourceNotFoundException("Invalid username or password");
        }

        String token = jwtService.generateToken(
                authUser.getId(),
                authUser.getUsername(),
                authUser.isAdmin()
        );

        return new JwtResponse(token);
    }

    public AuthUserDTO createUser(UserOperationDTO userOperationDTO) {
        if (authRepository.getByUsername(userOperationDTO.getUsername()).isPresent()) {
            LOGGER.error("Credentials having username {} are already present in db", userOperationDTO.getUsername());
            throw new DataIntegrityViolationException("Username is already taken");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(userOperationDTO.getUsername());
        authUser.setPassword(encoder.encode(userOperationDTO.getPassword()));
        authUser.setAdmin(userOperationDTO.getIsAdmin());
        AuthUser savedUser = authRepository.save(authUser);

        UserOperationEvent userCreated = UserOperationEvent.builder()
                .userId(authUser.getId())
                .firstName(userOperationDTO.getFirstName())
                .lastName(userOperationDTO.getLastName())
                .email(userOperationDTO.getEmail())
                .address(userOperationDTO.getAddress())
                .build();

        try {
            String payloadJson = objectMapper.writeValueAsString(userCreated);
            publishSyncEvent("USER_CREATED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send create user sync message {}", userCreated, e);
        }

        return AuthUserMapper.toAuthUserDTO(savedUser);
    }

    public AuthUserDTO updateUser(Long id, UserOperationDTO userOperationDTO) {
        Optional<AuthUser> authUserOptional = authRepository.findById(id);
        if (authUserOptional.isEmpty()) {
            LOGGER.error("Credentials with id {} were not found in db, updating it is not possible", id);
            throw new ResourceNotFoundException(AuthUser.class.getSimpleName() + " with id: " + id);
        }

        AuthUser existingUser = authUserOptional.get();
        if (!existingUser.getUsername().equals(userOperationDTO.getUsername()) && authRepository.getByUsername(userOperationDTO.getUsername()).isPresent()) {
            LOGGER.error("Credentials having username {} are already present in db", userOperationDTO.getUsername());
            throw new DataIntegrityViolationException("Username is already taken");
        }

        existingUser.setUsername(userOperationDTO.getUsername());
        existingUser.setAdmin(userOperationDTO.getIsAdmin());
        AuthUser updatedUser = authRepository.save(existingUser);

        UserOperationEvent userUpdated = UserOperationEvent.builder()
                .userId(existingUser.getId())
                .firstName(userOperationDTO.getFirstName())
                .lastName(userOperationDTO.getLastName())
                .email(userOperationDTO.getEmail())
                .address(userOperationDTO.getAddress())
                .build();

        try {
            String payloadJson = objectMapper.writeValueAsString(userUpdated);
            publishSyncEvent("USER_UPDATED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send update user sync message {}", userUpdated, e);
        }

        LOGGER.debug("Credentials with id {} were updated in db", updatedUser.getId());
        return AuthUserMapper.toAuthUserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        authRepository.deleteById(id);

        try {
            String payloadJson = objectMapper.writeValueAsString(new UserIdEvent(id));
            publishSyncEvent("USER_DELETED", payloadJson);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send delete user sync message {}", id, e);
        }

        LOGGER.debug("Credentials with id and all related data {} were deleted from the db", id);
    }

}
