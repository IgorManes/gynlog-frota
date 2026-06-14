package br.com.gynlog.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa uma despesa registrada para um veículo.
 *
 * Campos:
 *   id          → identificador único
 *   vehicle     → veículo ao qual a despesa pertence
 *   category    → tipo da despesa (combustível, multa, IPVA...)
 *   description → descrição livre digitada pelo usuário
 *   date        → data em que a despesa ocorreu
 *   value       → valor em reais
 *   mileage     → quilometragem no momento da despesa (usado para calcular consumo médio)
 */
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