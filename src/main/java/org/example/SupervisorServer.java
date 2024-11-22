package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class SupervisorServer {
    public static void main(String[] args) {
        int[] serverPorts = {8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088};

        for (int port : serverPorts) {
            try (Socket socket = new Socket("localhost", port)) {
                System.out.println("Połączono z serwerem na porcie: " + port);


            } catch (IOException e) {
                System.err.println("Błąd komunikacji z serwerem na porcie " + port + ": " + e.getMessage());
            }
        }
    }
}
