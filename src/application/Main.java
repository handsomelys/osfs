package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
// import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import filesystem.model.DiskModel;
import filesystem.model.FileModel;
import util.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        try {
            root = FXMLLoader.load(getClass().getResource("/ui/ui.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        FileModel f = new FileModel("abc", 'e', 4, 5);
        String[] s = TypeTransfrom.bytesToBinaryStrings(f.itemToBytes());
        for (int i = 0; i < 8; ++i) {
            Text t = new Text(100, 100+20*i, s[i]);
            t.setRotate(30);
            root.getChildren().add(t);
        }

        DiskModel d = new DiskModel();
        d.saveToFile("disk.dat");

        d.getDiskTable()[5] = f;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
