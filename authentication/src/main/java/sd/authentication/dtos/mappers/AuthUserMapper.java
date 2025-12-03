package sd.authentication.dtos.mappers;

import sd.authentication.dtos.AuthUserDTO;
import sd.authentication.entities.AuthUser;

public class AuthUserMapper {

    public static AuthUserDTO toAuthUserDTO(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .username(authUser.getUsername())
                .isAdmin(authUser.isAdmin())
                .build();
    }

}
