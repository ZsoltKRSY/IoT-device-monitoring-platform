package sd.authentication.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDTO {
    private Long id;
    private String username;
    private boolean isAdmin;
}
