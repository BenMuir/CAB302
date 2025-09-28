package com.typinggame.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal local REST API for custom drills.
 * Listens on http://127.0.0.1:18080
 *
 * POST   /api/drills          -> create custom drill (JSON or text/plain)
 * GET    /api/drills          -> list all (use ?custom=true for only custom)
 * DELETE /api/drills/{id}     -> delete a custom drill
 */
public class ApiServer {
    private final DrillRepository drills = new DrillRepository();
    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/drills", new DrillsHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("API listening on http://127.0.0.1:" + port);
    }

    public void stop() { if (server != null) server.stop(0); }

    class DrillsHandler implements HttpHandler {
        @Override public void handle(HttpExchange ex) throws IOException {
            try {
                String method = ex.getRequestMethod();
                URI uri = ex.getRequestURI();
                String path = uri.getPath(); // /api/drills or /api/drills/{id}

                if ("POST".equals(method) && "/api/drills".equals(path)) { handleCreate(ex); return; }
                if ("GET".equals(method) && "/api/drills".equals(path)) { handleList(ex); return; }
                if ("DELETE".equals(method) && path.startsWith("/api/drills/")) {
                    handleDelete(ex, path.substring("/api/drills/".length())); return;
                }
                send(ex, 404, json("error", "Not Found"));
            } catch (Exception e) {
                send(ex, 500, json("error", e.getMessage()));
            }
        }

        private void handleCreate(HttpExchange ex) throws IOException {
            String ctype = Optional.ofNullable(ex.getRequestHeaders().getFirst("Content-Type")).orElse("");
            String body = readAll(ex.getRequestBody());

            String title = null, content = null; int tier = 1;
            if (ctype.contains("application/json")) {
                title = extractJsonString(body, "title");
                content = extractJsonString(body, "content");
                Integer t = extractJsonInt(body, "tier");
                if (t != null) tier = Math.max(1, t);
            } else { // text/plain fallback
                Map<String,String> kv = parseTextBody(body);
                title = orNull(kv.get("title"), kv.get("Title"));
                content = orNull(kv.get("content"), kv.get("Content"));
                try { tier = Integer.parseInt(kv.getOrDefault("tier", "1")); } catch (Exception ignore) {}
            }
            if (title == null || title.isBlank() || content == null || content.isBlank()) {
                send(ex, 400, json("error", "title and content are required"));
                return;
            }

            Drill d = new Drill(0, title.trim(), content.trim(), tier);
            int id = drills.insertCustom(d);
            send(ex, 201, "{\"id\":" + id + ",\"status\":\"created\"}");
        }

        private void handleList(HttpExchange ex) throws IOException {
            boolean onlyCustom = Optional.ofNullable(ex.getRequestURI().getQuery()).orElse("").contains("custom=true");
            List<Drill> list = onlyCustom ? drills.findCustom() : drills.findAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Drill d = list.get(i);
                if (i>0) sb.append(',');
                sb.append('{')
                        .append("\"id\":").append(d.id).append(',')
                        .append("\"title\":\"").append(escape(d.title)).append("\",")
                        .append("\"tier\":").append(d.tier).append('}');
            }
            sb.append(']');
            send(ex, 200, sb.toString());
        }

        private void handleDelete(HttpExchange ex, String idStr) throws IOException {
            try {
                int id = Integer.parseInt(idStr);
                boolean ok = drills.deleteCustom(id);
                if (ok) send(ex, 200, json("status", "deleted"));
                else send(ex, 404, json("error", "not found or not custom"));
            } catch (NumberFormatException e) {
                send(ex, 400, json("error", "invalid id"));
            }
        }

        // Helpers
        private String readAll(InputStream in) throws IOException {
            byte[] buf = in.readAllBytes();
            return new String(buf, StandardCharsets.UTF_8);
        }
        private void send(HttpExchange ex, int code, String body) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            byte[] b = body.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(code, b.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(b); }
        }
        private String json(String k, String v) { return "{\"" + k + "\":\"" + escape(v) + "\"}"; }
        private String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
        private String orNull(String a, String b) { return a != null ? a : b; }

        // tiny JSON extractors (flat)
        private String extractJsonString(String json, String key) {
            Pattern p = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"(.*?)\\\"");
            Matcher m = p.matcher(json);
            return m.find() ? m.group(1) : null;
        }
        private Integer extractJsonInt(String json, String key) {
            Pattern p = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*([0-9]+)");
            Matcher m = p.matcher(json);
            return m.find() ? Integer.parseInt(m.group(1)) : null;
        }
        private Map<String,String> parseTextBody(String body) {
            Map<String,String> map = new HashMap<>();
            String[] parts = body.split("\\n\\n", 2);
            String headers = parts.length > 0 ? parts[0] : "";
            String content = parts.length > 1 ? parts[1] : "";
            for (String line : headers.split("\\n")) {
                int idx = line.indexOf(':');
                if (idx > 0) map.put(line.substring(0, idx).trim().toLowerCase(), line.substring(idx+1).trim());
            }
            if (!content.isBlank()) map.put("content", content);
            return map;
        }
    }
}
