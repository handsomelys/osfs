package ui;

import java.net.URL;
import java.util.ResourceBundle;

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

	public void initiate(FileModel file) {
		;
	}
	
	//带参数的话 不能bind到fxml中
	public void editFileContent(FileModel file) {
		
		save.setOnAction((ActionEvent t) -> {
            FileService.editFileContent(file,textArea.getText());
        });
		
		String text = FileService.getFileContent(file);
		textArea.setText(text);
		//如果按了x 默认保留文本内容
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
                String content = textArea.getText();
                   FileService.editFileContent(file, content);//更新文件内容
                   stage.close();
            }
        });
        stage.show();
	}
	
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
