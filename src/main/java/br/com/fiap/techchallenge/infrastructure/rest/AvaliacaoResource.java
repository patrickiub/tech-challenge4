package br.com.fiap.techchallenge.infrastructure.rest;

import br.com.fiap.techchallenge.application.usecase.CriarAvaliacaoUseCase;
import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.infrastructure.rest.dto.AvaliacaoRequest;
import br.com.fiap.techchallenge.infrastructure.rest.dto.AvaliacaoResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/avaliacao")
public class AvaliacaoResource {

    private final CriarAvaliacaoUseCase useCase;

    public AvaliacaoResource(CriarAvaliacaoUseCase useCase) {
        this.useCase = useCase;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criar(@Valid AvaliacaoRequest request) {
        Avaliacao avaliacao = useCase.executar(request.descricao(), request.nota());
        AvaliacaoResponse response = new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getUrgencia().name(),
                avaliacao.getDataEnvio()
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
