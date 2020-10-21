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
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;
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

    public void parseCommand(String command) {
        String[] commands = command.split(" ");
        switch (commands[0]) {
            case "create": 
            case "touch": {
                if (commands.length>1) {
                    String[] path = commands[1].split("/");
                    FileModel destination = AttrForFS.getRoot();
                    if (commands[1].startsWith("/")) {
                        path = commands[1].substring(1).split("/");
                    } else {
                        destination = this.current;
                    }
                    boolean allFound = true;
                    for (int i = 0; i < path.length-1; ++i) {
                        boolean found = false;
                        FileModel next = null;
                        for (Object o: FileService.getSubFiles(destination)) {
                            next = (FileModel) o;
                            if (next.getName().equals(path[i])) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            destination = next;
                        } else {
                            allFound = false;
                            break;
                        }
                    }
                    if (allFound) {
                        String fileName = path[path.length-1];
                        int dot = fileName.lastIndexOf(".");
                        if (dot == -1) {
                            if (fileName.length()>3) {
                                this.putLine(fileName+": invalid file name (too long)");
                            } else {
                                FileService.createFile(destination, 1, fileName, ' ');
                            }
                        } else {
                            if (dot == fileName.length()-2) {
                                String name = fileName.substring(0, dot);
                                char extend = fileName.charAt(fileName.length()-1);
                                if (name.length()>3) {
                                    this.putLine(fileName+": invalid file name (too long)");
                                } else {
                                    FileService.createFile(destination, 1, name, extend);
                                }
                            } else {
                                this.putLine(fileName+": invalid file name (extension too long)");
                            }
                        }
                    } else {
                        String err = "/";
                        for (String s: path) {
                            err = err+s+"/";
                        }
                        this.putLine(err+": path no exist");
                    }
                } else {
                    FileService.createFile(this.current, 1);
                }
                break;
            }
            case "delete": {
                this.putLine("you are deleteing something");

                break;
            }
            case "dir":
            case "ls": {
                this.putLine("----Directory of "+this.current.getName()+"----");
                if (FileService.getSubFiles(this.current).size() == 0) {
                    this.putLine("It's quite empty here!");
                } else {
                    this.putLine("type  size  index  name");
                    String format = "%-4s  %-4d  %-5d  %-8s";
                    for (Object o: FileService.getSubFiles(this.current)) {
                        FileModel f = (FileModel) o;
                        this.putLine(String.format(format,
                            f.getAttribute()==1?"file":"dir",
                            f.getSize(),
                            f.getStartIndex(),
                            f.getNormalName()));
                    }
                }
                break;
            }
            case "exit": {
                DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
                Platform.exit();
            }
            case "": {
                break;
            }
            default: {
                this.putLine(commands[0]+": command not found");
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
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
            }
        });
    }
    public static void main(String args[]) {
        launch(args);
    }
}
