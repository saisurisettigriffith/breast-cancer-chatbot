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

public class LoginPanel {

    private final LoginController controller;
    private final Pane pane;
    private final TextField emailFld;
    private final PasswordField passFld;
    private final Button loginBtn;
    private final Button registerBtn;
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
        } catch (Exception ex) {

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

    public LoginPanel(LoginController controller) {
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
                        "-fx-border-width: 1px;");

        loginBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                loginBtn.setBackground(new Background(new BackgroundFill(
                        Color.web("#c2c2c2"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        loginBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                loginBtn.setBackground(new Background(new BackgroundFill(
                        Color.web("#ffffff"), new CornerRadii(4.0), Insets.EMPTY)));
            }
        });
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String email = emailFld.getText().trim();
                String pass = passFld.getText();
                String err = controller.onLogin(email, pass);
                msgLabel.setText(err != null ? err : "");
            }
        });

        emailFld.setOnAction(e -> loginBtn.fire());
        passFld.setOnAction(e -> loginBtn.fire());

        registerBtn = new Button("Need an account? Register");
        registerBtn.setLayoutX(553.0);
        registerBtn.setLayoutY(285.0);
        registerBtn.setFont(loadFont(12.0));
        registerBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #1b1b1b; " +
                        "-fx-underline: true;");
        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controller.goToRegister();
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
                loginBtn, registerBtn,
                msgLabel);
    }

    public Pane getPane() {
        return pane;
    }
}

/**
 * LoginPanel builds the JavaFX UI for user login: styling, fields, buttons, and logo.
 *
 *   constructor and getPane() are called by:
 *       MainApp.buildScreens()
 *
 *   Once MainApp embeds this Pane into a JFXPanel and displays it,
 *   JavaFX’s event handling takes over:
 *     - EventHandler<ActionEvent> on loginBtn calls controller.onLogin(...)
 *     - EventHandler<ActionEvent> on registerBtn calls controller.goToRegister()
 *
 *   The LoginPanel itself only sets up layout, styling, and event wiring;
 *   it relies on the injected LoginController and MainApp to handle all
 *   authentication logic and navigation whenever the panel is shown.
 */
