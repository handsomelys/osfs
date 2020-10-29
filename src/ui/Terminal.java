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

    public static final String HELP = 
    "+-------------------------------------------------------------------------------------------------+\n"+
    "|                                       WELCOME TO TERMINAL !                                     |\n"+
    "| Here is some tips for termianl!                                      --relative path supported! |\n"+
    "+---------+-----------------------+---------------------------------------------------------------+\n"+
    "| command |     command format    | command description                                           |\n"+
    "+---------+-----------------------+---------------------------------------------------------------+\n"+
    "|  create |     create <file>     | create a new file on the disk                                 |\n"+
    "|  delete |     delete <file>     | delete the file specified on the disk                         |\n"+
    "|    type |       type <file>     | display file content in the terminal                          |\n"+
    "|    copy |  copy <source> <dest> | copy file to another location                                 |\n"+
    "|   mkdir |   mkdir <directory>   | make a new directory on the disk                              |\n"+
    "|   rmdir |   rmdir <directory>   | delete the directory specified on the disk                    |\n"+
    "|   chdir |   chdir <directory>   | change directory to another                                   |\n"+
    "|  deldir |  deldir <direvtory>   | delete the directory and it all childrens on the disk         |\n"+
    "|  format |         format        | get a new disk and clear all content currently on the disk    |\n"+
    "+---------+-----------------------+---------------------------------------------------------------+\n";


    public VBox root;

    protected TextArea display;
    protected TextField input;

    protected ArrayList<String> history;
    protected int historyPointer;

    protected FileModel current;

    /**
     * start a new terminal interface on {@code root} directory.
     * <p>use these code <pre>{@code (new Terminal()).start(new Stage());}</pre> to get a terminal!</p>
     */
    public Terminal() {
        this(AttrForFS.getRoot());
    }

    /**
     * start a new terminal interface on a directory.
     * <p>use these code <pre>{@code (new Terminal(someFileModel)).start(new Stage());}</pre> to get a terminal!</p>
     * @param f the directory you start terminal on
     */
    public Terminal(FileModel f) {
        this.root = new VBox();

        this.display = new TextArea();
        this.input = new TextField();
        this.history = new ArrayList<String>();
        this.historyPointer = -1;

        this.root.setPadding(new Insets(5, 5, 5, 5));
        this.root.setSpacing(5);

        VBox.setVgrow(this.display, Priority.ALWAYS);
        this.display.setEditable(false);
        this.display.setWrapText(true);
        this.display.setFont(Font.font("Consolas", 14));

        this.input.setFont(Font.font("Consolas", 14));
        this.input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) {
                    String raw = Terminal.this.input.getText();		//the command that haven't proposal
                    String command = raw.substring(Terminal.this.getPrompt().length());		//get the raw's substring
                    Terminal.this.history.add(command);	//the keyboard ↑ to get the historic command
                    Terminal.this.historyPointer = Terminal.this.history.size();
                    Terminal.this.putLine(raw);
                    Terminal.this.parseCommand(command);	//parse the command by the blank
                    Terminal.this.clearInput();
                } else if (e.getCode() == KeyCode.UP) {
                    if (Terminal.this.historyPointer > 0) {
                        --Terminal.this.historyPointer;
                        Terminal.this.input.setText(Terminal.this.getPrompt()+history.get(historyPointer));
                    } else {
                        java.awt.Toolkit.getDefaultToolkit().beep(); // deng!
                    }
                    Terminal.this.input.selectEnd();
                    Terminal.this.input.deselect();
                } else if (e.getCode() == KeyCode.DOWN) {
                    if (Terminal.this.historyPointer < Terminal.this.history.size()-1) {
                        Terminal.this.historyPointer-=-1;
                        Terminal.this.input.setText(Terminal.this.getPrompt()+history.get(historyPointer));
                    } else {
                        java.awt.Toolkit.getDefaultToolkit().beep(); // deng!
                    }
                    Terminal.this.input.selectEnd();
                    Terminal.this.input.deselect();
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

        this.current = f;
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
                                FileService.createFile(this.current, commands[1]);
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
                                FileService.createDirectory(this.current, commands[1]);
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
            case "copy": {
                // copy file to another location
                // this is so complicate!
                if (commands.length>2) {
                    try {
                        int lastDash = commands[2].lastIndexOf("/");
                        if (commands[1].startsWith("/")) {
                            // source is absolute path
                            if (commands[2].startsWith("/")) {
                                // destination is absolute path
                                if (lastDash == 0) {
                                    FileService.copyFile(FileService.getFileTraversal(commands[1].substring(1)), AttrForFS.getRoot(), commands[2].substring(1));
                                } else {
                                    FileService.copyFile(FileService.getFileTraversal(commands[1].substring(1)), FileService.getFileTraversal(commands[2].substring(1, lastDash)), commands[2].substring(1));
                                }
                            } else {
                                // destination is relative path
                                if (lastDash == -1) {
                                    FileService.copyFile(FileService.getFileTraversal(commands[1].substring(1)), this.current, commands[2]);
                                } else {
                                    FileService.copyFile(FileService.getFileTraversal(commands[1].substring(1)), FileService.getFileTraversal(this.current, commands[2].substring(0, lastDash)), commands[2].substring(lastDash+1));
                                }
                            }
                        } else {
                            // source is relative path
                            if (commands[2].startsWith("/")) {
                                // destination is absolute path
                                if (lastDash == 0) {
                                    FileService.copyFile(FileService.getFileTraversal(this.current, commands[1]), AttrForFS.getRoot(), commands[2].substring(1));
                                } else {
                                    FileService.copyFile(FileService.getFileTraversal(this.current, commands[1]), FileService.getFileTraversal(commands[2].substring(1, lastDash)), commands[2].substring(1));
                                }
                            } else {
                                // destination is relative path
                                if (lastDash == -1) {
                                    FileService.copyFile(FileService.getFileTraversal(this.current, commands[1]), this.current, commands[2]);
                                } else {
                                    FileService.copyFile(FileService.getFileTraversal(this.current, commands[1]), FileService.getFileTraversal(this.current, commands[2].substring(0, lastDash)), commands[2].substring(lastDash+1));
                                }
                            }
                        }
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                } else {
                    this.putLine("copy: imcompleted command");
                }
                break;
            }
            case "type":
            case "cat": {
                // display file content in the terminal
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
            case "chdir":
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
            case "open": {
                if (commands.length>1) {
                    try {
                        FileModel f = null;
                        if (commands[1].startsWith("/")) {
                        // absolute path
                            f = FileService.getFileTraversal(commands[1].substring(1));
                        } else {
                        // relative path
                            f = FileService.getFileTraversal(this.current, commands[1]);
                        }
                        
                        if (f.isFile()) {
                            (new Editor(f)).start(new Stage());
                        } else {
                            this.putLine(f.getNormalName()+": is not a file");
                        }
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                } else {
                    String name = FileService.getNewName(this.current, 1);
                    try {
                        FileService.createFile(this.current, name);
                    } catch (IOException e) {
                        this.putLine(e.getMessage());
                    }
                    (new Editor(FileService.getFile(this.current, name))).start(new Stage());
                }
                break;
            }
            case "deldir": 
            	//delete the directory include its sub files
            	{
            	 if (commands.length>1) {
                     try {
                         if (commands[1].startsWith("/")) {
                             // absolute path
                             FileService.deleteDirectory((FileService.getFileTraversal(commands[1].substring(1))));
                         } else {
                             // relative path
                             FileService.deleteDirectory((FileService.getFileTraversal(this.current, commands[1])));
                         }
                     } catch (IOException e) {
                         this.putLine(e.getMessage());
                     }
                 } else {
                     this.putLine("deldir: no file specified");
                 }
                 break;
            }
            case "format": {
                AttrForFS.format();
                break;
            }
            case "clear": {
                this.clearDisplay();
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

    public void getHelp() {
        this.display.appendText(Terminal.HELP);
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
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
            }
        });

        this.input.setText(this.getPrompt());
    }
    public static void main(String args[]) {
        AttrForFS.init();
        launch(args);
    }
}
