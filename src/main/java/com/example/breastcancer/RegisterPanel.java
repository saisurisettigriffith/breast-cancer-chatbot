// File: src/main/java/com/example/breastcancer/RegisterPanel.java
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
 * JavaFX version of your RegisterPanel.
 * Mirrors the styling and layout from WindowBuilder.java.
 *
 * Added (24 May 2025):
 *  • loadImage(..) helper + ImageView logo on the left-hand side.
 */
public class RegisterPanel {

    private final RegisterController controller;
    private final Pane               pane;
    private final TextField          emailFld;
    private final PasswordField      passFld;
    private final Button             registerBtn;
    private final Button             loginBtn;
    private final Label              msgLabel;
    private final ImageView          logoView;   // ← NEW

    /* ───────────────────────────────────────── helpers ──────────────────────────────────────── */
    /** Try to load “fonts/Lexend.ttf”; return system font if absent. */
    private Font loadFont(double size) {
        try {
            InputStream in = getClass().getResourceAsStream("/fonts/Lexend.ttf");
            if (in != null) {
                Font f = Font.loadFont(in, size);
                if (f != null) return f;
            }
        } catch (Exception ignore) { /* fall through */ }
        return Font.font("System", size);
    }

    /** Try to load an image from resources; return null if not found. */           // ← NEW
    private Image loadImage(String path) {                                         // ← NEW
        try {                                                                      // ← NEW
            InputStream in = getClass().getResourceAsStream(path);                 // ← NEW
            if (in != null) return new Image(in);                                  // ← NEW
        } catch (Exception ignore) { /* fall through */ }                          // ← NEW
        return null;                                                               // ← NEW
    }                                                                              // ← NEW

    /* ───────────────────────────────────────── constructor ──────────────────────────────────── */
    public RegisterPanel(RegisterController controller) {
        this.controller = controller;

        pane = new Pane();
        pane.setPrefSize(1177, 600);
        pane.setStyle("-fx-background-color: #eeeeee;");

        /* ---------------- logo (left side) ---------------- */                    // ← NEW
        Image img = loadImage("/logo.png");                                 // ← NEW
        if (img != null) {                                                         // ← NEW
            logoView = new ImageView(img);                                         // ← NEW
            logoView.setPreserveRatio(true);                                       // ← NEW
            logoView.setFitWidth(250.0);  // shrink / enlarge as you like          // ← NEW
            logoView.setLayoutX(80.0);                                              // ← NEW
            logoView.setLayoutY(120.0);                                             // ← NEW
            pane.getChildren().add(logoView);                                       // ← NEW
        } else {                                                                   // ← NEW
            logoView = null; /* file missing – silently continue */                // ← NEW
            System.err.println("⚠️ could not load logo.png – check your resources folder");
        }                                                                          // ← NEW

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

        /* ---------------- register button ---------------- */
        registerBtn = new Button("Register");
        registerBtn.setLayoutX(553.0);
        registerBtn.setLayoutY(240.0);
        registerBtn.setPrefWidth(100.0);
        registerBtn.setFont(loadFont(14.0));
        registerBtn.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-border-color: #626262; " +
            "-fx-border-radius: 4px; " +
            "-fx-background-radius: 4px; " +
            "-fx-border-width: 1px;"
        );
        /* mouse-press effect */
        registerBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                registerBtn.setBackground(new Background(new BackgroundFill(
                    Color.web("#c2c2c2"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        registerBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                registerBtn.setBackground(new Background(new BackgroundFill(
                    Color.web("#ffffff"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                String email = emailFld.getText().trim();
                String pass  = passFld.getText();
                String msg   = controller.onRegister(email, pass);
                msgLabel.setText(msg);
            }
        });

        /* ---------------- “already registered?” button ---------------- */
        loginBtn = new Button("Already registered? Sign-in");
        loginBtn.setLayoutX(553.0);
        loginBtn.setLayoutY(285.0);
        loginBtn.setFont(loadFont(12.0));
        loginBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #1b1b1b; " +
            "-fx-underline: true;"
        );
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.goToLogin();
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
            emailLbl, emailFld,
            passLbl,  passFld,
            registerBtn, loginBtn,
            msgLabel
        );
    }

    /** Expose the Pane so MainApp can embed it in a Scene. */
    public Pane getPane() {
        return pane;
    }

    /* ---------------- standalone test launcher (unchanged) ---------------- */
    public static class Launcher extends javafx.application.Application {
        @Override
        public void start(Stage primaryStage) {
            RegisterController dummy = new RegisterController(new Session(), null);
            RegisterPanel form = new RegisterPanel(dummy);
            primaryStage.setTitle("Create your account");
            primaryStage.setScene(new Scene(form.getPane()));
            primaryStage.show();
        }
        public static void main(String[] args) {
            Platform.setImplicitExit(true);
            launch(args);
        }
    }
}
