package ua.edu.chmnu.net_dev.c4.url.downloader_app_swing_based;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FileDownloaderApp extends JFrame {
    private JTable downloadTable;
    private DefaultTableModel tableModel;
    private List<FileDownloaderTask> tasks;

    public FileDownloaderApp() {
        setTitle("Swing Multi-Thread File Downloader");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"File URL", "Status", "Progress", "Speed"}, 0);
        downloadTable = new JTable(tableModel);
        add(new JScrollPane(downloadTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Download");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        buttonPanel.add(addButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        tasks = new ArrayList<>();

        addButton.addActionListener(e -> {
            String fileURL = JOptionPane.showInputDialog(this, "Enter File URL:");
            if (fileURL != null && !fileURL.isEmpty()) {
                addDownload(fileURL);
            }
        });

        pauseButton.addActionListener(e -> pauseDownload());
        resumeButton.addActionListener(e -> resumeDownload());
    }

    private void addDownload(String fileURL) {
        FileDownloaderTask task = new FileDownloaderTask(fileURL, tableModel, tableModel.getRowCount());
        tableModel.addRow(new Object[]{fileURL, "Downloading", 0, "0 B/s"});
        tasks.add(task);
        new Thread(task).start();
    }

    private void pauseDownload() {
        for (FileDownloaderTask task : tasks) {
            task.pauseDownload();
        }
    }

    private void resumeDownload() {
        for (FileDownloaderTask task : tasks) {
            task.resumeDownload();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileDownloaderApp().setVisible(true));
    }
}
