package com.example.api.musicapi;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private static final String BASE_URL = "https://musicbrainz.org/ws/2/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MusicBrainz API Example");

        TableView<Artist> artistTableView = new TableView<>();

        // Define columns
        TableColumn<Artist, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Artist, String> countryColumn = new TableColumn<>("Country");
        countryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCountry()));

        TableColumn<Artist, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));

        artistTableView.getColumns().addAll(nameColumn, countryColumn, typeColumn);

        // Set the preferred width and height to USE_COMPUTED_SIZE
        artistTableView.setPrefWidth(Region.USE_COMPUTED_SIZE);
        artistTableView.setPrefHeight(Region.USE_COMPUTED_SIZE);

        // Set maxWidth and maxHeight to limit the TableView size to its contents
        artistTableView.setMaxWidth(Double.MAX_VALUE);
        artistTableView.setMaxHeight(Double.MAX_VALUE);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER); // Center the VBox content
        vbox.setSpacing(10); // Add some spacing between elements

        TextField searchField = new TextField();
        searchField.setAlignment(Pos.CENTER);
        Button searchButton = new Button("Search");
        searchButton.setAlignment(Pos.CENTER);

        searchButton.setOnAction(e -> {
            String query = searchField.getText();
            List<Artist> artists = searchArtists(query);
            artistTableView.getItems().clear();
            artistTableView.getItems().addAll(artists);
        });

        vbox.getChildren().addAll(searchField, searchButton, artistTableView);

        Scene scene = new Scene(vbox, 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private List<Artist> searchArtists(String query) {
        List<Artist> artists = new ArrayList<>();

        try {
            String url = BASE_URL + "artist?query=" + query + "&fmt=json&limit=10";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("artists")) {
                    JSONArray jsonArray = jsonResponse.getJSONArray("artists");
                    for (Object obj : jsonArray) {
                        JSONObject jsonObject = (JSONObject) obj;
                        String name = jsonObject.getString("name");
                        String country = jsonObject.has("country") ? jsonObject.getString("country") : "N/A";
                        String type = jsonObject.getString("type");
                        artists.add(new Artist(name, country, type));
                    }

                    System.out.println(artists.get(3).getName());
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return artists;
    }

    private static class Artist {
        private final String name;
        private final String country;
        private final String type;

        public Artist(String name, String country, String type) {
            this.name = name;
            this.country = country;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public String getType() {
            return type;
        }
    }
}

