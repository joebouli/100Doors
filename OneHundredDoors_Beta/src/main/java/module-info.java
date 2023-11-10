module com.example.onehundreddoors {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires opencv;

    opens com.example.onehundreddoors to javafx.fxml;
    exports com.example.onehundreddoors;
}