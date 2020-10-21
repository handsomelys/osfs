package ui;

import java.io.IOException;

import filesystem.model.FileModel;
import filesystem.service.FileService;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



public class Editor extends Application {

	@FXML
	private VBox vbox;
	@FXML
	private TextArea textArea;
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem close;

	private FileModel file;
	
	private static Stage stage;

	public Editor(FileModel f) {
		this.file = f;

		String text = FileService.getFileContent(file);
		this.textArea.setText(text);

		this.save.setOnAction((ActionEvent t) -> {
            FileService.editFileContent(file,textArea.getText());
        });
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
        try {
            Parent editor = FXMLLoader.load(getClass().getResource("/ui/editor.fxml"));
            Scene scene = new Scene(editor, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setTitle(this.file.getNormalName()+" - editor");
			primaryStage.show();
			
			//如果按了x 默认保留文本内容
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent windowEvent) {
					windowEvent.consume();
					String content = textArea.getText();
					FileService.editFileContent(file, content);//更新文件内容
					stage.close();
				}
			});
        } catch (IOException e) {
            e.printStackTrace();
		}
	}

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
