package br.com.fiap.techchallenge.domain.notification;

import br.com.fiap.techchallenge.domain.model.Avaliacao;

public interface NotificadorUrgencia {
    void notificar(Avaliacao avaliacao);
}
