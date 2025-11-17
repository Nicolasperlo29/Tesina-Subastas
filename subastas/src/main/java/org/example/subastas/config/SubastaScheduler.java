package org.example.subastas.config;

import org.example.subastas.service.SubastaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SubastaScheduler {

    @Autowired
    private SubastaService subastaService;

    @Scheduled(fixedRate = 100000) // cada 60 segundos actualiza el estado de las subastas en la base de datos
    public void actualizarEstadosPeriodicamente() {
        System.out.println("Scheduler ejecutándose a las " + Instant.now());
        subastaService.actualizarEstadosSubastas();
    }
}
