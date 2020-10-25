package ui;

import java.util.ArrayList;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.FATModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DiskViewer extends Application {
	private List<StackPane> disk;
	private Stage diskStage;

	public void show() {
		GridPane showPane = new GridPane();
		showPane = setDisk();
		HBox diskHbox = new HBox();
		diskHbox.getChildren().add(showPane);
		diskStage = newBlankStage(diskHbox);
		diskStage.show();
		update(AttrForFS.getFat().getTable());
	}
	
    public GridPane setDisk() {
        GridPane tmpPane = new GridPane();
        tmpPane.setStyle("-fx-background-color: #ffffff");
        tmpPane.setVgap(5.0D);
        tmpPane.setHgap(5.0D);
        tmpPane.setPadding(new Insets(1.0D, 1.0D, 1.0D, 1.0D));
        disk = new ArrayList<StackPane>();

        for(int i = 0; i < 128; ++i) {
            Text numberLabel = new Text(String.valueOf(i));
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(numberLabel);
            stackPane.setStyle("-fx-background-color: #c8c8c8");
            disk.add(i, stackPane);
            tmpPane.add(stackPane, i % 10, i / 10);
        }

        return tmpPane;
    }
    
	public void update(int[] fatTable) {

		for (int i = 0; i < fatTable.length; ++i) {
			if (fatTable[i] == 0) {
				((StackPane) this.disk.get(i)).setStyle("-fx-background-color: #c8c8c8");

			} else {
				((StackPane) this.disk.get(i)).setStyle("-fx-background-color: #ff0000");
			}
		}
	}
	
	public static Stage newBlankStage(Pane showPane) {
        Stage stage = new Stage();
        // stage.initOwner(StartFileSystem.rootStage);
        Scene scene = new Scene(showPane);
        stage.setScene(scene);
        return stage;
    }

	@Override
	public void start(Stage primaryStage) {
//		GridPane diskpane = new GridPane();
//		List<StackPane> disk;
//	    diskpane.setVgap(5.0D);
//	    diskpane.setHgap(5.0D);
//	    diskpane.setPadding(new Insets(1.0D, 1.0D, 1.0D, 1.0D));
//	    disk = new ArrayList<StackPane>();
//	    
//	    for(int i = 0; i < 128; ++i) {
//            Text numberLabel = new Text(String.valueOf(i));
//            StackPane stackPane = new StackPane();
//            stackPane.getChildren().add(numberLabel);
//            stackPane.setStyle("-fx-background-color: #c8c8c8");
//            disk.add(i, stackPane);
//            diskpane.add(stackPane, i % 10, i / 10);
//        }
//	    FATModel fat = new FATModel();
//	    int []fatTable = fat.getTable();
//	    for(int i = 0; i < fatTable.length; ++i) {
//            if(fatTable[i]==0){
//                ((StackPane)disk.get(i)).setStyle("-fx-background-color: #c8c8c8");
//
//            }else {
//                ((StackPane)disk.get(i)).setStyle("-fx-background-color: #ff0000");
//            }
//        }
//
//	    Scene scene = new Scene(diskpane);
//	    primaryStage.setScene(scene);
//	    primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
