package util;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UIError {
	 public static void alertInformation(String header, String message,Stage rootStage){
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Message");
	        alert.setHeaderText(header);
	        alert.setContentText(message);
	        alert.initOwner(rootStage);
	        alert.show();
	    }
}
