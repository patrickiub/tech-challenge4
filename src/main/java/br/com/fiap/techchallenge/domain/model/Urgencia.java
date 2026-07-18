package br.com.fiap.techchallenge.domain.model;

public enum Urgencia {
    CRITICA,
    ALTA,
    NORMAL;

    public static Urgencia paraNota(int nota) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("nota deve estar entre 0 e 10");
        }
        if (nota <= 3) return CRITICA;
        if (nota <= 6) return ALTA;
        return NORMAL;
    }
}
