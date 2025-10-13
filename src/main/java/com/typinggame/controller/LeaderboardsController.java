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
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.List;

public class LeaderboardsController extends Controller { // <-- extend Controller

    @FXML private Button backButton;
    @FXML private ComboBox<Drill> drillSelect;
    @FXML private TableView<LeaderboardService.Row> table;
    @FXML private TableColumn<LeaderboardService.Row, String> colName;
    @FXML private TableColumn<LeaderboardService.Row, Number> colScore;
    @FXML private  TableColumn<LeaderboardService.Row, Image> colRank;

    private LeaderboardService leaderboardService;
    private DrillService drillService;

    @FXML
    public void initialize(){
        this.leaderboardService = new LeaderboardService(new LeaderboardRepository());
        this.drillService = new DrillService(new DrillRepository(), new ProgressService());

        customizeColumnHeader(colRank, "Rank");
        customizeColumnHeader(colName, "User");
        customizeColumnHeader(colScore, "Score (WPM×Acc)");

        User current = AppContext.userManager.getCurrentUser();
        int userId = current.getUserID();

        List<Drill> unlocked = drillService.listUnlocked(userId);
        drillSelect.setItems(FXCollections.observableArrayList(unlocked));
        if (!unlocked.isEmpty()) {
            drillSelect.getSelectionModel().selectFirst();
        }

        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colScore.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().score));

        colRank.setCellValueFactory(c -> {
            var rank = Rank.forTypingSpeed(c.getValue().wpm);
            Image rankImage = RankLoader.loadIcon(rank);
            return new SimpleObjectProperty<>(rankImage);
        });

        colRank.setCellFactory(col -> new TableCell<LeaderboardService.Row, Image>() {
            private final ImageView view = new ImageView();
            {
                view.setPreserveRatio(true);
                view.setFitHeight(128);
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

    private void customizeColumnHeader(TableColumn<?, ?> column, String heading) {
        Label label = new Label(heading);
        label.setFont(Font.font("Press Start 2P Regular", 18));
        label.setStyle("-fx-text-fill: black;");
        column.setGraphic(label);
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
        displayScene("/MainMenuView.fxml", event);
    }

}
