package ru.dfhub.eirc.eirc_paper.client;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.json.JSONObject;
import ru.dfhub.eirc.eirc_paper.Main;

/**
 * Class for working with data and processing it
 */
public class DataParser {

    /**
     * Types of incoming and outgoing messages
     */
    public enum MessageType {
        USER_MESSAGE("user_message"), // User text messages
        USER_SESSION("user_session"); // Messages about user join/leave

        private String fileName;

        MessageType(String fileName) {
            this.fileName = fileName;
        }

        private String getResourcesPath() {
            return "message_templates/%s.json".formatted(this.fileName);
        }

        public String getTemplate() {
            return new ResourcesReader(this.getResourcesPath()).readString().replace("\n", "");
        }
    }

    /**
     * Parse the incoming message and take the necessary action to work with it
     * @param data Raw data from server
     */
    public static void handleInputData(String data) {
        JSONObject dataObj;
        try {
            dataObj = new JSONObject(data);
        } catch (Exception e) { return; } // Null message from server


        switch (dataObj.getString("type")) {
            case "user-message" -> handleUserMessage(dataObj.getJSONObject("content"));
            case "user-session" -> handleUserSession(dataObj.getJSONObject("content"));
        }
    }

    /**
     * Collect a user message into a data type accepted by the client
     * @param message Message
     */
    public static void handleOutputMessage(String message, String username) {
        String template;
        try {
            template = MessageType.USER_MESSAGE.getTemplate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String encryptedMessage;
        try {
            encryptedMessage = Encryption.encrypt(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Main.getEircServer().handleUserMessage(template
            .replace("%user%", Main.getInstance().getConfig().getString("player-name-format").replace("<user>", username))
            .replace("%message%", encryptedMessage)
        , true);
    }

    /**
     * Process and send a message about your session (join/leave)
     * @param isJoin Is join
     */
    public static void handleOutputSession(boolean isJoin, String username) {
        String status = isJoin ? "join" : "leave";

        String template;
        try {
            template = MessageType.USER_SESSION.getTemplate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Main.getEircServer().handleUserMessage(template
                .replace("%user%", Main.getInstance().getConfig().getString("player-name-format").replace("<user>", username))
                .replace("%status%", status)
        , true);
    }

    /**
     * Processing an incoming user message
     * @param data Data's "content" object
     */
    private static void handleUserMessage(JSONObject data) {
        String sender = data.getString("user");
        String encryptedMessage = data.getString("message"); // In ftr, decrypt and handle decryption errors here

        String message;
        try {
            message = Encryption.decrypt(encryptedMessage);
        } catch (Exception e) {
            //Gui.showNewMessage("Failed to decrypt the incoming message! (wrong encryption key)", Gui.MessageType.SYSTEM_ERROR);
            e.printStackTrace();
            return;
        }

        String formattedMessage = Main.getInstance().getConfig().getString("message-format")
                .replace("<user>", sender)
                .replace("<message>", message);

        Main.showInGameMessage(
                MiniMessage.miniMessage().deserialize(formattedMessage)
        );
    }

    /**
     * Handle input user-session(join/leave) message and show it
     * @param data Data's "content" object
     */
    private static void handleUserSession(JSONObject data) {
        String user = data.getString("user");
        String status = data.getString("status").equals("join") ? "joined!" : "left.";

        String formattedMessage = Main.getInstance().getConfig().getString("session-format")
                .replace("<user>", user)
                .replace("<status>", status);

        Main.showInGameMessage(MiniMessage.miniMessage().deserialize(formattedMessage));
    }
}