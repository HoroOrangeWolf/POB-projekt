package org.example;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.PushGateway;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PrometheusServer {
    private static final CollectorRegistry registry = new CollectorRegistry();
    private static final String PROMETHEUS_GATEWAY_URL = "localhost:9091";

    private static final Counter messagesSent = Counter.build()
            .name("messages_sent_total")
            .help("Całkowita liczba wysłanych wiadomości")
            .register(registry);

    private static final Counter messagesSuccess = Counter.build()
            .name("messages_success_total")
            .help("Liczba pomyślnie wysłanych wiadomości")
            .labelNames("node")
            .register(registry);

    private static final Counter messagesError = Counter.build()
            .name("messages_error_total")
            .help("Liczba błędnych wiadomości")
            .labelNames("node")
            .register(registry);

    public static void incMessageSent() {
        messagesSent.inc();
    }

    public static void pushMessageSuccess(int port) {
        messagesSuccess.labels(String.valueOf(port)).inc();
    }

    public static void pushMessageError(int port) {
        messagesError.labels(String.valueOf(port)).inc();
    }

    public static void startAutomaticPush() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                PushGateway pg = new PushGateway(PROMETHEUS_GATEWAY_URL);
                pg.pushAdd(registry, "java_app");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);  // Co 5 sekund
    }
}