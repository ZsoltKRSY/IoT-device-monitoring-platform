package sd.authentication.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOperationDTO {
    private String username;
    private String password;
    private Boolean isAdmin;

    private String email;
    private String firstName;
    private String lastName;
    private String address;
}