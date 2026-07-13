package br.com.fiap.techchallenge.infrastructure.rest;

import br.com.fiap.techchallenge.infrastructure.rest.dto.AvaliacaoRequest;
import br.com.fiap.techchallenge.infrastructure.rest.dto.AvaliacaoResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.UUID;

@Path("/avaliacao")
public class AvaliacaoResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criar(@Valid AvaliacaoRequest request) {
        AvaliacaoResponse response = new AvaliacaoResponse(
                UUID.randomUUID(),
                request.descricao(),
                request.nota(),
                calcularUrgenciaMock(request.nota()),
                Instant.now()
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    private String calcularUrgenciaMock(Integer nota) {
        if (nota <= 3) return "CRITICA";
        if (nota <= 6) return "ALTA";
        return "NORMAL";
    }
}
