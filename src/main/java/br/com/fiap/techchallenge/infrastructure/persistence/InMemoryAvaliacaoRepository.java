package br.com.fiap.techchallenge.infrastructure.persistence;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
@io.quarkus.arc.lookup.LookupIfProperty(name = "app.repository.in-memory", stringValue = "true")
public class InMemoryAvaliacaoRepository implements AvaliacaoRepository {

    private final Map<UUID, Avaliacao> storage = new ConcurrentHashMap<>();

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {
        storage.put(avaliacao.getId(), avaliacao);
        return avaliacao;
    }

    @Override
    public Optional<Avaliacao> buscarPorId(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Avaliacao> buscarDesde(Instant inicio) {
        return storage.values().stream()
                .filter(avaliacao -> !avaliacao.getDataEnvio().isBefore(inicio))
                .collect(Collectors.toList());
    }
}
