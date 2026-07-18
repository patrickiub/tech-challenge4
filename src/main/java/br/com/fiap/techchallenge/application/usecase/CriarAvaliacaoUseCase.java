package br.com.fiap.techchallenge.application.usecase;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CriarAvaliacaoUseCase {

    private final AvaliacaoRepository repository;

    public CriarAvaliacaoUseCase(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public Avaliacao executar(String descricao, int nota) {
        Avaliacao avaliacao = new Avaliacao(descricao, nota);
        return repository.salvar(avaliacao);
    }
}
