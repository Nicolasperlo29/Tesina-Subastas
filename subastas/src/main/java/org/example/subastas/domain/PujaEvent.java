package org.example.subastas.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class PujaEvent extends ApplicationEvent {

    private Long subastaId;

    private BigDecimal monto;

    public PujaEvent(Object source, Long subastaId, BigDecimal monto) {
        super(source);
        this.subastaId = subastaId;
        this.monto = monto;
    }

    public Long getSubastaId() { return subastaId; }
    public BigDecimal getMonto() { return monto; }

}
