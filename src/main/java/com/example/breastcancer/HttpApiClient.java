package com.example.breastcancer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Concrete HTTP client for your Flask back-end.
 * Call postRisk(...) and postChat(...) directly.
 */
public class HttpApiClient {

    /** ← same hard-coded URL you’ve been using */
    private static final String BASE =
        "https://breast-cancer-flask-455419.ew.r.appspot.com";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson       gson   = new Gson();

    /** Send the risk-calculation request and return the parsed JSON. */
    public JsonObject postRisk(JsonObject body) throws Exception {
        return post("/api/risk", body);
    }

    /** Send the chat request and return the parsed JSON. */
    public JsonObject postChat(JsonObject body) throws Exception {
        return post("/api/chat", body);
    }

    /** Internal helper—no need to change this. */
    private JsonObject post(String path, JsonObject body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(BASE + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                gson.toJson(body), StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException(
                "Server responded " + res.statusCode() + ": " + res.body());
        }
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }
}
