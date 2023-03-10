package me.delta.csviewj.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class CSViewJ extends Application {

    //It all starts here
    public static void main(String[] args) {
        launch();
    }

    //Loads the fxml-resource file, sets the controller-class and sets the scene.
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CSViewJ.class.getResource("main-view.fxml"));
        fxmlLoader.setController(new MainController(stage));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("CSViewJ - Idle");
        stage.getIcons().add(new Image(CSViewJ.class.getResourceAsStream("icon.png")));
        stage.setScene(scene);
        stage.show();

    }
}