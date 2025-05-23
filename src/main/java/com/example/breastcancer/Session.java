package com.example.breastcancer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Keeps track of the logged-in user, performs register / sign-in
 * via Firebase Auth REST API, and stores the user-specific risk data.
 */
public class Session {

    // Resource filenames (must be placed in src/main/resources)
    private static final String SERVICE_ACCOUNT_FILE = "breast-cancer-react-9d2892f8bb57.json";
    private static final String WEBAPP_CONFIG_FILE   = "breast-cancer-react-webapp.json";

    private static FirebaseAuth auth;
    private static boolean firebaseReady = false;

    // Holds the Firebase Web API Key loaded from WEBAPP_CONFIG_FILE
    private static String webApiKey;
    private static boolean webAppConfigLoaded = false;

    /* ---------- per-user state ---------- */
    private Credentials credentials;
    private boolean     loggedIn = false;
    private final List<JsonObject> riskCache = new ArrayList<>();

    public Session() {
        // Initialize the Admin SDK once
        if (!firebaseReady) {
            firebaseReady = initFirebase();
            System.out.println("Firebase Admin SDK initialized? " + firebaseReady);
        }
        // Load the Web API Key once
        if (!webAppConfigLoaded) {
            webAppConfigLoaded = initWebAppConfig();
            System.out.println("Web-app config loaded? " + webAppConfigLoaded);
        }
    }

    /** Load service account JSON and initialize Admin SDK */
    private boolean initFirebase() {
        try (InputStream in = Session.class
                .getClassLoader()
                .getResourceAsStream(SERVICE_ACCOUNT_FILE)) {
            if (in == null) {
                System.err.println("Cannot find: " + SERVICE_ACCOUNT_FILE);
                return false;
            }
            GoogleCredentials cred = GoogleCredentials.fromStream(in);
            FirebaseOptions opt = FirebaseOptions.builder()
                    .setCredentials(cred)
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(opt);
            auth = FirebaseAuth.getInstance(app);
            return true;
        } catch (Exception ex) {
            System.err.println("Failed to init Firebase Admin SDK: " + ex.getMessage());
            return false;
        }
    }

    /** Load web-app JSON and extract the apiKey */
    private boolean initWebAppConfig() {
        try (InputStream in = Session.class
                .getClassLoader()
                .getResourceAsStream(WEBAPP_CONFIG_FILE)) {
            if (in == null) {
                System.err.println("Cannot find: " + WEBAPP_CONFIG_FILE);
                return false;
            }
            JsonObject cfg = JsonParser
                .parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .getAsJsonObject();
            if (cfg.has("apiKey")) {
                webApiKey = cfg.get("apiKey").getAsString();
                return true;
            } else {
                System.err.println("No apiKey in " + WEBAPP_CONFIG_FILE);
                return false;
            }
        } catch (Exception ex) {
            System.err.println("Failed to load web-app config: " + ex.getMessage());
            return false;
        }
    }

    /** Use env-var if set, otherwise the key from your webapp JSON */
    private String getApiKey() {
        String env = System.getenv("FIREBASE_API_KEY");
        if (env != null && !env.isBlank()) return env;
        return webApiKey;
    }

    /* ======================================================================
     *  PUBLIC API
     * ====================================================================== */
    public boolean register(Credentials creds) {
        String key = getApiKey();
        if (key == null || key.isEmpty()) {
            System.err.println("No Firebase API key available");
            return false;
        }
        String url = 
          "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + key;
        return callAuthEndpoint(url, creds);
    }

    public boolean login(Credentials creds) {
        String key = getApiKey();
        if (key == null || key.isEmpty()) {
            System.err.println("No Firebase API key available");
            return false;
        }
        String url =
          "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + key;
        boolean ok = callAuthEndpoint(url, creds);
        if (ok) {
            this.credentials = creds;
            this.loggedIn = true;
        }
        return ok;
    }

    public boolean isLoggedIn()            { return loggedIn; }
    public List<JsonObject> getRiskCache() { return riskCache; }
    public void addRisk(JsonObject j)      { riskCache.add(j); }

    /* ---------- low-level REST call ---------- */
    private boolean callAuthEndpoint(String url, Credentials creds) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("email",              creds.getUsername());
            body.addProperty("password",           creds.getPassword());
            body.addProperty("returnSecureToken",  true);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    body.toString(), StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> res = 
                client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (Exception ex) {
            System.err.println("Auth endpoint error: " + ex.getMessage());
            return false;
        }
    }
}
