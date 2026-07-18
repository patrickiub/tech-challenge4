package br.com.fiap.techchallenge.infrastructure.rest;

import br.com.fiap.techchallenge.application.usecase.GerarRelatorioSemanalUseCase;
import br.com.fiap.techchallenge.domain.model.RelatorioSemanal;
import br.com.fiap.techchallenge.infrastructure.notification.EnviarRelatorioService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/relatorio-semanal")
public class RelatorioResource {

    private static final Logger LOG = Logger.getLogger(RelatorioResource.class);

    private final GerarRelatorioSemanalUseCase gerar;
    private final EnviarRelatorioService enviar;
    private final String tokenEsperado;

    public RelatorioResource(
            GerarRelatorioSemanalUseCase gerar,
            EnviarRelatorioService enviar,
            @ConfigProperty(name = "app.relatorio.token") String tokenEsperado) {
        this.gerar = gerar;
        this.enviar = enviar;
        this.tokenEsperado = tokenEsperado;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response executar(@HeaderParam("X-Relatorio-Token") String tokenRecebido) {
        if (tokenRecebido == null || !tokenRecebido.equals(tokenEsperado)) {
            LOG.warn("tentativa de acesso ao relatório com token inválido");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        RelatorioSemanal relatorio = gerar.executar();
        enviar.enviar(relatorio);

        return Response.ok(Map.of(
            "status", "enviado",
            "totalAvaliacoes", relatorio.totalAvaliacoes(),
            "janelaInicio", relatorio.janelaInicio().toString(),
            "janelaFim", relatorio.janelaFim().toString()
        )).build();
    }
}
