package com.injibara.iams.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.injibara.iams.frontend.model.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private static final String BASE = "http://localhost:8080";
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static ObjectMapper mapper() { return MAPPER; }

    public static HttpResponse<String> get(String path) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(30)).GET();
        addAuth(b);
        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> post(String path, Object body) throws Exception {
        String json = body == null ? "" : MAPPER.writeValueAsString(body);
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        addAuth(b);
        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> put(String path, Object body) throws Exception {
        String json = body == null ? "" : MAPPER.writeValueAsString(body);
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));
        addAuth(b);
        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(30)).DELETE();
        addAuth(b);
        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void addAuth(HttpRequest.Builder b) {
        String t = UserSession.getInstance().getToken();
        if (t != null) b.header("Authorization", "Bearer " + t);
    }
}
