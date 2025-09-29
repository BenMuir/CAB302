package com.typinggame.controller;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class CustomDrillController extends Controller {

    @FXML private TextField titleField;
    @FXML private ComboBox<Integer> tierBox;
    @FXML private TextArea contentArea;

    @FXML
    private void initialize() {
        for (int i = 1; i <= 5; i++) tierBox.getItems().add(i);
        tierBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onSave(ActionEvent event) {
        String title   = titleField.getText() == null ? "" : titleField.getText().trim();
        Integer tier   = tierBox.getValue();
        String content = contentArea.getText() == null ? "" : contentArea.getText().trim();

        if (title.isEmpty()) { toast("Please enter a title."); return; }
        if (tier == null)    { toast("Please choose a tier."); return; }
        if (content.isEmpty()){ toast("Please add some drill content."); return; }

        try {
            Drill d = new Drill(0, title, content, tier);
            new DrillRepository().insertCustom(d);

            toast("Custom drill saved.");
            // pass the ActionEvent so Controller.displayScene(...) has a non-null event
            displayScene("/playmenu.fxml", event);
        } catch (Exception ex) {
            error("Failed to save the custom drill:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        // also pass the event to avoid NPE
        displayScene("/playmenu.fxml", event);
    }

    private void toast(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void error(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }
}
