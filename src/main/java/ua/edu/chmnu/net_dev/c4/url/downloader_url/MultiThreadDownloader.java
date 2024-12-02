package ua.edu.chmnu.net_dev.c4.url.downloader_url;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadDownloader {
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        String fileURL = "https://raw.githubusercontent.com/OlEkSaNdR0812/sport-shop/master/README.md";  // посилання на файл
        String saveDir = "D:\\chmnu\\Network Programming";  // директорія для збереження файлу
        try {
            downloadFile(fileURL, saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        URLConnection connection = url.openConnection();
        connection.connect();

        String fileName = getFileNameFromURL(fileURL, connection);

        int fileSize = connection.getContentLength();

        int partSize = fileSize / NUM_THREADS;

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        List<byte[]> fileParts = new CopyOnWriteArrayList<>();  // Список для зберігання частин файлу в пам'яті

        for (int i = 0; i < NUM_THREADS; i++) {
            int startByte = i * partSize;
            int endByte = (i == NUM_THREADS - 1) ? fileSize : startByte + partSize - 1;
            executorService.submit(new DownloadTask(url, startByte, endByte, i, fileParts));
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
        }

        combineFileParts(saveDir, fileName, fileParts);
    }

    private static String getFileNameFromURL(String fileURL, URLConnection connection) {
        String fileName = "";

        // Спроба отримати ім'я з Content-Disposition
        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null && disposition.contains("attachment")) {
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                fileName = disposition.substring(index + 10, disposition.length() - 1);
            }
        }

        // Якщо не вдалося отримати ім'я з заголовків, використовуємо частину URL
        if (fileName.isEmpty()) {
            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        }

        return fileName;
    }

    private static void combineFileParts(String saveDir, String fileName, List<byte[]> fileParts) throws IOException {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveDir + "/" + fileName))) {
            for (byte[] part : fileParts) {
                outputStream.write(part);
            }
        }
        System.out.println("File downloaded successfully and saved as " + saveDir + "/" + fileName);
    }
}

class DownloadTask implements Runnable {
    private URL url;
    private int startByte;
    private int endByte;
    private int partNumber;
    private List<byte[]> fileParts;

    public DownloadTask(URL url, int startByte, int endByte, int partNumber, List<byte[]> fileParts) {
        this.url = url;
        this.startByte = startByte;
        this.endByte = endByte;
        this.partNumber = partNumber;
        this.fileParts = fileParts;
    }

    @Override
    public void run() {
        try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream())) {

            byte[] buffer = new byte[4096];
            inputStream.skip(startByte);

            ByteArrayOutputStream partStream = new ByteArrayOutputStream();
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1 && totalBytesRead < (endByte - startByte + 1)) {
                partStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            byte[] partData = partStream.toByteArray();
            fileParts.add(partData);

            System.out.println("Part " + partNumber + " downloaded. From byte " + startByte + " to byte " + endByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

