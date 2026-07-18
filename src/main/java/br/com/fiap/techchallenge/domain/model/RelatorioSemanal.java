package br.com.fiap.techchallenge.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record RelatorioSemanal(
    Instant geradoEm,
    Instant janelaInicio,
    Instant janelaFim,
    int totalAvaliacoes,
    Map<Urgencia, Long> quantidadePorUrgencia,
    Map<LocalDate, Long> quantidadePorDia,
    List<ItemRelatorio> amostragem
) {}
