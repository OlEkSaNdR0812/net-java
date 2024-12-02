package ua.edu.chmnu.net_dev.c4.url.downloader_url;

import java.io.*;
import java.net.*;

public class SingleThreadDownloader {
    public static void main(String[] args) {
        String fileURL = "https://raw.githubusercontent.com/OlEkSaNdR0812/sport-shop/master/image-1.png";  // посилання на файл
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

        String fileName = getFileNameFromURL(fileURL, connection);
        System.out.println("Downloading file: " + fileName);

        try (InputStream inputStream = connection.getInputStream();
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveDir + "/" + fileName))) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("File downloaded successfully and saved as: " + saveDir + "/" + fileName);
    }

    private static String getFileNameFromURL(String fileURL, URLConnection connection) {
        String fileName = "";

        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null && disposition.contains("filename=")) {
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                fileName = disposition.substring(index + 10, disposition.length() - 1);
            }
        }
        if (fileName.isEmpty()) {
            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        }

        return fileName;
    }
}
