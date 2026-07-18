package br.com.fiap.techchallenge.application.usecase;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.model.ItemRelatorio;
import br.com.fiap.techchallenge.domain.model.RelatorioSemanal;
import br.com.fiap.techchallenge.domain.model.Urgencia;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GerarRelatorioSemanalUseCase {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final int TAMANHO_AMOSTRAGEM = 20;

    private final AvaliacaoRepository repository;
    private final int diasJanela;

    public GerarRelatorioSemanalUseCase(
            AvaliacaoRepository repository,
            @ConfigProperty(name = "app.relatorio.dias-janela", defaultValue = "7") int diasJanela) {
        this.repository = repository;
        this.diasJanela = diasJanela;
    }

    public RelatorioSemanal executar() {
        Instant fim = Instant.now();
        Instant inicio = fim.minus(diasJanela, ChronoUnit.DAYS);

        List<Avaliacao> avaliacoes = repository.buscarDesde(inicio);

        Map<Urgencia, Long> porUrgencia = avaliacoes.stream()
            .collect(Collectors.groupingBy(Avaliacao::getUrgencia, Collectors.counting()));

        Map<LocalDate, Long> porDia = avaliacoes.stream()
            .collect(Collectors.groupingBy(
                a -> a.getDataEnvio().atZone(ZONA).toLocalDate(),
                Collectors.counting()
            ));

        List<ItemRelatorio> amostragem = avaliacoes.stream()
            .sorted((a, b) -> b.getDataEnvio().compareTo(a.getDataEnvio()))
            .limit(TAMANHO_AMOSTRAGEM)
            .map(a -> new ItemRelatorio(a.getDescricao(), a.getUrgencia(), a.getDataEnvio()))
            .toList();

        return new RelatorioSemanal(
            Instant.now(),
            inicio,
            fim,
            avaliacoes.size(),
            porUrgencia,
            porDia,
            amostragem
        );
    }
}
