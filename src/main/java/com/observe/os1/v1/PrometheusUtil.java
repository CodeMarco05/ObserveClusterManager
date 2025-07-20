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
        try {
            // Execute request
            URL url = new URL(urlWithParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();

            // Check the response status code
            if (status != HttpURLConnection.HTTP_OK) {
                return Response.status(status)
                        .entity("Error fetching data from Prometheus: " + response.toString())
                        .build();
            }
            // Return the response
            return Response.ok(response.toString())
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
        }
    }
}
