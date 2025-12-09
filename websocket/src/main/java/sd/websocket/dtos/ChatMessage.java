package sd.websocket.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {
    private Long senderId;
    private Long recipientId;
    private String sender;
    private String target;
    private String text;
    private String timestamp;
}