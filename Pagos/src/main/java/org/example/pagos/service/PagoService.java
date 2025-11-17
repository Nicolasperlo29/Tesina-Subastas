package org.example.pagos.service;

import org.example.pagos.DTOS.DepositoGarantiaDTO;
import org.example.pagos.DTOS.MercadoPagoPaymentResponse;
import org.example.pagos.entities.DepositoGarantiaEntity;
import org.example.pagos.entities.PagoEntity;

import java.io.IOException;
import java.util.List;

public interface PagoService {

    PagoEntity guardarPagoDesdeWebhook(String paymentId);

    String getEstadoPago(Long subastaId);

    Boolean getDeposito(Long userId, Long subastaId);

    DepositoGarantiaEntity guardarDepositoDesdeWebhook(String paymentId);

    MercadoPagoPaymentResponse obtenerPagoDesdeMP(String paymentId) throws IOException, InterruptedException;

    List<PagoEntity> getPagosByUsuario(Long usuarioId);
}
