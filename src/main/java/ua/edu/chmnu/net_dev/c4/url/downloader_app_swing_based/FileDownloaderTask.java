package ua.edu.chmnu.net_dev.c4.url.downloader_app_swing_based;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloaderTask implements Runnable {
    private final String fileURL;
    private final int rowIndex;
    private final DefaultTableModel tableModel;
    private static final String SAVE_DIR = "D:\\chmnu\\Network Programming";

    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    private int downloaded = 0;
    private int fileSize = 0;
    private long startTime = 0;

    public FileDownloaderTask(String fileURL, DefaultTableModel tableModel, int rowIndex) {
        this.fileURL = fileURL;
        this.tableModel = tableModel;
        this.rowIndex = rowIndex;

        // Ensure save directory exists
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
    }

    @Override
    public void run() {
        try {
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            File outputFile = new File(SAVE_DIR + File.separator + fileName);

            if (outputFile.exists()) {
                tableModel.setValueAt("Completed", rowIndex, 1);
                return;
            }

            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            fileSize = httpConn.getContentLength();
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            startTime = System.currentTimeMillis();

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                while (isPaused) {
                    SwingUtilities.invokeLater(() -> tableModel.setValueAt("Paused", rowIndex, 1));
                    Thread.sleep(100);
                }

                outputStream.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
                updateUI();
            }

            outputStream.close();
            inputStream.close();

            if ( !isPaused) {
                tableModel.setValueAt("Completed", rowIndex, 1);
            }
        } catch (IOException | InterruptedException e) {
            tableModel.setValueAt("Error", rowIndex, 1);
        }
    }

    private void updateUI() {
        long elapsedTime = System.currentTimeMillis() - startTime;

        if (elapsedTime > 0 && fileSize > 0) {
            long speed = downloaded * 1000L / elapsedTime;
            int progress = (int) ((downloaded * 100.0) / fileSize);

            SwingUtilities.invokeLater(() -> {
                tableModel.setValueAt(progress + "%", rowIndex, 2);
                tableModel.setValueAt(formatSpeed(speed), rowIndex, 3);
            });
        }
    }

    private String formatSpeed(long speed) {
        if (speed < 1024) {
            return speed + " B/s";
        } else if (speed < 1048576) {
            return (speed / 1024) + " KB/s";
        } else {
            return (speed / 1048576) + " MB/s";
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void resumeDownload() {
        isPaused = false;
        SwingUtilities.invokeLater(() -> tableModel.setValueAt("Downloading", rowIndex, 1)); // Оновлення статусу
    }

}
