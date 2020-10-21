package ui;

import filesystem.model.FileModel;
import filesystem.service.FileService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



public class Editor {
	@FXML
	private VBox vbox;
	@FXML
	private TextArea textArea;
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem close;
	
	private static Stage stage;

	public void save(ActionEvent t) {
		System.out.println("保存成功");
	}
	
	//edit the file content
	public void editFileContent(FileModel file) {
		
		save.setOnAction((ActionEvent t) -> {
            FileService.editFileContent(file,textArea.getText());
        });
		
		String text = FileService.getFileContent(file);
		textArea.setText(text);
		//click the 'X' to close the window
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
                String content = textArea.getText();
                   FileService.editFileContent(file, content);
                   stage.close();
            }
        });
        stage.show();
	}

	//show the file content
	public void showFileContent(FileModel file) {
		String content = FileService.getFileContent(file);
		textArea.setText(content);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
                stage.close();
            }
        });
        stage.show();
	}
	
	/*
	 * public void initialize(URL location,ResourceBundle resources) {
	 * textArea.prefWidthProperty().bind(vbox.widthProperty()); }
	 */
}
