package com.example.CashRegisterHandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoredCurrencyDTO {

    private Long id;
    private BigDecimal count;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private Long cashRegisterId;
    private Long currencyId;
}
