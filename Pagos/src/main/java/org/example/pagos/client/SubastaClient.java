package org.example.pagos.client;

import org.example.pagos.DTOS.EmailRequest;
import org.example.pagos.DTOS.SubastaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SubastaClient {

    private final RestTemplate restTemplate;

    @Autowired
    public SubastaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SubastaDTO obtenerSubasta(Long id) {
        String url = "http://localhost:8083/subastas/subasta/" + id;

        ResponseEntity<SubastaDTO> subastaDTO = restTemplate.getForEntity(url, SubastaDTO.class);

        return subastaDTO.getBody();
    }
}
