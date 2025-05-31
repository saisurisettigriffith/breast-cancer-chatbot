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

import org.bson.Document;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Session {

    private static final String SERVICE_ACCOUNT_FILE = "breast-cancer-react-9d2892f8bb57.json";
    private static final String WEBAPP_CONFIG_FILE = "breast-cancer-react-webapp.json";

    private static FirebaseAuth auth;
    private static boolean firebaseReady = false;
    private static String webApiKey;
    private static boolean webAppConfigLoaded = false;

    private static final String CONNECTION_STRING = "mongodb+srv://saisuri2015:Uzumymw1998!@userdata.f5a8w0t.mongodb.net"
            + "/?retryWrites=true&w=majority&appName=UserData&serverApi=v1";
    private static final String DB_NAME = "UserData";
    private static final String COLLECTION_NAME = "risk_data";

    private static MongoClient mongoClient;
    private static MongoCollection<Document> riskCollection;
    private static boolean mongoReady = false;

    private Credentials credentials;
    private boolean loggedIn = false;
    private final List<JsonObject> riskCache = new ArrayList<>();

    public Session() {
        if (!firebaseReady)
            firebaseReady = initFirebase();
        if (!webAppConfigLoaded)
            webAppConfigLoaded = initWebAppConfig();
        if (!mongoReady)
            mongoReady = initMongo();
    }

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
            }
            System.err.println("No apiKey in " + WEBAPP_CONFIG_FILE);
            return false;
        } catch (Exception ex) {
            System.err.println("Failed loading web-app config: " + ex.getMessage());
            return false;
        }
    }

    private String getApiKey() {
        String env = System.getenv("FIREBASE_API_KEY");
        return (env != null && !env.isBlank()) ? env : webApiKey;
    }

    private boolean initMongo() {
        try {

            mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase db = mongoClient.getDatabase(DB_NAME);
            riskCollection = db.getCollection(COLLECTION_NAME);
            db.runCommand(new Document("ping", 1));
            System.out.println(" MongoDB connected.");
            return true;
        } catch (MongoException ex) {
            System.err.println(" Mongo init failed: " + ex.getMessage());
            return false;
        }
    }

    private void loadUserRiskCache() {
        riskCache.clear();
        if (!mongoReady || credentials == null) {
            return;
        }

        Document filter = new Document("email", credentials.getUsername());
        FindIterable<Document> iter = riskCollection.find(filter);
        MongoCursor<Document> cur = iter.iterator();
        while (cur.hasNext()) {
            Document doc = cur.next();
            String json = doc.getString("risk");
            if (json != null && !json.isEmpty()) {
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                riskCache.add(obj);
            }
        }
        cur.close();
    }

    private void saveRiskToDb(JsonObject risk) {
        if (!mongoReady || credentials == null || risk == null)
            return;

        Document doc = new Document("email", credentials.getUsername())
                .append("risk", risk.toString());
        try {
            riskCollection.insertOne(doc);
        } catch (MongoException ex) {
            System.err.println("Mongo insert failed: " + ex.getMessage());
        }
    }

    public boolean register(Credentials creds) {
        String key = getApiKey();
        if (key == null || key.isEmpty()) {
            System.err.println("No Firebase API key");
            return false;
        }

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + key;
        return callAuthEndpoint(url, creds);
    }

    public boolean login(Credentials creds) {
        String key = getApiKey();
        if (key == null || key.isEmpty()) {
            System.err.println("No Firebase API key");
            return false;
        }

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + key;
        boolean ok = callAuthEndpoint(url, creds);
        if (ok) {
            this.credentials = creds;
            this.loggedIn = true;
            loadUserRiskCache();
        }
        return ok;
    }

    public void logout() {
        this.loggedIn = false;
        this.credentials = null;
        this.riskCache.clear();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public List<JsonObject> getRiskCache() {
        return riskCache;
    }

    public void addRisk(JsonObject j) {
        riskCache.add(j);
        saveRiskToDb(j);
    }

    private boolean callAuthEndpoint(String url, Credentials creds) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("email", creds.getUsername());
            body.addProperty("password", creds.getPassword());
            body.addProperty("returnSecureToken", true);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (Exception ex) {
            System.err.println("Auth endpoint error: " + ex.getMessage());
            return false;
        }
    }
}

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