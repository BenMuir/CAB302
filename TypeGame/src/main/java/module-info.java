module com.typinggame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.typinggame.controller to javafx.fxml;
    exports com.typinggame;
}