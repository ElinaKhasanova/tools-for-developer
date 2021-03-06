package ru.kpfu.elina;

import ru.kpfu.elina.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main {

    public static void main(String[] args) {
        App.main(args);
    }

    public static class App extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            primaryStage.setTitle("Development tools");
            primaryStage.getIcons().add(new Image("/images/icons8-toolbox-64.png"));
            Scene scene = new Scene(root, 900, 500);
            scene.getStylesheets().addAll("/css/main-dark.css", "/css/json-highlighting-dark.css");
            primaryStage.setScene(scene);
            mainController.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}