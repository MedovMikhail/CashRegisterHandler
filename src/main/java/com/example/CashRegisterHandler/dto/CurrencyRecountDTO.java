package com.example.CashRegisterHandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRecountDTO {

    private List<StoredCurrencyDTO> storedCurrencies;
}
