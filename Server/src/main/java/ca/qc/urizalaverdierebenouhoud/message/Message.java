package ca.qc.urizalaverdierebenouhoud.message;

import ca.qc.urizalaverdierebenouhoud.users.Client;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Message {
    private ArrayList<Message> messages = new ArrayList<Message>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
    private Client author;
    private LocalDateTime dateTime;
    private String content;

    public Client getAuthor() {
        return author;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getContent() {
        return content;
    }

    private String getFormattedTime() {
        return this.getDateTime().format(DATE_TIME_FORMATTER);
    }

    public static void loadMessages() {
        // TODO Implement loadMessages method
        /*
            Define a Message as the following JSON Object:
            Reminder: a message is printed as [Utilisateur 1 - 132.207.29.107:46202 - 2017-10-13@13:02:01]: Salut Utilisateur 2 !
            {
                "author": {
                    "username": "cohenmaud",
                    "ipAddress": "192.168.0.2",
                    "port": 46202,
                }
                "time": "2024-01-12T00:32:41Z",
                "content": "Salut Utilisateur 2 !"
            }
            To ensure good readability as well as compatibility with the LocalTime class, the time format is ISO-8601.
         */
    }

    @Override
    public String toString() {
        return "[" + this.getAuthor() + " - " + this.getFormattedTime() + "]: " + this.getContent();
    }

    public Message(Client author, LocalDateTime dateTime, String content) {
        this.author = author;
        this.dateTime = dateTime;
        this.content = content;
    }
}
