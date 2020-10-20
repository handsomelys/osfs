package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import controller.AttrForFS;
import ui.Terminal;

public class Main extends Application {

    public static final String DISK = "disk.dat";

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent explorer = FXMLLoader.load(getClass().getResource("/ui/explorer.fxml"));
            primaryStage.setScene(new Scene(explorer, 900, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        (new Terminal()).start(new Stage());
        try {
            Parent editer = FXMLLoader.load(getClass().getResource("/ui/editor.fxml"));
            Scene scene = new Scene(editer,500,522);
            Stage editorStage = new Stage();
            editorStage.setScene(scene);
            editorStage.setResizable(true);
            editorStage.setTitle("editor");
            editorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws CloneNotSupportedException {
        AttrForFS.init();
        launch(args);
    }
}
