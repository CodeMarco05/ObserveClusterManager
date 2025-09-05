package com.observe.os1.v1;

import jakarta.enterprise.context.ApplicationScoped;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.Map;


@ApplicationScoped
public class AdvancedHttpService {

    private final HttpClient httpClient;

    public AdvancedHttpService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public HttpResult makeRequest(RequestConfig config) {
        try {
            String url = buildUrl(config);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(config.timeoutSeconds));

            // Add headers
            if (config.headers != null) {
                config.headers.forEach(requestBuilder::header);
            }

            // Set method and body
            setMethodAndBody(requestBuilder, config);

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return new HttpResult(
                    response.statusCode(),
                    response.body(),
                    response.headers().map(),
                    true,
                    null
            );

        } catch (Exception e) {
            return new HttpResult(0, null, null, false, e.getMessage());
        }
    }

    private String buildUrl(RequestConfig config) {
        String scheme = config.useHttps ? "https" : "http";
        boolean isDefaultPort = (config.port == 80 && !config.useHttps) ||
                (config.port == 443 && config.useHttps);

        String path = config.path;
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        if (path == null) path = "";

        // Add query parameters
        if (config.queryParams != null && !config.queryParams.isEmpty()) {
            StringBuilder query = new StringBuilder();
            config.queryParams.forEach((key, value) -> {
                if (query.length() > 0) query.append("&");
                query.append(key).append("=").append(value);
            });
            path += "?" + query.toString();
        }

        if (isDefaultPort) {
            return String.format("%s://%s%s", scheme, config.hostOrIp, path);
        } else {
            return String.format("%s://%s:%d%s", scheme, config.hostOrIp, config.port, path);
        }
    }

    private void setMethodAndBody(HttpRequest.Builder builder, RequestConfig config) {
        switch (config.method.toUpperCase()) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(config.body != null ? config.body : ""));
                break;
            case "PUT":
                builder.PUT(HttpRequest.BodyPublishers.ofString(config.body != null ? config.body : ""));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            case "PATCH":
                builder.method("PATCH", HttpRequest.BodyPublishers.ofString(config.body != null ? config.body : ""));
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + config.method);
        }
    }

    // Configuration class
    public static class RequestConfig {
        public String hostOrIp;
        public int port;
        public String path;
        public boolean useHttps;
        public String method = "GET";
        public String body;
        public Map<String, String> headers;
        public Map<String, String> queryParams;
        public int timeoutSeconds = 30;

        public RequestConfig(String hostOrIp, int port, boolean useHttps) {
            this.hostOrIp = hostOrIp;
            this.port = port;
            this.useHttps = useHttps;
        }
    }

    // Result class
    public static class HttpResult {
        public final int statusCode;
        public final String body;
        public final Map<String, java.util.List<String>> headers;
        public final boolean success;
        public final String error;

        public HttpResult(int statusCode, String body, Map<String, java.util.List<String>> headers,
                          boolean success, String error) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
            this.success = success;
            this.error = error;
        }
    }
}