package br.com.fiap.techchallenge.infrastructure.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record AvaliacaoResponse(
        UUID id,
        String descricao,
        Integer nota,
        String urgencia,
        Instant dataEnvio
) {}
