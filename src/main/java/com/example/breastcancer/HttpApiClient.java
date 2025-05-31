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

 /**
 * @title: Documentation for Session.java, Credentials.java and HttpApiClient.java
 *
 * @author: Sai Surisetti (@saisurisettigriffith)
 * @date: May 31, 2025
 * 
 * Application start-up
 *
 * When you open the application, the user is going to see a login page and then
 * a register page. The login page was made using (check relevant .java files for
 * documentation) and the register page was made using (check relevant .java
 * files for documentation). We need to create both a login page and a
 * registration page. In a normal scenario the user has credentials and logs in;
 * if not, they use the registration page. Once the user registers, that
 * information is sent to Firebase.
 *
 * How does that happen? It uses the Firebase imports we included in Java, then
 * sends the data to Firebase. Firebase checks whether those credentials already
 * exist and responds appropriately. If the credentials are already present and
 * you call the registration method instead of login, that causes a problem, so
 * the two methods are separate and each has its own implementation.
 *
 * Access control
 * Assume registration and login are handled. If a user is not logged in,
 * there’s nothing else they can do: the first UI requires authentication to
 * access any other page. Until then you only have two options — register or
 * sign-in. After registration you automatically sign-in. Before that, there is
 * no way to view or access the next page; credentials are mandatory.
 *
 * The check lives in Session.java. Session.java has a variable called boolean
 * isLoggedIn. The class also has a private static boolean, so it employs the
 * singleton pattern: static variables exist only once per running JVM. If you
 * run the app on two computers, each JVM holds its own static instance, so we
 * don’t worry about parallelism.
 *
 * Because of encapsulation we don’t expose credentials for direct modification.
 * Session holds a private Credentials object (username + password). The
 * variable loggedIn defaults to false. The login or registration logic ensures
 * that when either succeeds, the flag switches to true.
 *
 * A session starts as soon as you launch the application, but whether you are
 * logged-in is determined by calls from MainApp.java, then ChatPanel.java,
 * which shows RegistrationPanel.java or LoginPanel.java based on user status.
 * When the user presses submit (register or login), it calls
 * Session.register(…) or Session.login(…).
 *
 * Both functions call getApiKey() (also in Session.java), which fetches the
 * Firebase Web API key and passes it to
 * callAuthEndpoint(…). callAuthEndpoint(String url, Credentials creds) builds a
 * JSON payload, constructs an HttpRequest, sends it with the standard
 * HttpClient, and collects the HttpResponse<String> res. A statusCode of 200
 * means success.
 *
 * Risk data
 * New users have no risk data. We store that data in MongoDB. The method
 * saveRiskToDb(JsonObject risk) creates
 * Document doc = new Document("email", credentials.getUsername()).append("risk", risk.toString());
 * and inserts it into the risk_data collection.
 *
 * Flask is stateless: every /api/chat call includes the latest riskData array,
 * so Flask doesn’t track sessions; we always pass the context.
 * We currently store only risk documents in MongoDB; chat history or summaries
 * are not persisted yet.
 *
 * Flow guarantees
 * The application flow guarantees that a user cannot reach chat without:
 * 1. Being logged in, and
 * 2. Having at least one risk assessment.
 *
 * A brand-new user must fill the risk form right after registration; returning
 * users log in and jump straight to chat unless they click “Redo Assessment”,
 * which lets them update risk parameters and resubmit. Although we could add a
 * global flag for “has risk data”, the enforced flow already covers it.
 *
 * So, to recap: the user registers or logs in, Session verifies credentials
 * through Firebase, MainApp routes to the risk form if needed, risk data is
 * saved to MongoDB and cached in memory, and only then can the user enter the
 * chat page.
 */