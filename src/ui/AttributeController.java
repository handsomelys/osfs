package ui;

import java.io.IOException;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
	@FXML
	private TextField attribute;
	@FXML
	private AnchorPane propertyScene;
	
	private ToggleGroup group;
	private Stage primaryStage;
	private FileModel file;
	

	public AttributeController(FileModel file) {
		this.file = file;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AttributeSetter.fxml"));
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.show();
		Scene scene = new Scene(this.propertyScene);
		this.primaryStage.setScene(scene);
		this.primaryStage.setResizable(false);
		this.primaryStage.show();
		
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent windowEvent) {
				DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
				primaryStage.close();
			}
		}); // save file system if clicking the close button
		
	}


	public void show() {
		if (file.getAttribute() == FileModel.DIRECTORY) {
			type.setText("Directory");
		} else if (file.getAttribute() == FileModel.FILE) {
			type.setText("File");
		}
		type.setEditable(false);
		size.setText(String.valueOf(file.getSize() * 64) + "bytes");
		size.setEditable(false);
		startIndex.setText(String.valueOf(file.getStartIndex()));
		startIndex.setEditable(false);
		
		if(file.getType()==FileModel.EXE) {
			attribute.setText("EXE");
		}
		else {
			attribute.setText("NORMAL");
		}
		attribute.setEditable(false);
		
		group = new ToggleGroup();

		readWrite.setToggleGroup(group);
		if (file.isReadOnly()) {
			readOnly.setSelected(true);
			readWrite.setSelected(false);
		} else {
			readOnly.setSelected(false);
			readWrite.setSelected(true);
		}
		readOnly.setToggleGroup(group);

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (((ToggleButton) newValue).getText().equals("读写")) {
					file.setReadOnly(false);
				} else {
					file.setReadOnly(true);
				}
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
