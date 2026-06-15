package br.com.gynlog.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Movement(
        int id,
        Vehicle vehicle,
        ExpenseType category,
        String description,
        LocalDate date,
        BigDecimal value,
        double mileage
) {
}