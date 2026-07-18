package br.com.fiap.techchallenge.infrastructure.notification;

import br.com.fiap.techchallenge.domain.model.ItemRelatorio;
import br.com.fiap.techchallenge.domain.model.RelatorioSemanal;
import br.com.fiap.techchallenge.domain.model.Urgencia;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@ApplicationScoped
public class EnviarRelatorioService {

    private static final Logger LOG = Logger.getLogger(EnviarRelatorioService.class);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA_HORA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZONA);
    private static final DateTimeFormatter APENAS_DATA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZONA);

    private final Mailer mailer;
    private final String destinatario;

    public EnviarRelatorioService(
            Mailer mailer,
            @ConfigProperty(name = "app.relatorio.destinatario") String destinatario) {
        this.mailer = mailer;
        this.destinatario = destinatario;
    }

    public void enviar(RelatorioSemanal relatorio) {
        try {
            String assunto = String.format("[Relatório Semanal] %d avaliações", relatorio.totalAvaliacoes());
            String html = construirHtml(relatorio);
            mailer.send(Mail.withHtml(destinatario, assunto, html));
            LOG.infof("relatório semanal enviado para %s (%d avaliações)", destinatario, relatorio.totalAvaliacoes());
        } catch (Exception e) {
            LOG.errorf(e, "falha ao enviar relatório semanal");
            throw new RuntimeException("falha ao enviar relatório", e);
        }
    }

    private String construirHtml(RelatorioSemanal r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Arial,sans-serif;max-width:700px;margin:auto'>");
        sb.append("<h2>Relatório Semanal de Feedbacks</h2>");
        sb.append("<p><strong>Período:</strong> ")
          .append(DATA_HORA.format(r.janelaInicio())).append(" até ")
          .append(DATA_HORA.format(r.janelaFim())).append("</p>");
        sb.append("<p><strong>Total de avaliações:</strong> ").append(r.totalAvaliacoes()).append("</p>");

        sb.append("<h3>Quantidade por urgência</h3><ul>");
        for (Urgencia u : Urgencia.values()) {
            long qtd = r.quantidadePorUrgencia().getOrDefault(u, 0L);
            sb.append("<li>").append(u.name()).append(": ").append(qtd).append("</li>");
        }
        sb.append("</ul>");

        sb.append("<h3>Quantidade por dia</h3><ul>");
        r.quantidadePorDia().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> sb.append("<li>")
                .append(APENAS_DATA.format(e.getKey().atStartOfDay(ZONA).toInstant()))
                .append(": ").append(e.getValue()).append("</li>"));
        sb.append("</ul>");

        sb.append("<h3>Amostragem (últimas ").append(r.amostragem().size()).append(")</h3>");
        sb.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse;width:100%'>");
        sb.append("<tr><th>Data</th><th>Urgência</th><th>Descrição</th></tr>");
        for (ItemRelatorio item : r.amostragem()) {
            sb.append("<tr>")
              .append("<td>").append(DATA_HORA.format(item.dataEnvio())).append("</td>")
              .append("<td>").append(item.urgencia().name()).append("</td>")
              .append("<td>").append(escapeHtml(item.descricao())).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
