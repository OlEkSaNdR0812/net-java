package ua.edu.chmnu.net_dev.c4.url.working_with_url_resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountWords {

    public static String getHTMLContent(String urlString) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Error fetching the webpage content: " + e.getMessage());
        }
        return content.toString();
    }

    public static int countWordsInText(String htmlContent) {
        htmlContent = htmlContent.replaceAll("(?s)<script.*?>.*?</script>", " ");
        htmlContent = htmlContent.replaceAll("(?s)<style.*?>.*?</style>", " ");

        String textContent = htmlContent.replaceAll("<[^>]*>", " ");

        textContent = textContent.replaceAll("\\s+", " ").trim();

        Pattern wordPattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = wordPattern.matcher(textContent);

        int wordCount = 0;
        while (matcher.find()) {
            wordCount++;
        }

        return wordCount;
    }

    public static void main(String[] args) {
        String urlString = "https://www.wikipedia.org";

        String htmlContent = getHTMLContent(urlString);

        int wordCount = countWordsInText(htmlContent);

        System.out.println("Word Count: " + wordCount);
    }
}
