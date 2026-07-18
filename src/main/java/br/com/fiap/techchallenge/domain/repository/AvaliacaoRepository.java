package br.com.fiap.techchallenge.domain.repository;

import br.com.fiap.techchallenge.domain.model.Avaliacao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoRepository {

    Avaliacao salvar(Avaliacao avaliacao);

    Optional<Avaliacao> buscarPorId(UUID id);

    List<Avaliacao> buscarDesde(Instant inicio);
}
