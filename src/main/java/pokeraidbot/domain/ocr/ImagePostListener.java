package pokeraidbot.domain.ocr;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.ocr.OCRAnswer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

import static pokeraidbot.ocr.EggAnalyzer.analyzeImage;

public class ImagePostListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePostListener.class);

    @Override
    public void onEvent(Event event) {
        LOGGER.info("Entering the image post listener");
        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent guildMessageReactionEvent = (GuildMessageReceivedEvent) event;
            List<Message.Attachment> attachments = guildMessageReactionEvent.getMessage().getAttachments();
            for (Message.Attachment attachment : attachments) {
                try {

                    if (attachment.getSize()<3000000) {
                        final URL url = new URL(attachment.getUrl());
                        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
                        final BufferedImage image = ImageIO.read(connection.getInputStream());
                        OCRAnswer ocrAnswer = analyzeImage(image, attachment.getFileName());
                        MessageBuilder messageBuilder = new MessageBuilder();
                        messageBuilder.append(ocrAnswer);
                        switch (ocrAnswer.getType()) {
                            case EGG:
                                LocalTime time = LocalTime.parse(ocrAnswer.getTime());
                                String[] countdown = ocrAnswer.getCountdown().split(":");
                                Duration toStart = Duration.parse(MessageFormat.format("PT{0}H{1}M", countdown[0], countdown[1], countdown[2]));
                                LocalTime end = time.plus(toStart).plusHours(1L).plusMinutes(1L);
                                String template = "!raid new [Some pokemon] %2d:%2d %s";
                                String result = String.format(template, end.getHour(), end.getMinute(), ocrAnswer.getGymName());
                                messageBuilder.append("\n");
                                messageBuilder.append(result);
                                break;
                            case BOSS:

                                break;
                        }
                        guildMessageReactionEvent.getChannel().sendMessage(messageBuilder.build()).queue();
                    }
                } catch (java.io.IOException e) {
                    LOGGER.warn("Got faulty URL from discord: " + attachment.getUrl());
                }
                LOGGER.info("URL:" + attachment.getUrl());
            }
        }
    }
}
