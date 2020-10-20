package ui;

import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import filesystem.model.FileModel;
import filesystem.service.FileService;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import controller.AttrForFS;

public class Terminal extends Application {

    public static final String USER_NAME = "user";

    public VBox root;

    protected TextArea display;
    protected TextField input;

    protected ArrayList<String> history;

    protected FileModel current;

    public Terminal() {
        this.root = new VBox();

        this.display = new TextArea();
        this.input = new TextField();
        this.history = new ArrayList<String>();

        this.root.setPadding(new Insets(5, 5, 5, 5));
        this.root.setSpacing(5);

        VBox.setVgrow(this.display, Priority.ALWAYS);
        this.display.setEditable(false);
        this.display.setWrapText(true);
        this.display.setFont(Font.font("Consolas", 14));

        this.input.setFont(Font.font("Consolas", 14));
        this.input.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) {
                    String raw = Terminal.this.input.getText();
                    Terminal.this.putLine(raw);
                    Terminal.this.parseCommand(raw.substring(Terminal.this.getPrompt().length()));
                    Terminal.this.clearInput();
                }
            }
        });
        this.input.setTextFormatter(new TextFormatter<String>(new UnaryOperator<Change>() {
            @Override
            public Change apply(Change c) {
                String value = c.getControlNewText();
                if (value.startsWith(Terminal.this.getPrompt())) {
                    return c;
                } else {
                    java.awt.Toolkit.getDefaultToolkit().beep(); // deng!
                    return null;
                }
            }
            
        }));

        this.root.getChildren().add(this.display);
        this.root.getChildren().add(this.input);
    }

    public void putLine(String s) {
        this.display.appendText(s+"\n");
    }

    public void parseCommand(String s) {
        String[] command = s.split(" ");

        // for (String ss: command) {
        //     System.out.print(ss+", ");
        // }
        // System.out.println();
        switch (command[0]) {
            case "create": {
                this.putLine("you are creating something");
                if (command.length>1) {
                    String[] way = command[1].split("\\");
                    if (command[1].startsWith("\\")) {
                        for (Object o: FileService.getSubFiles(AttrForFS.getRoot())) {

                        }
                    }

                }
                break;
            }
            case "delete": {
                this.putLine("you are deleteing something");

                break;
            }
            case "ls": {
                this.putLine("you are listing something");

                break;
            }
            case "": {
                break;
            }
            default: {
                this.putLine(command[0]+": command not found");
            }
        }
    }

    public String getPrompt() {
        String dir = "/";
        FileModel traversal = this.current;
        while(this.current.getAttribute() != 3) {
            dir = dir + traversal.getName() + "/";
            traversal = traversal.getParentFile();
        }
        return Terminal.USER_NAME+"@"+main.Main.DISK+":"+dir+"$ ";
    }

    public void clearInput() {
        this.input.setText(this.getPrompt());
        this.input.selectEnd();
        this.input.deselect();
    }

    public void clearDisplay() {

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(this.root, 800, 480));
        primaryStage.show();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Terminal.this.input.requestFocus();
                Terminal.this.clearInput();
            }
        });

        AttrForFS.init();
        this.current = AttrForFS.getRoot();
        this.input.setText(this.getPrompt());
    }
    public static void main(String args[]) {
        launch(args);
    }
}
