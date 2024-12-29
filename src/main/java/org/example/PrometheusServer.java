package org.example;

import io.prometheus.client.*;
import io.prometheus.client.exporter.PushGateway;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PrometheusServer {
    private static final CollectorRegistry registry = new CollectorRegistry();
    private static final String PROMETHEUS_GATEWAY_URL = "localhost:9091";

    private static final Summary propagationTime = Summary.build()
            .name("propagation_time_milliseconds")
            .help("Średni czas propagacji wiadomości w milisekundach")
            .register(registry);

    private static final Counter messagesSent = Counter.build()
            .name("messages_sent_total")
            .help("Całkowita liczba wysłanych wiadomości")
            .register(registry);

    private static final Counter retransmissions = Counter.build()
            .name("retransmissions_total")
            .help("Całkowita liczba retransmisji")
            .register(registry);

    private static final Gauge activeConnections = Gauge.build()
            .name("active_connections")
            .help("Liczba aktywnych połączeń")
            .register(registry);

    private static final Counter bergerVerificationSuccess = Counter.build()
            .name("berger_verification_success_total")
            .help("Całkowita liczba poprawnych weryfikacji kodu Bergera")
            .labelNames("node")
            .register(registry);

    public static void incrementBergerVerificationSuccess(int port) {
        bergerVerificationSuccess.labels(String.valueOf(port)).inc();
    }

    public static void incrementActiveConnections() {
        activeConnections.inc();
    }

    public static void decrementActiveConnections() {
        activeConnections.dec();
    }

    public static void incrementRetransmission() {
        retransmissions.inc();
    }

    public static void incMessageSent() {
        messagesSent.inc();
    }


    public static void recordPropagationTime(double milliseconds) {
        propagationTime.observe(milliseconds);
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