package ru.dfhub.eirc.eirc_paper.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import ru.dfhub.eirc.eirc_paper.client.DataParser;
import ru.dfhub.eirc.eirc_paper.client.ResourcesReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Server {

    private final ServerSocket server;
    private final ArrayList<UserHandler> users = new ArrayList<>();
    private final Logger logger = LogManager.getLogger(this);

    public Server(int port) throws IOException {
        server = new ServerSocket(port);
        new Thread(this::run).start();
    }

    private void run() {
        while (true) {
            try {
                Socket user = server.accept();
                UserHandler userHandler = new UserHandler(user);
                userHandler.start();
                users.add(userHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle user message (now just send it to every user)
     * @param message Message data
     * @throws ConcurrentModificationException Error sending message to disconnected user
     */
    public void handleUserMessage(String message, boolean isSelf) {
        users.forEach(user -> user.sendOutMessage(message));

        if (isSelf) return;
        DataParser.handleInputData(message); // Show message in game
    }

    /**
     * Check if input message is leave message
     * @param message Input message
     * @return Is leave message
     */
    public boolean isQuitMessage(String message) {
        if (message == null) return false; // Resolves closed Scanner(InputStream) null message
        JSONObject msg = new JSONObject(message);

        if (!msg.optString("type").equals("user-session")) return false;
        return msg.getJSONObject("content").getString("status").equals("leave");
    }

    /**
     * Remove user from users(receivers) list and stop user thread
     * @param userHandler UserHandler
     */
    public void disconnectUser(UserHandler userHandler) {
        users.remove(userHandler);
        userHandler.disconnect();
    }

    public void handleServerShutdown() {
        users.forEach(user ->
                user.sendOutMessage(new ResourcesReader("message_templates/server_shutdown.json").readString())
        );
    }

}
