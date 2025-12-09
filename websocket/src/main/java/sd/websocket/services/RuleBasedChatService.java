package sd.websocket.services;

import org.springframework.stereotype.Service;
import sd.websocket.dtos.ChatRule;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleBasedChatService {

    private final List<ChatRule> rules = new ArrayList<>();

    public RuleBasedChatService() {
        rules.add(new ChatRule("consumption", "To see the consumption of your devices, visit the 'Energy Consumption' page and select a date."));
        rules.add(new ChatRule("overconsumption alert", "You will receive notifications whenever a device exceeds its allowed energy consumption."));
        rules.add(new ChatRule("overconsumption", "You will receive notifications whenever a device exceeds its allowed energy consumption."));
        rules.add(new ChatRule("energy", "Check the 'Energy Consumption' page to see hourly usage graphs."));
        rules.add(new ChatRule("all devices", "You can view all your devices under 'My Devices'."));
        rules.add(new ChatRule("my devices", "You can view all your devices under 'My Devices'."));
        rules.add(new ChatRule("day", "You can change the day using the date picker on the 'Energy Consumption' page."));
        rules.add(new ChatRule("help", "Please describe your issue, or ask a question about devices or consumption."));
        rules.add(new ChatRule("account", "You can view your profile and settings in the top-right profile menu."));
        rules.add(new ChatRule("profile", "You can view your profile and settings in the top-right profile menu."));
        rules.add(new ChatRule("hourly chart", "You can view hourly consumption by selecting a date and checking the hourly chart on the 'Energy Consumption' page."));
        rules.add(new ChatRule("daily chart", "Switch to daily view using the date selector above the consumption graph."));
    }

    public String getRuleResponse(String message) {
        for (ChatRule rule : rules) {
            if (rule.matches(message)) {
                return rule.getResponse();
            }
        }
        return null;
    }
}
