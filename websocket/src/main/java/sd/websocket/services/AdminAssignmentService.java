package sd.websocket.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sd.websocket.dtos.AuthUserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminAssignmentService {

    private final WebClient webClient;

    private final Map<Long, Long> userToAdmin = new ConcurrentHashMap<>();
    private final List<Long> admins = new ArrayList<>();

    private int index = 0;

    public AdminAssignmentService(WebClient webClient) {
        this.webClient = webClient;
    }

    public AdminAssignmentService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://auth-service:8083/auth")
                .build();
    }

    @PostConstruct
    public void loadAdmins() {
        System.out.println("Loading admins from Users service...");

        try {
            List<AuthUserDTO> allUsers =
                    webClient.get()
                            .retrieve()
                            .bodyToFlux(AuthUserDTO.class)
                            .collectList()
                            .block();
            if (allUsers != null) {
                admins.clear();

                allUsers.stream()
                        .filter(AuthUserDTO::isAdmin)        // filter admins
                        .map(AuthUserDTO::getId)
                        .forEach(admins::add);

                System.out.println("Loaded admins: " + admins);
            }

        } catch (Exception e) {
            System.err.println("Failed to fetch admin list: " + e.getMessage());
        }
    }

    public Long assignAdmin(Long userId) {
        return userToAdmin.computeIfAbsent(userId, id -> {
            if (admins.isEmpty()) {
                throw new IllegalStateException("No admins available for assignment.");
            }
            Long adminId = admins.get(index);
            index = (index + 1) % admins.size();
            return adminId;
        });
    }
}
