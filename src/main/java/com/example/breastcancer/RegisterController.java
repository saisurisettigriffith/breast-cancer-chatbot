package com.example.breastcancer;

public class RegisterController {

    private final Session session;
    private final MainApp mainApp;

    public RegisterController(Session session, MainApp mainApp) {
        this.session = session;
        this.mainApp = mainApp;
    }

    public String onRegister(String email, String password) {
        Credentials creds = new Credentials(email, password);
        if (!creds.isValidFormat) {
            return "Enter a valid e-mail and 6-plus-character password.";
        }
        if (session.register(creds)) {
            mainApp.showLoginPanel();
            return "Account created: please sign-in.";
        }
        return "Registration failed: try again.";
    }

    public void goToLogin() {
        mainApp.showLoginPanel();
    }
}

/**
 * This class handles user registration logic.
 * We inject the session and main app to keep it decoupled.
 * The core logic is located in the Session class.
 * We access it through session.register(Credentials).
 *
 * Purpose:
 * - Validate user input.
 * - Call session.register() to create a new user account.
 * - Navigate to the login panel on success.
 *       *** REMEMBER: ***
 *          1. BY DEFINITION, A USER IS ONLY PROMPTED TO LOGIN
 *             IF THEY ARE NOT ALREADY LOGGED IN.
 *          2. If they are logged in, they will be redirected to the risk assessment panel.
 *          3. This is automatic because we create a JavaFX
 *               where EVENT MANAGEMENT takes OVER!!!
 *               and this allows for RegisterController
 *               and as per our override of run metod,
 *               with in it we create RegisterController
 *               before we initialize and begin with LoginPanel.
 *               and RegisterController checks if the user is logged in
 *               and if so, it redirects them to the risk assessment panel.
 *               otherwise, if not logged in, it shows the login fields and forms.
 * - Show error messages on failure.
 * 
 * -> see Session.java for more information.
 */