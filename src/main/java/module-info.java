module com.example.api.musicapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.json;
    requires java.net.http;
    requires java.xml;


    opens com.example.api.musicapi to javafx.fxml;
    exports com.example.api.musicapi;
}