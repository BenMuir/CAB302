package com.typinggame.controller;

import com.typinggame.config.AppContext;
import com.typinggame.data.DrillRepository;
import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.UserManager;
import com.typinggame.model.Drill;
import com.typinggame.service.ProgressService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

/**
 * Selects difficulty (levels 1–10) and locks higher ones until previous are completed.
 */
public class DifficultySelectController extends Controller {

    private final DrillRepository drillRepo = new DrillRepository();
    private final ProgressService progress  = new ProgressService();

    @FXML private Button btnLevel1, btnLevel2, btnLevel3, btnLevel4, btnLevel5;
    @FXML private Button btnLevel6, btnLevel7, btnLevel8, btnLevel9, btnLevel10;

    @FXML
    public void initialize() {
        int userId = resolveUserId();
        int unlockedUpTo = progress.unlockedUpTo(userId);
        System.out.println("[DifficultySelect] userId=" + userId + " unlockedUpTo=" + unlockedUpTo);

        // Disable buttons beyond unlocked level
        for (int level = 1; level <= 10; level++) {
            Button b = getButton(level);
            if (b != null) {
                boolean locked = level > unlockedUpTo;
                b.setDisable(locked);
                b.setOpacity(locked ? 0.5 : 1.0);
            }
        }
    }

    private Button getButton(int i) {
        return switch (i) {
            case 1 -> btnLevel1;
            case 2 -> btnLevel2;
            case 3 -> btnLevel3;
            case 4 -> btnLevel4;
            case 5 -> btnLevel5;
            case 6 -> btnLevel6;
            case 7 -> btnLevel7;
            case 8 -> btnLevel8;
            case 9 -> btnLevel9;
            case 10 -> btnLevel10;
            default -> null;
        };
    }

    @FXML public void onBack(ActionEvent e) { displayScene("/playmenu.fxml", e); }

    @FXML public void onPick1(ActionEvent e){ pickLevel(1, e); }
    @FXML public void onPick2(ActionEvent e){ pickLevel(2, e); }
    @FXML public void onPick3(ActionEvent e){ pickLevel(3, e); }
    @FXML public void onPick4(ActionEvent e){ pickLevel(4, e); }
    @FXML public void onPick5(ActionEvent e){ pickLevel(5, e); }
    @FXML public void onPick6(ActionEvent e){ pickLevel(6, e); }
    @FXML public void onPick7(ActionEvent e){ pickLevel(7, e); }
    @FXML public void onPick8(ActionEvent e){ pickLevel(8, e); }
    @FXML public void onPick9(ActionEvent e){ pickLevel(9, e); }
    @FXML public void onPick10(ActionEvent e){ pickLevel(10, e); }

    private void pickLevel(int level, ActionEvent e) {
        int userId = resolveUserId();
        int unlockedUpTo = progress.unlockedUpTo(userId);
        if (level > unlockedUpTo) {
            System.err.println("[DifficultySelect] Level " + level + " locked (unlockedUpTo=" + unlockedUpTo + ")");
            return;
        }

        AppContext.get().setSelectedTier(level);

        List<Drill> drillsInLevel = drillRepo.findByLevel(level);
        if (drillsInLevel == null || drillsInLevel.isEmpty()) {
            System.err.println("[DifficultySelect] No drills for level " + level);
            return;
        }
        AppContext.get().setSelectedDrillId(drillsInLevel.get(0).id);
        displayScene("/GameView.fxml", e);
    }

    /**
     * Robust userId resolution (matches TypingGameController):
     * 1) AppContext (preferred instance getter)
     * 2) Legacy static AppContext.userManager (back-compat)
     * 3) New UserManager(SqliteUserRepository) fallback
     */
    private int resolveUserId() {
        try {
            UserManager um = AppContext.get().getUserManager();
            if (um != null && um.getCurrentUser() != null)
                return um.getCurrentUser().getUserID();
        } catch (Throwable ignore) {}

        try {
            // Back-compat: legacy static (kept in AppContext for older code paths)
            if (AppContext.userManager != null && AppContext.userManager.getCurrentUser() != null)
                return AppContext.userManager.getCurrentUser().getUserID();
        } catch (Throwable ignore) {}

        try {
            UserManager um = new UserManager(new SqliteUserRepository());
            if (um.getCurrentUser() != null)
                return um.getCurrentUser().getUserID();
        } catch (Throwable ignore) {}

        return 0;
    }
}
