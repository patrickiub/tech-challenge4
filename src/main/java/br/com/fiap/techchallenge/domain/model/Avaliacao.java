package br.com.fiap.techchallenge.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Avaliacao {

    private final UUID id;
    private final String descricao;
    private final int nota;
    private final Urgencia urgencia;
    private final Instant dataEnvio;

    public Avaliacao(String descricao, int nota) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao não pode ser vazia");
        }
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("nota deve estar entre 0 e 10");
        }
        this.id = UUID.randomUUID();
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = Urgencia.paraNota(nota);
        this.dataEnvio = Instant.now();
    }

    public Avaliacao(UUID id, String descricao, int nota, Urgencia urgencia, Instant dataEnvio) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNota() {
        return nota;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public Instant getDataEnvio() {
        return dataEnvio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Avaliacao)) return false;
        Avaliacao that = (Avaliacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avaliacao{id=" + id + ", nota=" + nota + ", urgencia=" + urgencia + "}";
    }
}
