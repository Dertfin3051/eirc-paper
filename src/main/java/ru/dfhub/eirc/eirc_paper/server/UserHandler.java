package ru.dfhub.eirc.eirc_paper.server;

import org.apache.logging.log4j.LogManager;
import ru.dfhub.eirc.eirc_paper.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that contains everything needed to work with a user,
 * including its socket, input/output paths, and message receiving loop.
 */
public class UserHandler extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    private boolean disconnected = false;

    /**
     * Creating a new user
     * @param socket User socket
     */
    public UserHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Cycle of receiving and processing user messages
     */
    @Override
    public void run() {
        while (!disconnected) {
            try {
                String inputMessage = in.readLine();
                Main.getEircServer().handleUserMessage(inputMessage, false); // Handle new message
                if (Main.getEircServer().isQuitMessage(inputMessage)) Main.getEircServer().disconnectUser(this);
            } catch (IOException e)
            {
                Main.logger().log(Level.WARNING, "An error occurred while retrieving user message (%s)".formatted(e.getMessage()));
            } catch (ConcurrentModificationException e) {} // Ignore this XD
        }
    }

    /**
     * Send message from server to user
     * @param message Message
     */
    public void sendOutMessage(String message) {
        out.println(message);
    }

    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
            disconnected = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
