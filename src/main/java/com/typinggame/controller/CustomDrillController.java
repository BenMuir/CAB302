package com.typinggame.controller;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/**
 * CustomDrillController handles creation of user-defined drills.
 * Provides UI logic for saving/canceling custom drills and tier selection.
 */
public class CustomDrillController extends Controller {

    // --- UI components ---
    @FXML private TextField titleField;   // Input for drill title
    @FXML private ComboBox<Integer> tierBox; // Dropdown for selecting tier
    @FXML private TextArea contentArea;   // Input for drill text/content

    /**
     * Initialize UI state when the view loads.
     * Populates tierBox with allowed tiers, capped by DB max and VISIBLE_TIER_CAP.
     */
    @FXML
    private void initialize() {
        final int VISIBLE_TIER_CAP = 5; // Max tiers user can ever see (hard cap)

        int dbMax;
        try {
            // Ask DB for the highest tier that exists
            dbMax = new DrillRepository().maxTier();
        } catch (Exception ignored) {
            dbMax = 1; // fallback if DB not ready
        }

        // Only show up to the smaller of DB max vs cap
        int maxToShow = Math.min(dbMax, VISIBLE_TIER_CAP);

        tierBox.getItems().clear();
        for (int i = 1; i <= maxToShow; i++) {
            tierBox.getItems().add(i);
        }
        tierBox.getSelectionModel().selectFirst();
    }

    /**
     * Handle Save button.
     * Validates fields, inserts drill into DB, and returns to PlayMenu.
     */
    @FXML
    private void onSave(ActionEvent event) {
        // Read and sanitize inputs
        String title   = titleField.getText() == null ? "" : titleField.getText().trim();
        Integer tier   = tierBox.getValue();
        String content = contentArea.getText() == null ? "" : contentArea.getText().trim();

        // Validation
        if (title.isEmpty()) { toast("Please enter a title."); return; }
        if (tier == null)    { toast("Please choose a tier."); return; }
        if (content.isEmpty()){ toast("Please add some drill content."); return; }

        try {
            // Build Drill object and save
            Drill d = new Drill(0, title, content, tier);
            new DrillRepository().insertCustom(d);

            toast("Custom drill saved.");
            displayScene("/playmenu.fxml", event); // Go back to PlayMenu
        } catch (Exception ex) {
            error("Failed to save the custom drill:\n" + ex.getMessage());
        }
    }

    /**
     * Handle Cancel button.
     * Discards input and returns to PlayMenu.
     */
    @FXML
    private void onCancel(ActionEvent event) {
        displayScene("/playmenu.fxml", event);
    }

    /** Helper to show informational popup. */
    private void toast(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /** Helper to show error popup. */
    private void error(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }
}
