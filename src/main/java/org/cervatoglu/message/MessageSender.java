package org.cervatoglu.message;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class MessageSender {
    protected final Properties properties;

    public MessageSender(Properties properties) {
        this.properties = properties;
    }

    public MessageSender(String propertiesPath) throws IOException {
        Properties properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream(propertiesPath)) {
            properties.load(inputStream);
        }
        this.properties = properties;
    }

    public final void send(Message message, MessageChannel channel) {
        switch (channel) {
            case MAIL -> sendViaMail(message);
            case SMS -> sendViaSMS(message);
            case WHATSAPP -> sendViaWhatsApp(message);
        }
    }

    protected void sendViaMail(Message message) {
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");
        String username = properties.getProperty("mail.smtp.username");
        String password = properties.getProperty("mail.smtp.password");

        Email email = EmailBuilder
                .startingBlank()
                .from(message.from())
                .to(message.to())
                .withSubject(message.subject())
                .withHTMLText(message.content())
                .withAttachments(message.attachments()
                        .stream()
                        .map(attachment -> new AttachmentResource(new File(attachment).getName(), new FileDataSource(attachment)))
                        .toList())
                .buildEmail();
        try (Mailer mailer = MailerBuilder
                .withSMTPServer(host, Integer.parseInt(port), username, password)
                .buildMailer()) {
            mailer.sendMail(email);
        } catch (Exception e) {
            throw new CannotSendMessageException(e);
        }
    }

    protected void sendViaSMS(Message message) {
        String username = properties.getProperty("twilio.username");
        String password = properties.getProperty("twilio.password");

        Twilio.init(username, password);
        try {
            if (message.attachments().isEmpty()) {
                com.twilio.rest.api.v2010.account.Message
                        .creator(new PhoneNumber(message.to()), new PhoneNumber(message.from()), message.content())
                        .create();
                return;
            }
            com.twilio.rest.api.v2010.account.Message
                    .creator(new PhoneNumber(message.to()), new PhoneNumber(message.from()), message.content())
                    .setMediaUrl(message.attachments().stream().map(URI::create).toList())
                    .create();
        } catch (Exception e) {
            throw new CannotSendMessageException(e);
        }
    }

    protected void sendViaWhatsApp(Message message) {
        String username = properties.getProperty("twilio.username");
        String password = properties.getProperty("twilio.password");

        Twilio.init(username, password);
        try {
            if (message.attachments().isEmpty()) {
                com.twilio.rest.api.v2010.account.Message
                        .creator(new PhoneNumber("whatsapp:".concat(message.to())), new PhoneNumber("whatsapp:".concat(message.from())), message.content())
                        .create();
                return;
            }
            com.twilio.rest.api.v2010.account.Message
                    .creator(new PhoneNumber("whatsapp:".concat(message.to())), new PhoneNumber("whatsapp:".concat(message.from())), message.content())
                    .setMediaUrl(message.attachments().stream().map(URI::create).toList())
                    .create();
        } catch (Exception e) {
            throw new CannotSendMessageException(e);
        }
    }
}
