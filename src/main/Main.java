package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

import controller.AttrForFS;
import filesystem.service.DiskService;
import src.ui.Terminal;


public class Main extends Application {

    public static final String DISK = "disk.dat";

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent explorer = FXMLLoader.load(getClass().getResource("/src/ui/explorer.fxml"));
            primaryStage.setScene(new Scene(explorer, 900, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Parent editer = FXMLLoader.load(getClass().getResource("/src/ui/editor.fxml"));
            Scene scene = new Scene(editer,500,522);
            Stage editorStage = new Stage();
            editorStage.setScene(scene);
            editorStage.setResizable(true);
            editorStage.setTitle("editor");
            editorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        (new Terminal()).start(new Stage());
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
            }
        });
    }
    public static void main(String[] args) throws CloneNotSupportedException {
        AttrForFS.init();
        launch(args);
    }

}
