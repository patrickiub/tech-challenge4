package br.com.fiap.techchallenge.application.usecase;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.model.Urgencia;
import br.com.fiap.techchallenge.domain.notification.NotificadorUrgencia;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NotificarUrgenciaUseCase {

    private static final Logger LOG = Logger.getLogger(NotificarUrgenciaUseCase.class);

    private final NotificadorUrgencia notificador;

    public NotificarUrgenciaUseCase(NotificadorUrgencia notificador) {
        this.notificador = notificador;
    }

    public boolean executar(Avaliacao avaliacao) {
        if (avaliacao.getUrgencia() != Urgencia.CRITICA) {
            LOG.debugf("avaliação %s não é crítica, notificação ignorada", avaliacao.getId());
            return false;
        }
        notificador.notificar(avaliacao);
        return true;
    }
}
