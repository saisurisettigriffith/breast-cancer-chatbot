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