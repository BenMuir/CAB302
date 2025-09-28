package com.typinggame.controller;

import com.typinggame.api.ApiClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PlayMenu extends Controller {

    private final ApiClient api = new ApiClient("http://127.0.0.1:18080");

    @FXML
    public void onAddCustomDrill(ActionEvent event) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add Custom Drill");

        TextField title = new TextField();
        title.setPromptText("Title");

        TextField tier = new TextField("1");

        TextArea content = new TextArea();
        content.setPromptText("Paste drill text here…");
        content.setPrefRowCount(10);

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("Title"), title);
        gp.addRow(1, new Label("Tier"), tier);
        gp.add(new Label("Content"), 0, 2);
        gp.add(content, 0, 3, 2, 1);

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dlg.showAndWait().filter(bt -> bt == ButtonType.OK).ifPresent(bt -> {
            try {
                int t = Integer.parseInt(tier.getText().trim());
                int id = api.createDrill(title.getText(), content.getText(), Math.max(1, t));
                // If you have a list on this view, refresh it here:
                // reloadDrills();
                new Alert(Alert.AlertType.INFORMATION, "Created drill #" + id).showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed: " + e.getMessage()).showAndWait();
            }
        });
    }

    public void learnModeButtonPressed(ActionEvent event) throws IOException {
        displayScene("/GameView.fxml", event);
    }

    public void raceModeButtonPressed(ActionEvent event) {}

    public void backButtonPressed(ActionEvent event) throws IOException {
        displayScene("/mainmenu.fxml", event);
    }
}
