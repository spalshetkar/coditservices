package app.codit.notification;

import app.codit.clients.notification.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@Slf4j
public record NotificationController(NotificationService notificationService) {

    @PostMapping
    public void createNotification(@RequestBody NotificationRequest notificationRequest) {
        log.info("Notification for customer {}", notificationRequest);

        notificationService.createNotification(notificationRequest);
    }
}