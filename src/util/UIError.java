package util;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UIError extends Application {
	 public static void alertInformation(String header, String message,Stage rootStage){
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Message");
	        alert.setHeaderText(header);
	        alert.setContentText(message);
	        alert.initOwner(rootStage);
	        alert.show();
	    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		;
	}
	public static void main(String[] args) {
		launch(args);
	}
}
