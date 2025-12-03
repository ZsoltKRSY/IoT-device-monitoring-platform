package sd.devices.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sd.devices.dtos.UserOperationEvent;
import sd.devices.entities.User;
import sd.devices.handlers.model.ResourceNotFoundException;
import sd.devices.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserOperationEvent userCreated) {
        User user = new User(userCreated.getUserId());
        Optional<User> userOptional = userRepository.findById(userCreated.getUserId());
        if (userOptional.isPresent()) {
            LOGGER.error("User id {} is already in the db", userCreated.getUserId());
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userCreated.getUserId());
        }

        user = userRepository.save(user);
        LOGGER.debug("User id {} was inserted in db", user.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        LOGGER.debug("User id {} was deleted from the db", id);
    }
}
