package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Połączono z serwerem " + host + ":" + port);

            try (OutputStream output = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(output, true);
                 InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 Scanner scanner = new Scanner(System.in)) {

                while (true) {
                    System.out.print("Wpisz wiadomość do wysłania (lub 'exit' aby zakończyć): ");
                    String message = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(message)) {
                        System.out.println("Zakończono połączenie z serwerem.");
                        break;
                    }

                    writer.println(message);

                    String response = reader.readLine();
                    if (response == null) {
                        System.out.println("Serwer zakończył połączenie.");
                        break;
                    }

                    System.out.println("Odpowiedź serwera: " + response);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
