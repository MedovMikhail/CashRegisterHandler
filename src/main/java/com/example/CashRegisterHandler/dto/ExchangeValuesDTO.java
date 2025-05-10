package com.example.CashRegisterHandler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeValuesDTO {

    @NotNull
    private BigDecimal baseCurrencyCount;
    @NotNull
    private BigDecimal exchangeRate;
    @NotNull
    private BigDecimal baseStoredCurrency;
    @NotNull
    private BigDecimal targetStoredCurrency;
}
