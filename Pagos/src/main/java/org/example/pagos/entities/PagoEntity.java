package org.example.pagos.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagos")
public class PagoEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mp_payment_id", unique = true)
    private String mpPaymentId;

    private String estado;

    @Column(name = "detalle_estado")
    private String detalleEstado;

    private BigDecimal monto;

    @Column(name = "monto_neto")
    private BigDecimal montoNeto;

    @Column(name = "codigo_autorizacion")
    private String codigoAutorizacion;

    @Column(name = "ultimos_cuatro_digitos")
    private String ultimosCuatroDigitos;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_confirmacion")
    private OffsetDateTime fechaConfirmacion;

    private String metodoPago;

    private String tipoPago;

    private String emailUsuario;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "subasta_id")
    private Long subastaId;
}
