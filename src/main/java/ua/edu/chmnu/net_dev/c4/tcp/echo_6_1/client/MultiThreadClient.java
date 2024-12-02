package ua.edu.chmnu.net_dev.c4.tcp.echo_6_1.client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadClient {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java MultiThreadClient <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int numberOfClients = 1000;
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        Random random = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            threadPool.execute(() -> {
                String randomString = generateRandomString(random, 10);
                try (Socket socket = new Socket(host, port)) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println(randomString);
                    String response = in.readLine();

                    System.out.println("Sent: " + randomString + " | Response: " + response);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
    }

    private static String generateRandomString(Random random, int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
