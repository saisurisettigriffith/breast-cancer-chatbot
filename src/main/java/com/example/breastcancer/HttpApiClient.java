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

    private static final String BASE = "https://breast-cancer-flask-455419.ew.r.appspot.com";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public JsonObject postRisk(JsonObject body) throws Exception {
        return post("/api/risk", body);
    }

    public JsonObject postChat(JsonObject body) throws Exception {
        return post("/api/chat", body);
    }

    private JsonObject post(String path, JsonObject body) throws Exception {
        // Create the HTTP request
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(body), StandardCharsets.UTF_8))
                .build();
        // gson converts the body to a JSON string
        // JSON body is not automatically a JSON string,
        // so we need to convert it explicitly.
        // .uri(flask-url/path).header(spcifiy content-type here).POST(body).build()

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException(
                    "Server responded " + res.statusCode() + ": " + res.body());
        }
        // What we receive is in HttpResponse<String> format,
        // and res.body() is what contains the JSON response as a String.
        // so we need to parse res.body() into a JsonObject.
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }
}

/**
 * 
 * So, this is a simple HTTP API client for interacting with the Flask back-end.
 * It calls flask/endpoint using POST(body), but it initally stores the request
 * in...
 * ...a format required by the parameters of HttpResponse returning call
 * "HttpClient.send()".
 * This format is HttpRequest Class/Type - lets call it <req>
 * So, essentially, it does .uri(flask-url/path)... then .POST(body).build()
 * when creating a variable of type HttpRequest.
 * Then when calling and storing the return value of HttpClient.send(),
 * we pass in the <req> variable that contains the request we built as a
 * parameter.
 * What we get is in a format HttpResponse<String> which contains the response
 * body as a String.
 * We then parse that String into a JsonObject using Gson's JsonParser, a format
 * that allows us to work with JSON data in Java.
 * 
 */