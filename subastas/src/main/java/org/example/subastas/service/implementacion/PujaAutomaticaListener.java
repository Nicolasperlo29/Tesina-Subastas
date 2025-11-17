package org.example.subastas.service.implementacion;

import org.example.subastas.DTO.PujaDTOPost;
import org.example.subastas.domain.PujaEvent;
import org.example.subastas.entity.PujaAutomaticaEntity;
import org.example.subastas.entity.PujaEntity;
import org.example.subastas.entity.SubastaEntity;
import org.example.subastas.repository.PujaAutomaticaRepository;
import org.example.subastas.repository.PujaRepository;
import org.example.subastas.repository.SubastaRepository;
import org.example.subastas.service.PujaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class PujaAutomaticaListener {

    @Autowired
    private PujaAutomaticaRepository pujaAutomaticaRepository;

    @Autowired
    private PujaRepository pujaRepository;

    @Autowired
    private SubastaRepository subastaRepository ;

    @Autowired
    private PujaService pujaService;

    @EventListener
    public void procesarPujaAutomatica(PujaEvent event) {
        List<PujaAutomaticaEntity> pujas = pujaAutomaticaRepository.findAllBySubasta_IdAndEstado(event.getSubastaId(), "aceptada");

        if (pujas.isEmpty()) {
            return;
        }

        Optional<SubastaEntity> subastaEntityOpt = subastaRepository.findById(event.getSubastaId());
        if (subastaEntityOpt.isEmpty()) {
            throw new RuntimeException("Subasta no encontrada");
        }
        SubastaEntity subastaEntity = subastaEntityOpt.get();

        boolean huboPuja;
        do {
            huboPuja = false;

            // Obtengo la puja más alta actual antes de cada ronda
            PujaEntity pujaEntity = pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(event.getSubastaId(), "aceptada");

            for (PujaAutomaticaEntity p : pujas) {
                BigDecimal incremento = subastaEntity.getIncrementoFijo();
                System.out.println("Incremento: " + incremento);
                BigDecimal siguienteValor = pujaEntity.getValor().add(incremento);
                System.out.println("Siguiente valor: " + siguienteValor);

                if (siguienteValor.compareTo(p.getValorMaximo()) <= 0 && !p.getUserId().equals(pujaEntity.getUserId())) {
                    PujaDTOPost pujaDTOPost = new PujaDTOPost();
                    pujaDTOPost.setEstado("aceptada");
                    pujaDTOPost.setValor(siguienteValor);
                    pujaDTOPost.setSubastaId(event.getSubastaId());
                    pujaDTOPost.setUserId(p.getUserId());

//                    if (siguienteValor.compareTo(p.getValorMaximo()) >= 0) {
//                        p.setEstado("Finalizada");
//                        pujaAutomaticaRepository.save(p);
//                    }

                    pujaService.crearPuja(pujaDTOPost);

                    System.out.println("Pujando automaticamente: " + pujaDTOPost);

                    huboPuja = true;
                    // Actualizo la pujaEntity para reflejar el nuevo valor antes de la siguiente puja
                    pujaEntity = pujaRepository.findTopBySubasta_IdAndEstadoOrderByValorDesc(event.getSubastaId(), "aceptada");
                }
            }
        } while (huboPuja);
    }

}
