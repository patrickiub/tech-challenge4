package br.com.fiap.techchallenge.infrastructure.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotBlank(message = "descricao é obrigatória")
        @Size(max = 1000, message = "descricao deve ter no máximo 1000 caracteres")
        String descricao,

        @NotNull(message = "nota é obrigatória")
        @Min(value = 0, message = "nota mínima é 0")
        @Max(value = 10, message = "nota máxima é 10")
        Integer nota
) {}
