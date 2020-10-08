package ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
public class UIController {
	
	@FXML
	private MenuBar topMenuBar;
	@FXML
	private MenuItem new_item;
	
	@FXML
	void newItem(ActionEvent event) {
		System.out.println("you have click it!!");
	}
}
