package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class TCPClient implements Runnable {
    private final List<Integer> children;
    private final int port;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public TCPClient(List<Integer> children, int port) {
        this.children = children;
        this.port = port;
    }

    @Override
    public void run() {
        log.info("TCP Client started on port {}", port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String securedMessage;
            while ((securedMessage = reader.readLine()) != null) {
                try {
                    String originalMessage = securedMessage;

                    double nodeProbability = SimpleConfigApp.getNodeProbability(this.port);
                    ErrorType nodeErrorType = SimpleConfigApp.getNodeErrorType(this.port);
                    securedMessage = DisturbMessage.disturbMessage(securedMessage, nodeProbability, nodeErrorType);

                    PrometheusServer.incrementBergerVerificationCount(port);
                    String parsedMessage = BergerCode.parseMessage(securedMessage);
                    PrometheusServer.incrementBergerSuccessiveVerification(port);

                    log.info("Server na porcie {} odebrał: {}", port, parsedMessage);
                    forwardToChildren(securedMessage);
                    writer.println("SUCCESS");

                    if (!originalMessage.equals(securedMessage)) {
                        PrometheusServer.incrementBergerVerificationError(this.port);
                    }
                    break;
                } catch (InvalidBergerCodeException e) {
                    log.error("Failed to parse message", e);
                    writer.println("ERROR");
                }
            }

            socket.close();
        } catch (IOException e) {
            log.error("Błąd obsługi klienta", e);
        }
    }

    private void forwardToChildren(String message) {
        children.stream()
                .map(childPort -> new MessageSenderWrapper(message, port, childPort))
                .map(executor::submit)
                .forEach(future -> {
                    try {
                        // Czeka na potwierdzenie wiadomości
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
