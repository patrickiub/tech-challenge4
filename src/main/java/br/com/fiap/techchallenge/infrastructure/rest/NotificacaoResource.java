package br.com.fiap.techchallenge.infrastructure.rest;

import br.com.fiap.techchallenge.application.usecase.NotificarUrgenciaUseCase;
import br.com.fiap.techchallenge.domain.repository.AvaliacaoRepository;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/notificar")
public class NotificacaoResource {

    private final NotificarUrgenciaUseCase useCase;
    private final AvaliacaoRepository repository;

    public NotificacaoResource(NotificarUrgenciaUseCase useCase, AvaliacaoRepository repository) {
        this.useCase = useCase;
        this.repository = repository;
    }

    @POST
    @Path("/{id}")
    public Response notificarPorId(@PathParam("id") UUID id) {
        return repository.buscarPorId(id)
            .map(avaliacao -> {
                useCase.executar(avaliacao);
                return Response.accepted().build();
            })
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
