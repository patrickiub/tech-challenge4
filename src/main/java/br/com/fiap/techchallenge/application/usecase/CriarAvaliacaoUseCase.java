package br.com.fiap.techchallenge.application.usecase;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class CriarAvaliacaoUseCase {

    private static final Logger LOG = Logger.getLogger(CriarAvaliacaoUseCase.class);

    private final AvaliacaoRepository repository;
    private final NotificarUrgenciaUseCase notificar;

    public CriarAvaliacaoUseCase(AvaliacaoRepository repository, NotificarUrgenciaUseCase notificar) {
        this.repository = repository;
        this.notificar = notificar;
    }

    public Avaliacao executar(String descricao, int nota) {
        Avaliacao avaliacao = new Avaliacao(descricao, nota);
        Avaliacao salva = repository.salvar(avaliacao);

        // fire-and-forget: notificação não bloqueia o fluxo principal
        CompletableFuture.runAsync(() -> notificar.executar(salva))
            .exceptionally(ex -> {
                LOG.errorf(ex, "erro no fluxo assíncrono de notificação para %s", salva.getId());
                return null;
            });

        return salva;
    }
}
