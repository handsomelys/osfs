package ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.FileService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    @FXML
    void handleClear(ActionEvent event) {
        this.text.setText("");
    }

    @FXML
    void handleSave(ActionEvent event) {
        FileService.editFileContent(Editor.this.current, this.text.getText());
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

        this.text.setText(FileService.getFileContent(this.current));
    }
}
