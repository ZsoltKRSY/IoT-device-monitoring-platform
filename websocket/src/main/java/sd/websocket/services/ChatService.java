package sd.websocket.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sd.websocket.dtos.ChatMessage;

import java.util.Map;

@Service
public class ChatService {

    private final RuleBasedChatService ruleBasedChatService;
    private final AdminAssignmentService adminAssignmentService;

    private final SimpMessagingTemplate messagingTemplate;
    private final WebClient ollamaWebClient;

    public ChatService(RuleBasedChatService ruleBasedChatService, AdminAssignmentService adminAssignmentService, SimpMessagingTemplate messagingTemplate) {
        this.ruleBasedChatService = ruleBasedChatService;
        this.adminAssignmentService = adminAssignmentService;
        this.messagingTemplate = messagingTemplate;
        this.ollamaWebClient = WebClient.builder()
                .baseUrl("http://ollama-model:11434")
                .build();
    }

    public void processIncomingMessage(ChatMessage message) {
        if ("ai".equals(message.getTarget())) {
            handleAiMessage(message);
        } else if ("admin".equals(message.getTarget()) && "user".equals(message.getSender())) {
            handleUserToAdminMessage(message);
        } else if ("user".equals(message.getTarget()) && "admin".equals(message.getSender())) {
            handleAdminToUserMessage(message);
        }
    }

    private void sendUserTopicMessage(ChatMessage message) {
        Long receivingUserId = message.getRecipientId();

        if (receivingUserId == null && "ai".equals(message.getSender())) {
            System.err.println("Cannot send AI reply without recipientId.");
            return;
        }

        if (message.getTimestamp() == null) {
            message.setTimestamp(String.valueOf(System.currentTimeMillis()));
        }

        messagingTemplate.convertAndSend(
                "/topic/chat.user." + receivingUserId,
                message
        );
    }

    private void sendAiReply(Long originalSenderId, String text) {
        ChatMessage response = new ChatMessage();
        response.setSenderId(null);
        response.setRecipientId(originalSenderId);
        response.setSender("ai");
        response.setTarget("user");
        response.setText(text);

        sendUserTopicMessage(response);
    }

    private void handleAiMessage(ChatMessage message) {
        String ruleResponse = ruleBasedChatService.getRuleResponse(message.getText());

        if (ruleResponse != null) {
            sendAiReply(message.getSenderId(), ruleResponse);
        } else {
            this.getAiResponse(message.getText())
                    .subscribe(aiReplyText -> sendAiReply(message.getSenderId(), aiReplyText));
        }
    }

    private void handleUserToAdminMessage(ChatMessage message) {
        Long userId = message.getSenderId();
        Long assignedAdminId = adminAssignmentService.assignAdmin(userId);

        if (message.getRecipientId() == null) {
            ChatMessage userUpdate = new ChatMessage();
            userUpdate.setSenderId(assignedAdminId);
            userUpdate.setRecipientId(userId);
            userUpdate.setSender("system");
            userUpdate.setTarget("admin");
            userUpdate.setText("You are now connected to admin with ID: " + assignedAdminId);
            userUpdate.setTimestamp(String.valueOf(System.currentTimeMillis()));
            messagingTemplate.convertAndSend("/topic/chat.user." + userId, userUpdate);
        }

        message.setRecipientId(assignedAdminId);
        messagingTemplate.convertAndSend(
                "/queue/admin." + assignedAdminId + ".inbox",
                message
        );
    }

    private void handleAdminToUserMessage(ChatMessage message) {
        Long receivingUserId = message.getRecipientId();

        if (receivingUserId == null) {
            System.err.println("Admin reply missing recipientId (User ID). Message dropped.");
            return;
        }

        sendUserTopicMessage(message);
    }

    private Mono<String> getAiResponse(String prompt) {
        return this.ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(Map.of(
                        "model", "llama3.1:latest",
                        "prompt", prompt,
                        "stream", false,
                        "system", """
                                    You are the AI assistant for a Smart Energy Management platform.\s
                                    Your role is to help users understand and navigate the application.\s
                                
                                    The platform allows users to:
                                    - View their devices, including device name, manufacturer, description, and maximum consumption in the 'My Devices' tab.
                                    - View hourly energy consumption graphs on a specific date (day) for their devices in the 'Energy Consumption' tab.
                                    - Select the date and which devices are displayed in the graphs.
                                    - Check their profile details, including account information.
                                    - Register, log in, or log out of the platform.
                                
                                    Always provide clear, helpful, friendly but CONCISE answers focused on these features.\s
                                    If a question does not match these topics, politely indicate that the AI is specialized for this platform.
                                """
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object responseText = response.get("response");
                    return responseText != null ? responseText.toString() : "No response";
                });
    }
}
