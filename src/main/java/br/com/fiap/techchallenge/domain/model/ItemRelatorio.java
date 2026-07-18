package br.com.fiap.techchallenge.domain.model;

import java.time.Instant;

public record ItemRelatorio(
    String descricao,
    Urgencia urgencia,
    Instant dataEnvio
) {}
