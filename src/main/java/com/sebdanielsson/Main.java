package com.sebdanielsson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        String endpointURL = "https://www.reddit.com/r/relationship_advice/new.json?limit=100"; // Retrieve up to 100
                                                                                                // posts
        int iterations = 10; // Number of iterations

        try {
            // Create a BufferedWriter to write data to a text file
            BufferedWriter writer = new BufferedWriter(new FileWriter("reddit_posts.txt"));

            // Iterate 10 times to collect more posts
            String after = null;
            for (int i = 0; i < iterations; i++) {
                // Send the request to the Reddit API
                URL url = new URL(endpointURL + (after != null ? "&after=" + after : ""));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Read the JSON response from the API
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject data = jsonResponse.getJSONObject("data");
                JSONArray children = data.getJSONArray("children");

                // Write post information to the text file
                for (int j = 0; j < children.length(); j++) {
                    JSONObject post = children.getJSONObject(j).getJSONObject("data");
                    writer.write(post.getString("selftext"));
                }

                // Get the value associated with the "after" key
                Object afterObj = data.get("after");
                if (afterObj instanceof String) {
                    after = (String) afterObj;
                } else {
                    after = null;
                }

                // Disconnect the connection
                connection.disconnect();
            }

            // Close the BufferedWriter
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
