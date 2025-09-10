package com.typinggame.controller;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginPage{
    @FXML public Button loginButton;
    public void handleLogin(){

    }

    public void setLoginButton()  {

        Image image = new Image(getClass().getResourceAsStream("Button.png"));
        ImageView imageView = new ImageView(image);

        loginButton.setText("Login");
        loginButton.setStyle("-fx-background-color: transparent;");
        loginButton.setGraphic(imageView);


        // Add an event click to the button
        loginButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                // Input logic for handling the event
            }
        });

    }
}


