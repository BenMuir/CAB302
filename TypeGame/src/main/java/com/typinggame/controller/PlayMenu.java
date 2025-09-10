package com.typinggame.controller;


import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PlayMenu extends Controller {
    public ImageView learnButton;

    public void learnModeButtonPressed(MouseEvent mouseEvent) throws IOException {
        displayScene("typinggame.fxml", mouseEvent);
    }
}
