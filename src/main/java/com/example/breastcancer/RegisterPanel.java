package com.example.breastcancer;

import java.io.InputStream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RegisterPanel {

    private final RegisterController controller;
    private final Pane pane;
    private final TextField emailFld;
    private final PasswordField passFld;
    private final Button registerBtn;
    private final Button loginBtn;
    private final Label msgLabel;
    private final ImageView logoView;

    private Font loadFont(double size) {
        try {
            InputStream in = getClass().getResourceAsStream("/fonts/Lexend.ttf");
            if (in != null) {
                Font f = Font.loadFont(in, size);
                if (f != null)
                    return f;
            }
        } catch (Exception ignore) {

        }
        return Font.font("System", size);
    }

    private Image loadImage(String path) {
        try {
            InputStream in = getClass().getResourceAsStream(path);
            if (in != null)
                return new Image(in);
        } catch (Exception ignore) {

        }
        return null;
    }

    public RegisterPanel(RegisterController controller) {
        this.controller = controller;

        pane = new Pane();
        pane.setPrefSize(1177, 600);
        pane.setStyle("-fx-background-color: #eeeeee;");

        Image img = loadImage("/logo.png");
        if (img != null) {
            logoView = new ImageView(img);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(250.0);
            logoView.setLayoutX(80.0);
            logoView.setLayoutY(120.0);
            pane.getChildren().add(logoView);
        } else {
            logoView = null;
            System.err.println("Could not load logo image: /logo.png");
        }

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
                        "-fx-prompt-text-fill: #737674;");

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
                        "-fx-prompt-text-fill: #737674;");

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
                        "-fx-border-width: 1px;");

        registerBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                registerBtn.setBackground(new Background(new BackgroundFill(
                        Color.web("#c2c2c2"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        registerBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                registerBtn.setBackground(new Background(new BackgroundFill(
                        Color.web("#ffffff"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String email = emailFld.getText().trim();
                String pass = passFld.getText();
                String msg = controller.onRegister(email, pass);
                msgLabel.setText(msg);
            }
        });
        emailFld.setOnAction(e -> registerBtn.fire());
        passFld.setOnAction(e -> registerBtn.fire());

        loginBtn = new Button("Already registered? Sign-in");
        loginBtn.setLayoutX(553.0);
        loginBtn.setLayoutY(285.0);
        loginBtn.setFont(loadFont(12.0));
        loginBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #1b1b1b; " +
                        "-fx-underline: true;");
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.goToLogin();
            }
        });

        msgLabel = new Label(" ");
        msgLabel.setLayoutX(553.0);
        msgLabel.setLayoutY(330.0);
        msgLabel.setFont(loadFont(14.0));
        msgLabel.setTextFill(Color.RED);

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

        pane.getChildren().addAll(
                emailLbl, emailFld,
                passLbl, passFld,
                registerBtn, loginBtn,
                msgLabel);
    }

    public Pane getPane() {
        return pane;
    }
}

/**
 * RegisterPanel builds the JavaFX UI for user registration: styling, fields, buttons, and logo.
 *
 *   constructor and getPane() are called by:
 *       MainApp.buildScreens()
 *
 *   Once embedded into a JFXPanel and displayed, JavaFX’s event handling takes over:
 *     - EventHandler<ActionEvent> on registerBtn calls controller.onRegister(...)
 *     - EventHandler<ActionEvent> on loginBtn calls controller.goToLogin()
 *
 *   RegisterPanel sets up layout, styling, and event wiring; it relies on the injected
 *   RegisterController and MainApp to handle account creation logic and navigation
 *   whenever the panel is shown.
 */