package com.observe.os1.v1;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PrometheusUtil {
    public static Response executePrometheusRequest(String urlWithParams) {
        HttpURLConnection conn = null;
        try {
            // Execute request
            URL url = new URL(urlWithParams);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            String responseBody = readResponse(conn, status);

            // Check the response status code
            if (status != HttpURLConnection.HTTP_OK) {
                return Response.status(status)
                        .entity("Error fetching data from Prometheus: " + responseBody)
                        .build();
            }

            // Return the response
            return Response.ok(responseBody)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .build();

        } catch (MalformedURLException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL: " + e.getMessage())
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error connecting to Prometheus: " + e.getMessage())
                    .build();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readResponse(HttpURLConnection conn, int status) throws IOException {
        BufferedReader reader;

        // Use error stream for HTTP error codes, input stream for success
        if (status >= 400) {
            reader = new BufferedReader(new InputStreamReader(
                    conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream()
            ));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
