package ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;
import util.UIError;

public class Explorer extends Application implements Initializable {

    public static final String NAME = "Explorer";

    public static final Image FILE_ICON = new Image("resource/file.png");
    public static final Image DIRECTORY_ICON = new Image("resource/directory.png");

    public static final String COLUMN_1_MAP_KEY = "name";
    public static final String COLUMN_2_MAP_KEY = "type";
    public static final String COLUMN_3_MAP_KEY = "size";
    public static final String COLUMN_4_MAP_KEY = "readonly";
    public static final String COLUMN_5_MAP_KEY = "startindex";

    public static final String RENAME_FAILED = "重命名失败";
    public static final String CREATE_FAILED = "创建失败";
    public static final String DELETE_FAILED = "删除失败";

    @FXML
    private VBox filesystemScene;

    // menu bar
    @FXML
    private MenuBar menuBar;

        // menu bar directory
    @FXML
    private Menu menuBarDirectory;

    @FXML
    private MenuItem menuBarDirectoryCreateFile;

    @FXML
    private MenuItem menuBarDirectoryCreateDirectory;

    @FXML
    private MenuItem menuBarDirectoryProperty;

    @FXML
    private MenuItem menuBarDirectoryExit;

        // menu bar edit
    @FXML
    private Menu menuBarEdit;

    @FXML
    private MenuItem menuBarEditOpen;

    @FXML
    private MenuItem menuBarEditRemove;

    // tool bar
    @FXML
    private ToolBar toolBar;

    @FXML
    private Label toolBarCurrent;

    @FXML
    private Button toolBarButtonUp;

    @FXML
    private Button toolBarButtonOpen;

    @FXML
    private Button toolBarButtonRemove;

    @FXML
    private Button toolBarButtonRefresh;

    @FXML
    private Button toolBarButtonTerminal;

    @FXML
    private Button toolBarButtonProperty;

    // main pane
    @FXML
    private SplitPane mainPane;

    @FXML
    private ScrollPane treeViewScrollPane;

        // tree view
    @FXML
    private TreeView<FileModel> treeView;

            // tree view popup menu
    @FXML
    private ContextMenu treeViewPopupMenu;

    @FXML
    private MenuItem treeViewPopupMenuCreateFile;

    @FXML
    private MenuItem treeViewPopupMenuCreateDirectory;

        // file view
    @FXML
    private ScrollPane fileViewScrollPane;

    @FXML
    private TableView<FileModelTableItem> fileView;

    @FXML
    private TableColumn<FileModelTableItem, Node> fileViewColumnIcon;

    @FXML
    private TableColumn<FileModelTableItem, String> fileViewColumnName;

    @FXML
    private TableColumn<FileModelTableItem, String> fileViewColumnType;

    @FXML
    private TableColumn<FileModelTableItem, Integer> fileViewColumnSize;

    @FXML
    private TableColumn<FileModelTableItem, Integer> fileViewColumnStartindex;

            // file view popup menu
    @FXML
    private ContextMenu fileViewPopupMenu;

    @FXML
    private MenuItem fileViewPopupMenuOpenSelected;

    @FXML
    private MenuItem fileViewPopupMenuCreateFile;

    @FXML
    private MenuItem fileViewPopupMenuCreateDirectory;

    // imformation view
    @FXML
    private Accordion infomationStack;

    @FXML
    private TitledPane diskChart;

    @FXML
    private TitledPane FATTable;

    // variable
    private Stage primaryStage;
    public FileModel current;
    public FileModel selected;

    @FXML
    void createDirectory(ActionEvent event) {
        try {
            FileService.createNew(this.current, FileModel.DIRECTORY);
        } catch (IOException e) {
            UIError.alertInformation(Explorer.CREATE_FAILED, e.getMessage(), Explorer.this.primaryStage);
        }
        this.updateAll();
    }

    @FXML
    void createFile(ActionEvent event) {
        try {
            FileService.createNew(this.current, FileModel.FILE);
        } catch (IOException e) {
            UIError.alertInformation(Explorer.CREATE_FAILED, e.getMessage(), Explorer.this.primaryStage);
        }
        this.updateAll();
    }

    @FXML
    void exit(ActionEvent event) {
        this.primaryStage.close();
    }

    @FXML
    void refresh(ActionEvent event) {
        this.updateAll();
    }

    @FXML
    void launchTerminal(ActionEvent event) {
        Terminal t = new Terminal(this.current);
        t.start(new Stage());
    }

    @FXML
    void openSelected(ActionEvent event) {
        open(this.selected);
    }

    @FXML
    void removeSelected(ActionEvent event) {
        try {
            if (this.selected.isFile()) {
                FileService.removeFile(this.selected);
            } else if (this.selected.isDirectory()) {
                FileService.removeDir(this.selected);
            }
        } catch (IOException e) {
            UIError.alertInformation(Explorer.DELETE_FAILED, e.getMessage(), Explorer.this.primaryStage);
        }
        this.updateAll();
    }

    @FXML
    void switchToParent(ActionEvent event) {
        this.switchDirectory(this.current.getParentFile());
    }

    public void switchDirectory(FileModel f) {
        if (f != null && f.isDirectory()) {
            this.current = f;
            this.toolBarCurrent.setText(f.getName());
            this.updateFileView();
        }
        this.updateButtonStatus();
        this.primaryStage.setTitle(this.current.getNormalName()+" - "+Explorer.NAME);
    }

    public void open(FileModel f) {
        if (f.isFile()) {
            (new Editor(f)).start(new Stage());
        } else if (f.isDirectory()) {
            this.switchDirectory(f);
        }
    }

    public void updateAll() {
        this.updateTreeView();
        this.updateFileView();
        ((DiskViewer) this.diskChart.getContent()).update();
        ((FATViewer) this.FATTable.getContent()).update();
        this.updateButtonStatus();
    }

    public void updateButtonStatus() {
        if (this.current.getParentFile() == null) {
            this.toolBarButtonUp.setDisable(true);
        } else {
            this.toolBarButtonUp.setDisable(false);
        }

        if (this.selected == null) {
            this.menuBarEdit.setDisable(true);
            this.menuBarEditOpen.setDisable(true);
            this.menuBarEditRemove.setDisable(true);
            this.toolBarButtonOpen.setDisable(true);
            this.toolBarButtonRemove.setDisable(true);
            this.fileViewPopupMenuOpenSelected.setDisable(true);
        } else {
            this.menuBarEdit.setDisable(false);
            this.menuBarEditOpen.setDisable(false);
            this.menuBarEditRemove.setDisable(false);
            this.toolBarButtonOpen.setDisable(false);
            this.toolBarButtonRemove.setDisable(false);
            this.fileViewPopupMenuOpenSelected.setDisable(false);
        }
    }

    public void updateTreeView() {
        this.treeView.setRoot(generateTreeItem(AttrForFS.getRoot()));
    }
    
    public void updateFileView() {
        this.fileView.setItems(generateTableItem(this.current));
    }

    private TreeItem<FileModel> generateTreeItem(final FileModel f) {
        TreeItem<FileModel> treeItem = new TreeItem<FileModel>(f) {

            private boolean isLeaf = false;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<FileModel>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    FileModel file = (FileModel) this.getValue();
                    if (file.isDirectory()) {
                        List<Object> files = FileService.getSubFiles(file);
                        this.isLeaf = files.isEmpty();
                    } else {
                        this.isLeaf = true;
                    }
                }
                return this.isLeaf;
            }

            private ObservableList<TreeItem<FileModel>> buildChildren(TreeItem<FileModel> i) {
                FileModel file = i.getValue();
                if (file == null) {
                    return FXCollections.emptyObservableList();
                } else if (file.isDirectory()) {
                    List<Object> files = FileService.getSubFiles(file);
                    if (!files.isEmpty()) {
                        ObservableList<TreeItem<FileModel>> children = FXCollections.observableArrayList();
                        for (Object childFile : files) {
                            FileModel c = (FileModel) childFile;
                            if (c.isDirectory()) {
                                children.add(generateTreeItem(c));
                            }
                        }
                        return children;
                    }
                    return FXCollections.emptyObservableList();
                } else {
                    return FXCollections.emptyObservableList();
                }
            }
        };
        treeItem.setExpanded(true);
        treeItem.setGraphic(new ImageView(Explorer.DIRECTORY_ICON));
        return treeItem;
    }
    
    private ObservableList<FileModelTableItem> generateTableItem(FileModel file) {
        ObservableList<FileModelTableItem> all = FXCollections.observableArrayList();
        for (Object o: FileService.getSubFiles(file)) {
            if (o instanceof FileModel) {
                FileModel f = (FileModel) o;
                all.add(new FileModelTableItem(f));
            }
        }
        return all;
    }

    public class FileModelTableItem {
        public FileModel file;
        public Node icon;
        public FileModelTableItem(FileModel file) {
            this.file = file;
            if (this.file.isFile()) {
                this.icon = new ImageView(Explorer.FILE_ICON);
            } else if (this.file.isDirectory()) {
                this.icon = new ImageView(Explorer.DIRECTORY_ICON);
            }
        }
        public FileModel getFile() {return this.file;}
        public Node getIcon() {return this.icon;}
        public String getName() {return this.file.getNormalName();}
        public String getType() {
            if (this.file.isFile()) {
                return "文件";
            } else if (this.file.isDirectory()) {
                return "目录";
            } else {
                return "?";
            }
        }
        public Integer getSize() {return this.file.getSize();}
        public Integer getStartIndex() {return this.file.getStartIndex();}
        public String toString() {return this.file.toString();}
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // configuring tree view style
        this.treeView.setCellFactory(new Callback<TreeView<FileModel>, TreeCell<FileModel>>() {
            @Override
            public TreeCell<FileModel> call(TreeView<FileModel> param) {
                TreeCell<FileModel> c = new TreeCell<FileModel>() {

                    private TextField tf;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        if (this.tf == null) {
                            createTextField();
                        }
                        this.setText(null);
                        this.setGraphic(this.tf);
                        this.tf.setText(this.getTreeItem().getValue().getNormalName());
                        this.tf.selectAll();
                        this.tf.requestFocus();
                    }

                    @Override
                    public void cancelEdit() {
                        System.out.println("cancel edit");
                        super.cancelEdit();
                        this.setText(this.getTreeItem().getValue().getName());
                        this.setGraphic(this.getTreeItem().getGraphic());
                    }

                    @Override
                    protected void updateItem(FileModel f, boolean empty) {
                        super.updateItem(f, empty);
                        if (empty) {
                            this.setText(null);
                            this.setGraphic(null);
                        } else {
                            if (isEditing()) {
                                if (this.tf != null) {
                                    this.tf.setText(this.getTreeItem().getValue().getName());
                                }
                            } else {
                                this.setText(this.getTreeItem().getValue().getName());
                                this.setGraphic(this.getTreeItem().getGraphic());
                            }
                        }
                    }

                    public void createTextField() {
                        this.tf = new TextField(this.getTreeItem().getValue().getName());
                        final FileModel file = this.getItem();
                        this.tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                if (event.getCode() == KeyCode.ENTER) {
                                    FileModel n = file;
                                    try {
                                        n = FileService.rename(file, tf.getText());
                                    } catch (IOException e) {
                                        UIError.alertInformation(Explorer.RENAME_FAILED, e.getMessage(), Explorer.this.primaryStage);
                                    }
                                    commitEdit(n);
                                } else if (event.getCode() == KeyCode.ESCAPE) {
                                    cancelEdit();
                                }
                            }
                        });
                        this.tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if (oldValue && (!newValue)) {
                                    FileModel n = file;
                                    try {
                                        n = FileService.rename(file, tf.getText());
                                    } catch (IOException e) {
                                        UIError.alertInformation(Explorer.RENAME_FAILED, e.getMessage(), Explorer.this.primaryStage);
                                    }
                                    commitEdit(n);
                                }
                            }
                        });
                    }
                };
                c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Explorer.this.switchDirectory(c.getItem());
                    }
                });
                return c;
            }
        });
        // configuring file view style
        this.fileView.setPlaceholder(new Label("当前目录无文件"));
        this.fileViewColumnIcon.setCellValueFactory(new PropertyValueFactory<FileModelTableItem, Node>("icon"));
        this.fileViewColumnIcon.setStyle("-fx-alignment: CENTER;");
        this.fileViewColumnName.setCellValueFactory(new PropertyValueFactory<FileModelTableItem, String>("name"));
        this.fileViewColumnName.setStyle("-fx-alignment: CENTER-RIGHT;");
        this.fileViewColumnType.setCellValueFactory(new PropertyValueFactory<FileModelTableItem, String>("type"));
        this.fileViewColumnType.setStyle("-fx-alignment: CENTER;");
        this.fileViewColumnSize.setCellValueFactory(new PropertyValueFactory<FileModelTableItem, Integer>("size"));
        this.fileViewColumnSize.setStyle("-fx-alignment: CENTER;");
        this.fileViewColumnStartindex.setCellValueFactory(new PropertyValueFactory<FileModelTableItem, Integer>("startIndex"));
        this.fileViewColumnStartindex.setStyle("-fx-alignment: CENTER;");
        // select file item
        this.fileView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileModelTableItem>() {
            @Override
            public void changed(ObservableValue<? extends FileModelTableItem> observable, FileModelTableItem oldValue,
                    FileModelTableItem newValue) {
                if (newValue != null) {
                    Explorer.this.selected = newValue.getFile();
                    Explorer.this.updateButtonStatus();
                }
            }
        });
        // file item rename
        this.fileViewColumnName.setCellFactory(TextFieldTableCell.<FileModelTableItem>forTableColumn());
        this.fileViewColumnName.setOnEditCommit(new EventHandler<CellEditEvent<FileModelTableItem, String>>() {
            @Override
            public void handle(CellEditEvent<FileModelTableItem, String> event) {
                FileModel f = event.getTableView().getItems().get(event.getTablePosition().getRow()).getFile();
                try {
                    FileService.rename(f, event.getNewValue());
                } catch (IOException e) {
                    UIError.alertInformation(Explorer.RENAME_FAILED, e.getMessage(), Explorer.this.primaryStage);
                }
                Explorer.this.updateAll();
            }
        });

        // double click to open and switch directory
        this.fileView.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                    FileModel f = Explorer.this.fileView.getSelectionModel().getSelectedItem().getFile();
                    if (f != null) {
                        if (f.isFile()) {
                            (new Editor(f)).start(new Stage());
                        } else if (f.isDirectory()) {
                            Explorer.this.switchDirectory(f);
                        }
                    }
                }
            }
        });
        // initializing imformation view
        this.diskChart.setContent(new DiskViewer(AttrForFS.getDisk()));
        this.FATTable.setContent(new FATViewer(AttrForFS.getFat()));
        this.diskChart.setExpanded(true);
        // initializing all components
        switchDirectory(AttrForFS.getRoot());
        this.updateTreeView();
        this.updateButtonStatus();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/explorer.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(this.filesystemScene);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
                primaryStage.close();
            }
        }); // save file system if clicking the close button
    }

    public static void main(String[] args) {
        AttrForFS.init();
        launch(args);
    }
}
