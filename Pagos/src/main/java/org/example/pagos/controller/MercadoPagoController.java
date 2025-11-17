package org.example.pagos.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.example.pagos.DTOS.DepositoGarantiaDTO;
import org.example.pagos.DTOS.MercadoPagoPaymentResponse;
import org.example.pagos.DTOS.PagoRequestDTO;
import org.example.pagos.DTOS.SubastaDTO;
import org.example.pagos.client.NotificationClient;
import org.example.pagos.client.SubastaClient;
import org.example.pagos.entities.PagoEntity;
import org.example.pagos.repositories.DepositoRepository;
import org.example.pagos.repositories.PagoRepository;
import org.example.pagos.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pagos")
public class MercadoPagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DepositoRepository depositoRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private SubastaClient subastaClient;

    @Value("${mercadopago.access.token}")
    private String accesToken;

    @PostMapping("/crear-preferencia")
    public Map<String, String> crearPreferencia(@RequestBody PagoRequestDTO dto) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accesToken);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(dto.getTitulo())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(dto.getPrecioFinal())
                .build();

        if (pagoRepository.existsByUsuarioIdAndSubastaIdAndEstado(dto.getUsuarioId(), dto.getSubastaId(), "approved")) {
            throw new IllegalStateException("Ya pagaste esta subasta.");
        }

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("usuarioId", dto.getUsuarioId().toString());
        metadata.put("subastaId", dto.getSubastaId().toString());

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .metadata(metadata)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        Map<String, String> response = new HashMap<>();
        // Link para redirigir al checkout real
        response.put("initPoint", preference.getInitPoint());

        SubastaDTO subastaDTO = subastaClient.obtenerSubasta(dto.getSubastaId());
        notificationClient.notifyPagoRealizado(subastaDTO.getEmailCreador(), subastaDTO.getUserGanadorId(), dto.getSubastaId(), subastaDTO.getTitle());

        return response;
    }

    @PostMapping("/crear-deposito")
    public Map<String, String> crearDeposito(@RequestBody DepositoGarantiaDTO dto) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accesToken);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title("Depósito de garantía para Subasta " + dto.getSubastaId())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(dto.getMonto())
                .build();

        if (depositoRepository.existsByUserIdAndSubastaIdAndActivoTrue(dto.getUserId(), dto.getSubastaId())) {
            throw new IllegalStateException("Ya hiciste el depósito para esta subasta.");
        }

        List<PreferenceItemRequest> items = List.of(itemRequest);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("usuarioId", dto.getUserId().toString());
        metadata.put("subastaId", dto.getSubastaId().toString());
        metadata.put("tipo", "garantia");

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .metadata(metadata)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return Map.of("initPoint", preference.getInitPoint());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody Map<String, Object> webhookPayload) {
        try {
            Map<String, Object> data = (Map<String, Object>) webhookPayload.get("data");
            String paymentId = data.get("id").toString();

            // Consultar a MP para obtener metadata
            MercadoPagoPaymentResponse pagoMP = pagoService.obtenerPagoDesdeMP(paymentId);
            String tipo = (String) pagoMP.getMetadata().get("tipo");

            if ("garantia".equalsIgnoreCase(tipo)) {
                pagoService.guardarDepositoDesdeWebhook(paymentId);
            } else {
                pagoService.guardarPagoDesdeWebhook(paymentId);
            }

            return ResponseEntity.ok("Webhook recibido correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar webhook.");
        }
    }

    @GetMapping("/estado/{subastaId}")
    public ResponseEntity<String> obtenerEstadoPago(@PathVariable Long subastaId) {
        return ResponseEntity.ok(pagoService.getEstadoPago(subastaId));
    }

    @GetMapping("/deposito/{subastaId}/{userId}")
    public ResponseEntity<Boolean> obtenerDeposito(@PathVariable Long subastaId, @PathVariable Long userId) {
        return ResponseEntity.ok(pagoService.getDeposito(userId, subastaId));
    }

    @GetMapping("/{usuarioId}")
    public List<PagoEntity> getPagosUsuario(@PathVariable Long usuarioId) {
        return pagoService.getPagosByUsuario(usuarioId);
    }
}
