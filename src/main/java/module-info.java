module com.typinggame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.typinggame.controller to javafx.fxml;
    exports com.typinggame;
}