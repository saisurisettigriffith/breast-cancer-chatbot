// File: src/main/java/com/example/breastcancer/LoginPanel.java
package com.example.breastcancer;

import java.io.InputStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;           // ← NEW
import javafx.scene.image.ImageView;      // ← NEW
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX version of your LoginPanel.
 * Mirrors styling/layout from RegisterPanel and WindowBuilder.
 *
 * Added logo on the left, same as RegisterPanel.
 */
public class LoginPanel {

    private final LoginController controller;
    private final Pane            pane;
    private final TextField       emailFld;
    private final PasswordField   passFld;
    private final Button          loginBtn;
    private final Button          registerBtn;
    private final Label           msgLabel;
    private final ImageView       logoView;   // ← NEW

    /* ───────────────────────────────────────── helper ───────────────────────────────────────── */
    /** Try to load “fonts/Lexend.ttf”; return system font if absent. */
    private Font loadFont(double size) {
        try {
            InputStream in = getClass().getResourceAsStream("/fonts/Lexend.ttf");
            if (in != null) {
                Font f = Font.loadFont(in, size);
                if (f != null) return f;
            }
        } catch (Exception ex) {
            /* ignore – fall through to system font */
        }
        return Font.font("System", size);
    }

    /** Try to load an image from resources; return null if not found. */
    private Image loadImage(String path) {
        try {
            InputStream in = getClass().getResourceAsStream(path);
            if (in != null) return new Image(in);
        } catch (Exception ignore) { /* fall through */ }
        return null;
    }

    /* ───────────────────────────────────────── constructor ──────────────────────────────────── */
    public LoginPanel(LoginController controller) {
        this.controller = controller;

        pane = new Pane();
        pane.setPrefSize(1177, 600);
        pane.setStyle("-fx-background-color: #eeeeee;");

        /* ---------------- logo (left side) ---------------- */
        Image img = loadImage("/logo.png");                                    // ← adjust to match RegisterPanel
        if (img != null) {
            logoView = new ImageView(img);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(250.0);                                        // ← same width as RegisterPanel
            logoView.setLayoutX(80.0);
            logoView.setLayoutY(120.0);
            pane.getChildren().add(logoView);
        } else {
            logoView = null; /* file missing – silently continue */
            System.err.println("⚠️ could not load logo.png – check your resources folder");
        }

        /* ---------------- e-mail field ---------------- */
        emailFld = new TextField();
        emailFld.setLayoutX(553.0);
        emailFld.setLayoutY(162.0);
        emailFld.setPrefWidth(200.0);
        emailFld.setPromptText("E-mail");
        emailFld.setFont(loadFont(14.0));
        emailFld.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-border-color: #626262; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 2px; " +
            "-fx-prompt-text-fill: #737674;"
        );

        /* ---------------- password field ---------------- */
        passFld = new PasswordField();
        passFld.setLayoutX(553.0);
        passFld.setLayoutY(195.0);
        passFld.setPrefWidth(200.0);
        passFld.setPromptText("Password");
        passFld.setFont(loadFont(14.0));
        passFld.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-border-color: #626262; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 2px; " +
            "-fx-prompt-text-fill: #737674;"
        );

        /* ---------------- login button ---------------- */
        loginBtn = new Button("Login");
        loginBtn.setLayoutX(553.0);
        loginBtn.setLayoutY(240.0);
        loginBtn.setPrefWidth(100.0);
        loginBtn.setFont(loadFont(14.0));
        loginBtn.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-border-color: #626262; " +
            "-fx-border-radius: 4px; " +
            "-fx-background-radius: 4px; " +
            "-fx-border-width: 1px;"
        );
        /* mouse-press effect */
        loginBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                loginBtn.setBackground(new Background(new BackgroundFill(
                    Color.web("#c2c2c2"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        loginBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                loginBtn.setBackground(new Background(new BackgroundFill(
                    Color.web("#ffffff"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                String email = emailFld.getText().trim();
                String pass  = passFld.getText();
                String err   = controller.onLogin(email, pass);
                msgLabel.setText(err != null ? err : "");
            }
        });

        /* ---------------- register button ---------------- */
        registerBtn = new Button("Need an account? Register");
        registerBtn.setLayoutX(553.0);
        registerBtn.setLayoutY(285.0);
        registerBtn.setFont(loadFont(12.0));
        registerBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-underline: true;"
        );
        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.goToRegister();
            }
        });

        /* ---------------- status label ---------------- */
        msgLabel = new Label(" ");
        msgLabel.setLayoutX(553.0);
        msgLabel.setLayoutY(330.0);
        msgLabel.setFont(loadFont(14.0));
        msgLabel.setTextFill(Color.RED);

        /* ---------------- field labels ---------------- */
        Label emailLbl = new Label("E-mail:");
        emailLbl.setLayoutX(460.0);
        emailLbl.setLayoutY(167.0);
        emailLbl.setFont(loadFont(14.0));
        emailLbl.setTextFill(Color.web("#1b1b1b"));

        Label passLbl = new Label("Password:");
        passLbl.setLayoutX(460.0);
        passLbl.setLayoutY(200.0);
        passLbl.setFont(loadFont(14.0));
        passLbl.setTextFill(Color.web("#1b1b1b"));

        /* ---------------- assemble ---------------- */
        pane.getChildren().addAll(
            emailLbl,   emailFld,
            passLbl,    passFld,
            loginBtn,   registerBtn,
            msgLabel
        );
    }

    /** Expose the Pane so MainApp can embed it in a Scene. */
    public Pane getPane() {
        return pane;
    }

    /**
     * Convenience launcher for standalone testing.
     * You may delete this once embedded in MainApp.
     */
    public static class Launcher extends javafx.application.Application {
        @Override
        public void start(Stage primaryStage) {
            LoginController dummy = new LoginController(new Session(), null);
            LoginPanel form = new LoginPanel(dummy);
            primaryStage.setTitle("Sign-in");
            primaryStage.setScene(new Scene(form.getPane()));
            primaryStage.show();
        }
        public static void main(String[] args) {
            Platform.setImplicitExit(true);
            launch(args);
        }
    }
}
