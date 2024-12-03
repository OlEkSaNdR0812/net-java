package ua.edu.chmnu.net_dev.c4.udp.voice_chat;

import java.net.*;
import javax.sound.sampled.*;

public class UDPVoiceClient {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        TargetDataLine microphone = null;
        SourceDataLine speakers = null;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 9876;

            // Налаштування мікрофона для запису аудіо
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            // Налаштування динаміків для відтворення звуку
            DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speakers.open(format);
            speakers.start();

            byte[] data = new byte[1024];
            while (true) {
                // Запис аудіо з мікрофона
                int bytesRead = microphone.read(data, 0, data.length);

                // Відправка аудіо пакету на сервер
                DatagramPacket packet = new DatagramPacket(data, bytesRead, serverAddress, serverPort);
                socket.send(packet);

                // Отримання пакету від сервера (голос інших клієнтів)
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                socket.receive(receivePacket);

                // Відтворення аудіо через динаміки
                speakers.write(receivePacket.getData(), 0, receivePacket.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (microphone != null) {
                microphone.close();
            }
            if (speakers != null) {
                speakers.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
