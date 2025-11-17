package org.example.subastas.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UsuariosApi {

    @Autowired
    private RestTemplate restTemplate;

    @Value("http://localhost:8081/user")
    private String usuariosUrl;

    public UsuariosApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean existeUsuario(Long userId) {
        try {
            restTemplate.getForEntity(usuariosUrl + "/" + userId, Void.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
