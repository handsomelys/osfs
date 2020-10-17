package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UILauch extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = new Pane();
        try {
            root = FXMLLoader.load(getClass().getResource("/ui/ui.fxml"));
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    //     launch(args);
    // }
}
