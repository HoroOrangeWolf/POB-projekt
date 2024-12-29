package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class MessageSenderWrapper implements Runnable {
    private final String securedMessage;
    private final Socket targetServer;
    private static final String CODE_ERROR = "ERROR";
    private static final String CODE_SUCCESS = "SUCCESS";

    public MessageSenderWrapper(String securedMessage, int port) {
        this.securedMessage = securedMessage;
        try {
            this.targetServer = new Socket("localhost", port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(targetServer.getInputStream()));
             PrintWriter writer = new PrintWriter(targetServer.getOutputStream(), true)) {

            log.info("First attempt to send message to {}", targetServer.getPort());
            writer.println(securedMessage);

            String message;

            while ((message = reader.readLine()) != null) {
                if (CODE_SUCCESS.equals(message)) {
                    log.info("Message {} was successfully processed", message);
                    return;
                } else if (CODE_ERROR.equals(message)) {
                    log.warn("Client server failed to deliver message: {} to server on port {}, attempting to resend", message, targetServer.getPort());
                    writer.println(message);
                }
            }
        } catch (IOException _) {
        }
    }
}
