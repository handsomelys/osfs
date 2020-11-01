package ui;

import java.io.IOException;

import filesystem.model.FileModel;
import filesystem.service.FileService;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.UIError;

public class Editor extends Application {

    @FXML
    private VBox vbox;

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu menubarFile;

    @FXML
    private MenuItem menubarFileSave;

    @FXML
    private MenuItem menubarFileSaveAndClose;

    @FXML
    private MenuItem menubarFileClose;

    @FXML
    private Menu menubarEdit;

    @FXML
    private MenuItem menubarMenuEditSelectAll;

    @FXML
    private MenuItem menubarMenuEditClear;

    @FXML
    private TextArea text;
    
    private Stage primaryStage;
    @FXML
    void handleClear(ActionEvent event) {
        this.text.setText("");
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
			FileService.editFileContent(Editor.this.current, this.text.getText());
		} catch (IOException e) {
			UIError.alertInformation("Warning!", "该文件只读，不可编辑", this.primaryStage);
		}
    }

    @FXML
    void handleSelectAll(ActionEvent event) {
        this.text.selectAll();
    }

    private FileModel current;

    public Editor(FileModel f) {
        this.current = f;
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/editor.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.primaryStage = primaryStage;
        this.text.setWrapText(true);
        Scene scene = new Scene(this.vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setTitle(this.current.getNormalName() + " - editor");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                primaryStage.close();
            }
        }); // do not save file content if clicking the close button

        this.menubarFileSaveAndClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Editor.this.handleSave(event);
                primaryStage.close();
            }
        });
        this.menubarFileClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });

        try {
			this.text.setText(FileService.getFileContent(this.current));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
