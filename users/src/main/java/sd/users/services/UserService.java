package sd.users.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sd.users.dtos.UserDTO;
import sd.users.dtos.UserOperationEvent;
import sd.users.dtos.mappers.UserMapper;
import sd.users.entities.User;
import sd.users.handlers.model.ResourceNotFoundException;
import sd.users.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserMapper.toUserDTO(userOptional.get());
    }

    public UserDTO createUser(UserOperationEvent userCreated) {
        User user = UserMapper.toUserEntity(userCreated);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getUserId());
        return UserMapper.toUserDTO(user);
    }

    public UserDTO updateUser(Long id, UserOperationEvent userUpdatedEvent) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            LOGGER.error("User with id {} was not found in db, updating it is not possible", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        User existingUser = userOptional.get();
        existingUser.setFirstName(userUpdatedEvent.getFirstName());
        existingUser.setLastName(userUpdatedEvent.getLastName());
        existingUser.setEmail(userUpdatedEvent.getEmail());
        existingUser.setAddress(userUpdatedEvent.getAddress());

        User updatedUser = userRepository.save(existingUser);
        LOGGER.debug("User with id {} was updated in db", updatedUser.getUserId());
        return UserMapper.toUserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        LOGGER.debug("User with id {} was deleted from the db", id);
    }

}
