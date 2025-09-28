package com.typinggame.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/** Simple client for the local API. */
public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String base;
    public ApiClient(String baseUrl) {
        this.base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
    }

    public int createDrill(String title, String content, int tier) throws Exception {
        String json = "{\"title\":\"" + esc(title) + "\",\"content\":\"" + esc(content) + "\",\"tier\":" + tier + "}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/api/drills"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = resp.body();
            int idx = body.indexOf("\"id\":");
            if (idx >= 0) {
                int start = idx + 5; int end = body.indexOf(',', start);
                if (end < 0) end = body.indexOf('}', start);
                return Integer.parseInt(body.substring(start, end).trim());
            }
        }
        throw new RuntimeException("Create failed: " + resp.statusCode() + " " + resp.body());
    }

    private String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
