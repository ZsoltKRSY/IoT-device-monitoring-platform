package sd.users.dtos.mappers;

import sd.users.dtos.UserDTO;
import sd.users.dtos.UserOperationEvent;
import sd.users.entities.User;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .build();
    }

    public static User toUserEntity(UserOperationEvent userCreateDTO) {
        return User.builder()
                .userId(userCreateDTO.getUserId())
                .firstName(userCreateDTO.getFirstName())
                .lastName(userCreateDTO.getLastName())
                .email(userCreateDTO.getEmail())
                .address(userCreateDTO.getAddress())
                .build();
    }

}
