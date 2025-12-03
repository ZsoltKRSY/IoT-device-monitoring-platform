package sd.users.dtos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdEvent implements Serializable {
    private Long userId;
}
