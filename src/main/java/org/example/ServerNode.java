package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNode implements Runnable {
    private final int port;

    public ServerNode(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serwer działa na porcie: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Otrzymano połączenie na porcie " + port);


            }
        } catch (IOException e) {
            System.err.println("Błąd na serwerze na porcie " + port + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Podaj port jako argument");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new Thread(new ServerNode(port)).start();
    }
}
