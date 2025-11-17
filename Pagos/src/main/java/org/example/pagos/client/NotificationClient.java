package org.example.pagos.client;

import org.example.pagos.DTOS.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Autowired
    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyPagoRealizado(String email, Long userId, Long subastaId, String title) {
        String url = "http://localhost:8087/notification/finalizada/email/" + email;

        EmailRequest request = new EmailRequest();
        request.setEmail(email);
        request.setUserId(userId);
        request.setSubastaId(subastaId);
        request.setTitle(title);

        System.out.println("Enviando notificacion: " + request.getEmail());
        restTemplate.postForEntity(url, request, Void.class);
    }
}
