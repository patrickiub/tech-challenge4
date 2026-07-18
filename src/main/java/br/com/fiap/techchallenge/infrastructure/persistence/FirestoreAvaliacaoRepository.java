package br.com.fiap.techchallenge.infrastructure.persistence;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.model.Urgencia;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
public class FirestoreAvaliacaoRepository implements AvaliacaoRepository {

    private static final String COLLECTION = "avaliacoes";

    private final Firestore firestore;

    public FirestoreAvaliacaoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("id", avaliacao.getId().toString());
        dados.put("descricao", avaliacao.getDescricao());
        dados.put("nota", avaliacao.getNota());
        dados.put("urgencia", avaliacao.getUrgencia().name());
        dados.put("dataEnvio", avaliacao.getDataEnvio().toString());

        try {
            firestore.collection(COLLECTION).document(avaliacao.getId().toString()).set(dados).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }

        return avaliacao;
    }

    @Override
    public Optional<Avaliacao> buscarPorId(UUID id) {
        try {
            DocumentSnapshot snapshot = firestore.collection(COLLECTION).document(id.toString()).get().get();
            if (!snapshot.exists()) {
                return Optional.empty();
            }
            return Optional.of(paraAvaliacao(snapshot));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public List<Avaliacao> buscarDesde(Instant inicio) {
        try {
            QuerySnapshot snapshot = firestore.collection(COLLECTION)
                    .whereGreaterThanOrEqualTo("dataEnvio", inicio.toString())
                    .get()
                    .get();

            return snapshot.getDocuments().stream()
                    .map(this::paraAvaliacao)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private Avaliacao paraAvaliacao(DocumentSnapshot snapshot) {
        return new Avaliacao(
                UUID.fromString((String) snapshot.get("id")),
                (String) snapshot.get("descricao"),
                ((Long) snapshot.get("nota")).intValue(),
                Urgencia.valueOf((String) snapshot.get("urgencia")),
                Instant.parse((String) snapshot.get("dataEnvio"))
        );
    }
}
