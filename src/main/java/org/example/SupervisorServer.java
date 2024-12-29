package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class SupervisorServer {
    private static final Integer[] superVisorConnectedNodes = {0, 1};
    // 7 Serwerów połączonych z Supervisorem łącznie 8
    private static final Integer[] serverPorts = {8081, 8082, 8083, 8084, 8085, 8086, 8087};
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

        for (int i = 0; i < 16; i++) {
            stringBuilder.append(random.nextInt(2));
        }

        return stringBuilder.toString();
    }

    @SneakyThrows
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

        SimpleConfigApp.launchConfigApp();
        waitForServerToStart();

        List<Thread> allThreads = new LinkedList<>();

        while (true) {
            allThreads = allThreads.stream()
                    .filter(Thread::isAlive)
                    .collect(Collectors.toCollection(LinkedList::new));

            if (allThreads.size() >= SimpleConfigApp.getMaxActiveMessages()) {
                continue;
            }

            List<Thread> threadList = sendRandomMessageToChildren();

            allThreads.addAll(threadList);

            TimeUnit.MILLISECONDS.sleep(1000 / SimpleConfigApp.getMessageRate());
        }
    }

    private static void waitForServerToStart() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Thread> sendRandomMessageToChildren() {
        String message = generateMessage();

        String bergerCode = BergerCode.encode(message);

        String securedMessage = message + bergerCode;

        PrometheusServer.incMessageSent();

        List<Thread> readyThreads = Arrays.stream(superVisorConnectedNodes)
                .map(targetPort -> serverPorts[targetPort])
                .map(serverPort -> new MessageSenderWrapper(securedMessage, 8080, serverPort, true))
                .map(Thread::new)
                .toList();

        readyThreads.forEach(Thread::start);

        return readyThreads;
    }
}
