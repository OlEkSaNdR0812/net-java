package ua.edu.chmnu.net_dev.c4.tcp.echo_6_1.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EchoServer {
    public static void main(String[] args) throws IOException {

        String host = "localhost";
        int port = 7707;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }


        ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host));
        System.out.println("Server started on " + host + ":" + port);

        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            threadPool.execute(() -> handleClient(clientSocket));
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String input = in.readLine();
            long startTime = System.nanoTime();
            String output = input; // Обробка даних
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) ;
            out.println(output + " | Duration: " + duration + " ns");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

