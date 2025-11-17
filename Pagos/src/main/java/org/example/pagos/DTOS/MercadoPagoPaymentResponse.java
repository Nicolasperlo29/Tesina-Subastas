package org.example.pagos.DTOS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MercadoPagoPaymentResponse {

    private Long id;

    private String status;

    @JsonProperty("status_detail")
    private String statusDetail;

    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    @JsonProperty("payment_type_id")
    private String paymentTypeId;

    private Map<String, Object> metadata;

    private Payer payer;

    private Card card;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("date_created")
    private OffsetDateTime dateCreated;

    @JsonProperty("date_approved")
    private OffsetDateTime dateApproved;

    @JsonProperty("money_release_date")
    private OffsetDateTime moneyReleaseDate;

    @JsonProperty("transaction_details")
    private TransactionDetails transactionDetails;

    private BigDecimal feeAmount;

    private String description;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payer {
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        @JsonProperty("last_four_digits")
        private String lastFourDigits;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionDetails {
        @JsonProperty("net_received_amount")
        private BigDecimal netReceivedAmount;
    }
}
