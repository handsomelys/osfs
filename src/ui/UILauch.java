package ui;

import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
// import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UILauch extends Application {

	@Override
	public void start(Stage primaryStage) {
		TreeItem<String> rootItem = new TreeItem<>("Root");
		rootItem.setExpanded(true);
		TreeItem<String> item = new TreeItem<>("A");
		rootItem.getChildren().add(item);
		item = new TreeItem<>("B");
		rootItem.getChildren().add(item);
		TreeView<String> tree = new TreeView<>(rootItem);
		StackPane root = new StackPane();
		root.getChildren().add(tree);
		primaryStage.setScene(new Scene(root,300,250));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
