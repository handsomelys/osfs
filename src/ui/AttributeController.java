package ui;

import filesystem.model.FileModel;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AttributeController extends Application {
	@FXML
	private RadioButton readWrite;
	@FXML
	private RadioButton readOnly;
	@FXML
	private TextField type;
	@FXML
	private TextField size;
	@FXML
	private TextField startIndex;
	
	private ToggleGroup group;
	@Override
	public void start(Stage primaryStage) {
		
	}

	public void show(FileModel file) {
		if(file.getAttribute()==FileModel.DIRECTORY) {
			type.setText("Directory");
		}
		else if(file.getAttribute()==FileModel.FILE) {
			type.setText("File");
		}
		size.setText(String.valueOf(file.getSize()*64)+"bytes");
		group = new ToggleGroup();
		
		readWrite.setToggleGroup(group);
		if(file.isReadOnly()) {
			readOnly.setSelected(true);
			readWrite.setSelected(false);
		}
		else {
			readOnly.setSelected(false);
			readWrite.setSelected(true);
		}
		readOnly.setToggleGroup(group);
		
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(((ToggleButton) newValue).getText().equals("读写")) {
					file.setReadOnly(false);
				}
				else {
					file.setReadOnly(true);
				}
			}
		});
	}
	public static void main(String[] args) {
		launch(args);
	}
}
