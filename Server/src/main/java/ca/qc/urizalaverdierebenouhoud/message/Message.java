package ca.qc.urizalaverdierebenouhoud.message;

import ca.qc.urizalaverdierebenouhoud.message.serialization.LocalDateTimeTypeAdapter;
import ca.qc.urizalaverdierebenouhoud.users.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Message implements Comparable<Message> {
    private static final Logger messageLogger = Logger.getLogger(Message.class.getName());
    public static List<Message> messages = new ArrayList<>();

    private final Client author;
    private LocalDateTime time;
    private String content;

    public Client getAuthor() {
        return author;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    private String getFormattedTime() {
        return this.getTime().format(LocalDateTimeTypeAdapter.DISPLAY_DATE_TIME_FORMATTER);
    }

    /**
     *  Loads messages from a JSON file
     * @param file The JSON file to load messages from
     */
    public static void loadMessages(File file) {
        /*
            Define a Message as the following JSON Object:
            Reminder: a message is printed as [Utilisateur 1 - 132.207.29.107:46202 - 2017-10-13@13:02:01]: Salut Utilisateur 2 !
            {
                "author": {
                    "username": "cohenmaud",
                    "ipAddress": "192.168.0.2",
                    "port": 46202
                },
                "time": "2024-01-12T00:32:41",
                "content": "Salut Utilisateur 2 !"
            }
            To ensure good readability as well as compatibility with the LocalTime class, the time format is ISO-8601/ISO_LOCAL_DATE_TIME
            when saving to a file and yyyy-MM-dd@HH:mm:ss when displaying the time to the user.
         */
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        String fileContent;
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            attemptToParseJSON(file, gson, fileContent);
        } catch (IOException e) {
            Message.messageLogger.severe("File" + file.getAbsolutePath() + " couldn't be read");
            System.exit(1);
        }
    }

    private static void attemptToParseJSON(File file, Gson gson, String fileContent) {
        try {
            Message[] loadedMessages = gson.fromJson(fileContent, Message[].class);
            Message.messages = new ArrayList<>(Arrays.asList(loadedMessages));
        } catch (JsonSyntaxException jsonSyntaxException) {
            Message.messageLogger.severe("File" + file.getAbsolutePath() + " is not a valid JSON file");
            System.exit(1);
        }
    }

    /**
     *  Returns the last 15 messages, or less if there are less than 15 messages
     * @return The last 15 messages, or less if there are less than 15 messages
     */
    public static Message[] getUpToLast15Messages() {
        int numberOfMessages = Message.messages.size();
        int numberOfMessagesToReturn = Math.min(numberOfMessages, 15);
        Message[] messagesToReturn = new Message[numberOfMessagesToReturn];
        for (int i = 0; i < numberOfMessagesToReturn; i++) {
            messagesToReturn[i] = Message.messages.get(numberOfMessages - numberOfMessagesToReturn + i);
        }
        return messagesToReturn;
    }

    /**
     *  Saves messages to a JSON file
     * @param file The JSON file to save messages to
     */
    public static void saveMessages(File file) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(Message.messages);
        try {
            Files.writeString(Paths.get(file.getAbsolutePath()), json);
        } catch (IOException e) {
            Message.messageLogger.severe("File" + file.getAbsolutePath() + " couldn't be written");
            System.exit(1);
        }
    }
    @Override
    public String toString() {
        return "[" + this.getAuthor() + " - " + this.getFormattedTime() + "]: " + this.getContent();
    }

    public Message(Client author, LocalDateTime time, String content) {
        this.author = author;
        this.time = time;
        this.content = content;
    }

    @Override
    public int compareTo(Message otherMessage) {
        return this.getTime().compareTo(otherMessage.getTime());
    }
}
