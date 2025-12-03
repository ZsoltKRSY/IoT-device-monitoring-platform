package sd.authentication.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOperationEvent implements Serializable {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
