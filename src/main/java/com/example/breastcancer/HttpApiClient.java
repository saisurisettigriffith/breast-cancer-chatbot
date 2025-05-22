package com.example.breastcancer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Concrete implementation that wraps java.net.http. */
public class HttpApiClient implements ApiClient {

    private static final String BASE = System.getenv().getOrDefault(
            "REACT_APP_API_BASE_URL",
            "https://breast-cancer-flask-455419.ew.r.appspot.com");

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson       gson   = new Gson();

    @Override
    public JsonObject post(String path, JsonObject body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("Server responded: " + res.statusCode());
        }
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }
}