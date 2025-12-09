package sd.websocket.dtos;

import lombok.Getter;

@Getter
public class ChatRule {
    private final String keyword;
    private final String response;

    public ChatRule(String keyword, String response) {
        this.keyword = keyword.toLowerCase();
        this.response = response;
    }

    public boolean matches(String message) {
        return message.toLowerCase().contains(keyword);
    }

}

