package sd.websocket.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import sd.websocket.dtos.ChatMessage;
import sd.websocket.services.ChatService;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void receiveChatMessage(@Payload ChatMessage chatMessage) {
        chatService.processIncomingMessage(chatMessage);
    }
}