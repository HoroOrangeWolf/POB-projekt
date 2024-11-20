package org.example;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serwer nasłuchuje na porcie " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nowe połączenie z: " + socket.getInetAddress().getHostAddress());

                try (InputStream input = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                     OutputStream output = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(output, true)) {

                    String message = reader.readLine();
                    System.out.println("Otrzymano: " + message);

                    String response = "Odebrano: " + message;
                    writer.println(response);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
