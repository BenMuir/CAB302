package com.typinggame.controller;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/**
 * CustomDrillController handles creation of user-defined drills.
 * Works with FXML using either fx:id="levelBox" or fx:id="tierBox".
 */
public class CustomDrillController extends Controller {

    // --- UI components ---
    @FXML private TextField titleField;
    @FXML private TextArea  contentArea;

    // Support both old and new fx:id
    @FXML private ComboBox<Integer> levelBox; // preferred new id
    @FXML private ComboBox<Integer> tierBox;  // legacy id

    /** Helper: return whichever ComboBox is present in FXML. */
    private ComboBox<Integer> levelChooser() {
        return (levelBox != null) ? levelBox : tierBox;
    }

    @FXML
    private void initialize() {
        final ComboBox<Integer> box = levelChooser();
        if (box == null) {
            // Defensive: give a clear error if neither fx:id exists
            throw new IllegalStateException("Neither 'levelBox' nor 'tierBox' is defined in customdrillview.fxml");
        }

        final int MAX_LEVEL = 10;
        box.getItems().clear();
        for (int i = 1; i <= MAX_LEVEL; i++) {
            box.getItems().add(i);
        }
        box.getSelectionModel().selectFirst();
    }

    @FXML
    private void onSave(ActionEvent event) {
        final ComboBox<Integer> box = levelChooser();

        String title   = titleField.getText()   == null ? "" : titleField.getText().trim();
        String content = contentArea.getText()  == null ? "" : contentArea.getText().trim();
        Integer level  = (box == null) ? null : box.getValue();

        if (title.isEmpty())   { toast("Please enter a title."); return; }
        if (level == null)     { toast("Please choose a level."); return; }
        if (content.isEmpty()) { toast("Please add some drill content."); return; }

        try {
            Drill d = new Drill(0, title, content, level);
            new DrillRepository().insertCustom(d);
            toast("Custom drill saved.");
            displayScene("/playmenu.fxml", event);
        } catch (Exception ex) {
            error("Failed to save the custom drill:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
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
