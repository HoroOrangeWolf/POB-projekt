package org.example;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SupervisorServer {
    private static final int[] superVisorConnectedNodes = {0, 1};
    // 7 Serwerów połączonych z Supervisorem łącznie 8
    private static final int[] serverPorts = {8081, 8082, 8083, 8084, 8085, 8086, 8087};
    private static final Map<Integer, List<Integer>> treeTopology = new HashMap<>();

    static {
        // Topologia
        // Supervisor -> 8081, 8082
//        treeTopology.put(0, List.of(0, 1));
        // 8081 ->  8084
        treeTopology.put(0, List.of(3));
        // 8082 -> 8085, 8086
        treeTopology.put(1, List.of(4, 5));
        // 8084 -> 8087
        treeTopology.put(3, List.of(6));
        // 8086 -> 8083
        treeTopology.put(6, List.of(2));
    }

    public static String generateMessage() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            stringBuilder.append(random.nextInt(2));
        }

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        PrometheusServer.startAutomaticPush();

        for (int i = 0; i < serverPorts.length; i++) {
            int port = serverPorts[i];
            List<Integer> childrenNodes = Optional.ofNullable(treeTopology.get(i))
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(index -> serverPorts[index])
                    .toList();

            TCPClient tcpClient = new TCPClient(childrenNodes, port);
            new Thread(tcpClient).start();
        }

        waitForServerToStart();
        sendRandomMessageToChildren();
    }

    private static void waitForServerToStart() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        Arrays.stream(serverPorts).forEach(port -> {
//            while (true) {
//                try (Socket serverSocket = new Socket("localhost", port)) {
//                    serverSocket.close();
//                    return;
//                } catch (IOException _) {
//                }
//            }
//        });
    }

    private static void sendRandomMessageToChildren() {
        String message = generateMessage();

        String bergerCode = BergerCode.encode(message);

        String securedMessage = message + bergerCode;

        PrometheusServer.incMessageSent();

        for (int targetPortIndex : superVisorConnectedNodes) {
            int serverPort = serverPorts[targetPortIndex];

            MessageSenderWrapper serverWrapper = new MessageSenderWrapper(securedMessage, serverPort);
            new Thread(serverWrapper).start();
        }
    }
}
