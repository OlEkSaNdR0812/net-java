package ua.edu.chmnu.net_dev.c4.udp.voice_chat;

import java.net.*;
import java.io.*;

public class UDPVoiceServer {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9876); // Відкриття порту
            System.out.println("Сервер запущено на порту 9876");

            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket); // Отримання даних від клієнта

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Відправка отриманих даних назад всім підключеним клієнтам
                DatagramPacket sendPacket = new DatagramPacket(
                        receivePacket.getData(),
                        receivePacket.getLength(),
                        clientAddress,
                        clientPort
                );
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
