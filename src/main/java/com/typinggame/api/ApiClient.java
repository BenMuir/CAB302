package com.typinggame.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * ApiClient provides a minimal HTTP client for interacting with the local ApiServer.
 * Currently supports creating new custom drills.
 */
public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient(); // shared client
    private final String base; // base API URL (e.g. http://127.0.0.1:18080)

    /** Normalize base URL to not end with a slash. */
    public ApiClient(String baseUrl) {
        this.base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
    }

    /**
     * Send a POST /api/drills to create a new drill.
     *
     * @param title   drill title
     * @param content drill text body
     * @param tier    drill tier
     * @return the generated drill ID from the server
     */
    public int createDrill(String title, String content, int tier) throws Exception {
        // Manually construct JSON payload
        String json = "{\"title\":\"" + esc(title) + "\",\"content\":\"" + esc(content) + "\",\"tier\":" + tier + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/api/drills"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        // Parse ID if request succeeded (2xx)
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = resp.body();
            int idx = body.indexOf("\"id\":");
            if (idx >= 0) {
                int start = idx + 5;
                int end = body.indexOf(',', start);
                if (end < 0) end = body.indexOf('}', start);
                return Integer.parseInt(body.substring(start, end).trim());
            }
        }
        // Otherwise fail with error + body
        throw new RuntimeException("Create failed: " + resp.statusCode() + " " + resp.body());
    }

    /** Escape JSON string fields. */
    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
