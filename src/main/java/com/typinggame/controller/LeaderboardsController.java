package com.typinggame.controller;

import com.typinggame.config.AppContext;
import com.typinggame.data.DrillRepository;
import com.typinggame.data.LeaderboardRepository;
import com.typinggame.data.User;
import com.typinggame.model.Drill;
import com.typinggame.service.DrillService;
import com.typinggame.service.LeaderboardService;
import com.typinggame.service.ProgressService;
import com.typinggame.util.Rank;
import com.typinggame.util.RankLoader;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent; // <-- important
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

public class LeaderboardsController extends Controller { // <-- extend Controller

    @FXML private Button backButton;
    @FXML private ComboBox<Drill> drillSelect;
    @FXML private TableView<LeaderboardService.Row> table;
    @FXML private TableColumn<LeaderboardService.Row, String> colName;
    @FXML private TableColumn<LeaderboardService.Row, Number> colWpm;
    @FXML private TableColumn<LeaderboardService.Row, Number> colAcc;
    @FXML private TableColumn<LeaderboardService.Row, Number> colScore;
    @FXML private  TableColumn<LeaderboardService.Row, Image> colRank;

    private LeaderboardService leaderboardService;
    private DrillService drillService;

    @FXML
    public void initialize(){
        this.leaderboardService = new LeaderboardService(new LeaderboardRepository());
        this.drillService = new DrillService(new DrillRepository(), new ProgressService());

        User current = AppContext.userManager.getCurrentUser();
        int userId = current.getUserID(); // your User class exposes getUserID()

        List<Drill> unlocked = drillService.listUnlocked(userId);
        drillSelect.setItems(FXCollections.observableArrayList(unlocked));
        if (!unlocked.isEmpty()) {
            drillSelect.getSelectionModel().selectFirst();
        }

        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colWpm.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().wpm));
        colAcc.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().accuracy));
        colScore.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().score));

        colRank.setCellValueFactory(c -> {
            var rank = Rank.forTypingSpeed(c.getValue().wpm);
            Image rankImage = RankLoader.load(rank);
            return new SimpleObjectProperty<>(rankImage);
        });

        colRank.setCellFactory(col -> new TableCell<LeaderboardService.Row, Image>() {
            private final ImageView view = new ImageView();
            {
                view.setPreserveRatio(true);
                view.setFitHeight(24);  // adjust to taste
                view.setSmooth(true);
            }

            @Override
            protected void updateItem(Image img, boolean empty) {
                super.updateItem(img, empty);
                if (empty || img == null) {
                    setGraphic(null);
                } else {
                    view.setImage(img);
                    setGraphic(view);
                }
            }
        });
        drillSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> refreshTable());
        refreshTable();
    }

    private void refreshTable(){
        Drill selected = drillSelect.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        List<LeaderboardService.Row> rows = leaderboardService.topByBestScoreForDrill(selected.id, 50);
        ObservableList<LeaderboardService.Row> data = FXCollections.observableArrayList(rows);
        table.setItems(data);
    }

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        // Use the same helper that applies the StackPane + scale binding
        // IMPORTANT: match your actual resource name/case
        displayScene("/mainmenu.fxml", event);
    }

}
