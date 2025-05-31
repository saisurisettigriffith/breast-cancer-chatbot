package com.example.breastcancer;

/**
 * Credentials class manages Firebase Admin SDK initialization
 * and provides methods for validating user credentials
 * against Firebase Authentication via the REST API.
 */
public class Credentials {
    public boolean isValidFormat = false;
    private String username;
    private String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
        this.isValidFormat = checkValidityRegEx();
    }

    public boolean checkValidityRegEx() {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String passwordPattern = ".{6,}";
        boolean emailMatches = this.username.matches(emailPattern);
        boolean passwordMatches = this.password.matches(passwordPattern);
        if (emailMatches && passwordMatches) {
            return true;
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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