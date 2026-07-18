package br.com.fiap.techchallenge.infrastructure.notification;

import br.com.fiap.techchallenge.domain.model.Avaliacao;
import br.com.fiap.techchallenge.domain.notification.NotificadorUrgencia;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@ApplicationScoped
public class MailtrapNotificador implements NotificadorUrgencia {

    private static final Logger LOG = Logger.getLogger(MailtrapNotificador.class);
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.of("America/Sao_Paulo"));

    private final Mailer mailer;
    private final String destinatario;

    public MailtrapNotificador(
            Mailer mailer,
            @ConfigProperty(name = "app.notification.destination", defaultValue = "admin@feedback-service.tech") String destinatario) {
        this.mailer = mailer;
        this.destinatario = destinatario;
    }

    @Override
    public void notificar(Avaliacao avaliacao) {
        try {
            String assunto = "[URGENTE] Feedback crítico recebido";
            String corpo = construirCorpo(avaliacao);
            mailer.send(Mail.withText(destinatario, assunto, corpo));
            LOG.infof("notificação enviada para %s referente à avaliação %s", destinatario, avaliacao.getId());
        } catch (Exception e) {
            LOG.errorf(e, "falha ao enviar notificação para avaliação %s", avaliacao.getId());
        }
    }

    private String construirCorpo(Avaliacao avaliacao) {
        return String.format("""
                Um feedback com urgência %s foi registrado.

                Descrição: %s
                Nota: %d
                Data de envio: %s
                ID: %s
                """,
                avaliacao.getUrgencia().name(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                FORMATTER.format(avaliacao.getDataEnvio()),
                avaliacao.getId());
    }
}
