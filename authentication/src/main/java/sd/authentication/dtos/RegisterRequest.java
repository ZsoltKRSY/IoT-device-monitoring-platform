package sd.authentication.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String password;

    private String email;
    private String firstName;
    private String lastName;
    private String address;
}
