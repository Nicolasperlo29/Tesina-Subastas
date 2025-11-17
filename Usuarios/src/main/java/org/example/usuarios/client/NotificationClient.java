package org.example.usuarios.client;

import org.example.usuarios.DTOS.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationClient {

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl = "http://localhost:8087/notification/email";

    @Autowired
    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyVerificationEmail(String email, Long userId, String link) {
        String url = "http://localhost:8087/notification/verification/email/" + email;

        EmailRequest request = new EmailRequest();
        request.setEmail(email);
        request.setUserId(userId);
        request.setLink(link);

        restTemplate.postForEntity(url, request, Void.class);
    }

    public void notifyBienvenidaEmail(String email, Long userId) {
        String url = "http://localhost:8087/notification/email/welcome/{email}?userId={userId}";
        restTemplate.postForEntity(url, null, Void.class, email, userId);
    }

    public void notifyResetPasswordEmail(String email, Long userId, String resetLink) {
        String url = "http://localhost:8087/notification/forgot-password/email/" + email;

        EmailRequest request = new EmailRequest();
        request.setEmail(email);
        request.setUserId(userId);
        request.setLink(resetLink);

        restTemplate.postForEntity(url, request, Void.class);
    }
}
