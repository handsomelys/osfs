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

import java.io.IOException;
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

    /**
     * start a new terminal interface on {@code root} directory.
     * <p>use <pre>{@code (new Terminal()).start(new Stage())}</pre> to get a terminal!</p>
     */
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

    /**
     * put a line in the terminal
     * @param s some string
     */
    public void putLine(String s) {
        this.display.appendText(s+"\n");
    }

    /**
     * to eval the command
     * @param command the command
     */
    public void parseCommand(String command) {
        String[] commands = command.split(" ");
        switch (commands[0]) {
            case "create": 
            case "touch": {
                // create file
                try {
                    if (commands.length>1) {
                        if (commands[1].startsWith("/")) {
                            // absolute path
                            int lastDash = commands[1].lastIndexOf("/");
                            if (lastDash == 0) {
                                FileService.createFile(AttrForFS.getRoot(), commands[1].substring(1));
                            } else {
                                FileService.createFile(FileService.getFileTraversal(commands[1].substring(1, lastDash)), commands[1].substring(lastDash+1));
                            }
                        } else {
                            // relative path
                            int lastDash = commands[1].lastIndexOf("/");
                            if (lastDash == -1) {
                                FileService.createFile(AttrForFS.getRoot(), commands[1]);
                            } else {
                                FileService.createFile(FileService.getFileTraversal(this.current, commands[1].substring(0, lastDash)), commands[1].substring(lastDash+1));
                            }
                        }
                    } else {
                        FileService.createNew(this.current, FileModel.FILE);
                    }
                } catch (IOException e) {
                    this.putLine(e.getMessage());
                }
                break;
            }
            case "delete": {
                // delete file
                if (commands.length>1) {
                    try {
                        if (commands[1].startsWith("/")) {
                        // absolute path
                            FileService.removeFile(FileService.getFileTraversal(commands[1].substring(1)));
                        } else {
                        // relative path
                            FileService.removeFile(FileService.getFileTraversal(this.current, commands[1]));
                        }
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                } else {
                    this.putLine("delete: no file specified");
                }
                break;
            }
            case "mkdir": {
                // make directory
                try {
                    if (commands.length>1) {
                        if (commands[1].startsWith("/")) {
                            // absolute path
                            int lastDash = commands[1].lastIndexOf("/");
                            if (lastDash == 0) {
                                FileService.createDirectory(AttrForFS.getRoot(), commands[1].substring(1));
                            } else {
                                FileService.createDirectory(FileService.getFileTraversal(commands[1].substring(1, lastDash)), commands[1].substring(lastDash+1));
                            }
                        } else {
                            // relative path
                            int lastDash = commands[1].lastIndexOf("/");
                            if (lastDash == -1) {
                                FileService.createDirectory(AttrForFS.getRoot(), commands[1]);
                            } else {
                                FileService.createDirectory(FileService.getFileTraversal(this.current, commands[1].substring(0, lastDash)), commands[1].substring(lastDash+1));
                            }
                        }
                    } else {
                        FileService.createNew(this.current, FileModel.DIRECTORY);
                    }
                } catch (IOException e) {
                    this.putLine(e.getMessage());
                }
                break;
            }
            case "rmdir": {
                // remove directory
                if (commands.length>1) {
                    try {
                        if (commands[1].startsWith("/")) {
                            // absolute path
                            FileService.removeDir(FileService.getFileTraversal(commands[1].substring(1)));
                        } else {
                            // relative path
                            FileService.removeDir(FileService.getFileTraversal(this.current, commands[1]));
                        }
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                } else {
                    this.putLine("rmdir: no file specified");
                }
                break;
            }
            case "cd": {
                if (commands.length>1) {
                    if (commands[1].startsWith("/")) {
                        this.current = FileService.getFile(AttrForFS.getRoot(), commands[1].substring(1));
                    } else if(commands[1].equals("..")) {
                        this.current = this.current.getParentFile();
                    } else {
                        if (commands[1].contains("..")) {
                            this.putLine("Sorry! This function is difficult to complete! Please go back by step.");
                        } else {
                            this.current = FileService.getFile(this.current, commands[1]);
                        }
                    }
                }
                break;
            }
            case "copy": {
                if (commands.length>2) {
                    // TODO: write this
                    // try {
                    //     if (commands[1].startsWith("/")) {
                    //         // absolute path
                    //         if (commands[2].startsWith("/")) {
                    //             // absolute path
                    //             FileService.copyFile(FileService.getFileTraversal(commands[2].substring(1)), FileService.getFileTraversal(commands[1].substring(1)));
                    //         } else {
                    //             // relative path
                    //             FileService.copyFile(FileService.getFileTraversal(commands[1]));
                    //         }
                    //     } else {
                    //         // relative path
                    //         if (commands[2].startsWith("/")) {
                    //             // absolute path
                    //             FileService.copyFile(FileService.getFileTraversal(commands[1].substring(1)));
                    //         } else {
                    //             // relative path
                    //             FileService.copyFile(FileService.getFileTraversal(commands[1]));
                    //         }
                    //     }
                    // } catch (IOException e) {
                    //     this.putLine(e.getMessage());
                    // }
                } else {
                    this.putLine("copy: imcompleted command");
                }
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
                            f.getNormalName())
                        );
                    }
                }
                break;
            }
            case "type":
            case "cat": {
                if (commands.length>1) {
                    if (commands[1].startsWith("/")) {
                        // absolute path
                        try {
                            this.putLine(FileService.getFileContent(FileService.getFileTraversal(commands[1].substring(1))));
                        } catch (IOException e) {
                            this.putLine(e.getMessage());
                        }
                    } else {
                        // relative path
                        try {
                            this.putLine(FileService.getFileContent(FileService.getFileTraversal(commands[1])));
                        } catch (IOException e) {
                            this.putLine(e.getMessage());
                        }
                    }
                } else {
                    this.putLine("type: no file specified");
                }
                break;
            }
            case "open": {
                if (commands.length>1) {

                } else {
                    String name = FileService.getNewName(this.current, 1);
                    try {
                        FileService.createFile(this.current, name);
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                    try {
                        (new Editor(FileService.getFile(this.current, name))).start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case "format": {
                AttrForFS.format();
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

    /**
     * get a prompt string like this {@code user@disk.dat:/$ }
     * @return the prompt string
     */
    public String getPrompt() {
        String dir = "/";
        FileModel traversal = this.current;
        while(traversal.getAttribute() != 3) {
            dir = "/" + traversal.getName() + dir;
            traversal = traversal.getParentFile();
        }
        return Terminal.USER_NAME+"@"+main.Main.DISK+":"+dir+"$ ";
    }

    /**
     * clean the terminal
     */
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
