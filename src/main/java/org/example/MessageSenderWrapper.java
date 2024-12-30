package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageSenderWrapper implements Runnable {
    private final String securedMessage;
    private final int sourcePort;
    private final int targetPort;
    private final boolean recordTime;
    private static final String CODE_ERROR = "ERROR";
    private static final String CODE_SUCCESS = "SUCCESS";

    public MessageSenderWrapper(String securedMessage, int sourcePort, int port, boolean recordTime) {
        this.securedMessage = securedMessage;
        this.sourcePort = sourcePort;
        this.targetPort = port;
        this.recordTime = recordTime;
    }

    public MessageSenderWrapper(String securedMessage, int sourcePort, int port) {
        this.securedMessage = securedMessage;
        this.sourcePort = sourcePort;
        this.targetPort = port;
        this.recordTime = false;
    }

    @SneakyThrows
    private Socket createSocket() {
        while (true) {
            try {
                return new Socket("localhost", targetPort);
            } catch (Exception e) {
                TimeUnit.MILLISECONDS.sleep(50);
            }
        }
    }

    @Override
    public void run() {
        Date now = new Date();

        Socket targetServer = createSocket();
        PrometheusServer.incrementActiveConnections();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(targetServer.getInputStream()));
             PrintWriter writer = new PrintWriter(targetServer.getOutputStream(), true)) {

            log.info("First attempt to send message to {}", targetServer.getPort());
            writer.println(securedMessage);

            String message;

            while ((message = reader.readLine()) != null) {
                if (CODE_SUCCESS.equals(message)) {
                    log.info("Message {} was successfully processed", message);
                    break;
                } else if (CODE_ERROR.equals(message)) {
                    PrometheusServer.incrementRetransmission(this.sourcePort);
                    log.warn("Client server failed to deliver message: {} to server on port {}, attempting to resend", message, targetServer.getPort());
                    writer.println(securedMessage);
                }
            }

            targetServer.close();
        } catch (IOException _) {
        } finally {
            PrometheusServer.decrementActiveConnections();
        }

        Date last = new Date();

        if (recordTime) {
            long diff = last.getTime() - now.getTime();
            PrometheusServer.recordPropagationTime(diff);
        }
    }
}
