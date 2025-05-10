package com.example.CashRegisterHandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeValuesDTO {

    private BigDecimal baseCurrencyCount;
    private BigDecimal targetCurrencyCount;
    private BigDecimal exchangeRate;
    private BigDecimal baseStoredCurrency;
    private BigDecimal targetStoredCurrency;
}
