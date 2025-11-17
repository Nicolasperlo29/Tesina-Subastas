package org.example.pagos.service.implementacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.pagos.DTOS.DepositoGarantiaDTO;
import org.example.pagos.DTOS.MercadoPagoPaymentResponse;
import org.example.pagos.DTOS.PagoWebhookDTO;
import org.example.pagos.DTOS.SubastaDTO;
import org.example.pagos.client.NotificationClient;
import org.example.pagos.client.SubastaClient;
import org.example.pagos.entities.DepositoGarantiaEntity;
import org.example.pagos.entities.PagoEntity;
import org.example.pagos.repositories.DepositoRepository;
import org.example.pagos.repositories.PagoRepository;
import org.example.pagos.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DepositoRepository depositoGarantiaRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private SubastaClient subastaClient;

    @Value("${mercadopago.access.token}")
    private String accessToken;

    public PagoEntity guardarPagoDesdeWebhook(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            System.out.println("paymentId inválido o vacío.");
            return null;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            int maxReintentos = 3;
            int intentos = 0;
            boolean exito = false;
            PagoEntity pagoEntity = null;

            while (intentos < maxReintentos && !exito) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.mercadopago.com/v1/payments/" + paymentId))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Intento " + (intentos + 1) + ": Status " + response.statusCode());

                if (response.statusCode() == 200) {
                    String body = response.body();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                        MercadoPagoPaymentResponse pagoMP = mapper.readValue(body, MercadoPagoPaymentResponse.class);

                        // Obtener IDs de metadata
                        Long usuarioId = pagoMP.getMetadata() != null && pagoMP.getMetadata().containsKey("usuario_id")
                                ? Long.valueOf(pagoMP.getMetadata().get("usuario_id").toString()) : null;

                        Long subastaId = pagoMP.getMetadata() != null && pagoMP.getMetadata().containsKey("subasta_id")
                                ? Long.valueOf(pagoMP.getMetadata().get("subasta_id").toString()) : null;

                        // Verificación de pago duplicado aprobado
                        Optional<PagoEntity> pagoExistente = pagoRepository
                                .findByUsuarioIdAndSubastaIdAndEstado(usuarioId, subastaId, "approved");

                        if (pagoExistente.isPresent()) {
                            System.out.println("Ya existe un pago aprobado para esta subasta y usuario.");
                            return pagoExistente.get(); // O null, según prefieras
                        }

                        pagoEntity = new PagoEntity();
                        pagoEntity.setMpPaymentId(String.valueOf(pagoMP.getId()));
                        pagoEntity.setEstado(pagoMP.getStatus());
                        pagoEntity.setDetalleEstado(pagoMP.getStatusDetail());
                        pagoEntity.setMonto(pagoMP.getTransactionAmount());
                        pagoEntity.setMetodoPago(pagoMP.getPaymentMethodId());
                        pagoEntity.setTipoPago(pagoMP.getPaymentTypeId());
                        pagoEntity.setFechaCreacion(pagoMP.getDateCreated());
                        pagoEntity.setFechaConfirmacion(pagoMP.getDateApproved());
                        pagoEntity.setEmailUsuario(
                                pagoMP.getPayer() != null ? pagoMP.getPayer().getEmail() : null);
                        pagoEntity.setCodigoAutorizacion(pagoMP.getAuthorizationCode());
                        pagoEntity.setUltimosCuatroDigitos(
                                pagoMP.getCard() != null ? pagoMP.getCard().getLastFourDigits() : null);
                        pagoEntity.setMontoNeto(
                                pagoMP.getTransactionDetails() != null ? pagoMP.getTransactionDetails().getNetReceivedAmount() : null);
                        pagoEntity.setUsuarioId(usuarioId);
                        pagoEntity.setSubastaId(subastaId);

                        pagoRepository.save(pagoEntity);
                        System.out.println("Pago guardado con éxito." + pagoEntity);
//                        SubastaDTO subastaDTO = subastaClient.obtenerSubasta(pagoEntity.getSubastaId());
//                        notificationClient.notifyPagoRealizado(subastaDTO.getEmail(), subastaDTO.getUserGanadorId(), pagoEntity.getSubastaId());
                        exito = true;

                    } catch (Exception e) {
                        System.out.println("Error al parsear o guardar el pago:");
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Pago aún no disponible. Reintentando...");
                    Thread.sleep(1500);
                    intentos++;
                }
            }

            if (!exito) {
                System.out.println("No se pudo obtener el pago después de varios intentos.");
            }

            return pagoEntity;

        } catch (Exception e) {
            System.out.println("Error general en guardarPagoDesdeWebhook:");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DepositoGarantiaEntity guardarDepositoDesdeWebhook(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            System.out.println("paymentId inválido o vacío.");
            return null;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            int maxReintentos = 3;
            int intentos = 0;
            boolean exito = false;
            DepositoGarantiaEntity deposito = null;

            while (intentos < maxReintentos && !exito) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.mercadopago.com/v1/payments/" + paymentId))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                    MercadoPagoPaymentResponse pagoMP = mapper.readValue(response.body(), MercadoPagoPaymentResponse.class);

                    Long userId = pagoMP.getMetadata() != null && pagoMP.getMetadata().containsKey("usuario_id")
                            ? Long.valueOf(pagoMP.getMetadata().get("usuario_id").toString()) : null;

                    Long subastaId = pagoMP.getMetadata() != null && pagoMP.getMetadata().containsKey("subasta_id")
                            ? Long.valueOf(pagoMP.getMetadata().get("subasta_id").toString()) : null;

                    if (userId == null || subastaId == null) {
                        System.out.println("Metadata incompleta");
                        return null;
                    }

                    // Verificar si ya existe un depósito activo
                    Optional<DepositoGarantiaEntity> existente = depositoGarantiaRepository
                            .findByUserIdAndSubastaIdAndActivoTrue(userId, subastaId);

                    if (existente.isPresent()) {
                        System.out.println("Ya existe un depósito activo para esta subasta y usuario.");
                        return existente.get();
                    }

                    // Guardar depósito
                    deposito = new DepositoGarantiaEntity();
                    deposito.setMonto(pagoMP.getTransactionAmount());
                    deposito.setUserId(userId);
                    deposito.setSubastaId(subastaId);
                    deposito.setActivo(true);
                    deposito.setFechaDeposito(LocalDateTime.now());

                    depositoGarantiaRepository.save(deposito);
                    exito = true;
                    System.out.println("Depósito guardado correctamente.");
                } else {
                    Thread.sleep(1500);
                    intentos++;
                }
            }

            if (!exito) {
                System.out.println("No se pudo guardar el depósito después de varios intentos.");
            }

            return deposito;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MercadoPagoPaymentResponse obtenerPagoDesdeMP(String paymentId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mercadopago.com/v1/payments/" + paymentId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(response.body(), MercadoPagoPaymentResponse.class);
        }

        throw new IllegalStateException("No se pudo obtener el pago de MercadoPago.");
    }

    @Override
    public List<PagoEntity> getPagosByUsuario(Long usuarioId) {

        return pagoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public String getEstadoPago(Long subastaId) {
        Optional<PagoEntity> pago = pagoRepository.findTopBySubastaIdOrderByFechaConfirmacionDesc(subastaId);

        if (pago.isEmpty()) {
            return "No hay pagos realizados";
        }

        return pago.get().getEstado();
    }

    @Override
    public Boolean getDeposito(Long userId, Long subastaId) {
        boolean deposito = depositoGarantiaRepository.existsByUserIdAndSubastaIdAndActivoTrue(userId, subastaId);

        if (!deposito) {
            return false;
        }

        return deposito;
    }
}
