package org.cervatoglu.message;

import java.util.List;

public class Message {
    private final String from;
    private final String to;
    private final String subject;
    private final String content;
    private final List<String> attachments;

    public Message(String from, String to, String subject, String content, List<String> attachments) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.attachments = attachments;
    }

    public Message(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.attachments = List.of();
    }

    public String from() {
        return from;
    }

    public String to() {
        return to;
    }

    public String subject() {
        return subject;
    }

    public String content() {
        return content;
    }

    public List<String> attachments() {
        return attachments;
    }
}
