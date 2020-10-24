package ui;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.FileService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Explorer extends Application implements Initializable {

    public static final Image FILE_ICON = new Image("resource/file.png");
    public static final Image DIRECTORY_ICON = new Image("resource/directory.png");

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Explorer.stage = stage;
    }

    public static TilePane getShowPane() {
        return showPane;
    }

    public static void setShowPane(TilePane showPane) {
        Explorer.showPane = showPane;
    }

    private static Stage stage;
    private static TilePane showPane;
    public FileModel current;

    @FXML
    private VBox filesystemScene;

    @FXML
    private MenuBar menuBar;

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

    @FXML
    private Menu menuBarEdit;

    @FXML
    private MenuItem menuBarEditOpen;

    @FXML
    private MenuItem menuBarEditRename;

    @FXML
    private MenuItem menuBarEditRemove;

    @FXML
    private Menu menuBarHelp;

    @FXML
    private MenuItem menuBarHelpAbout;

    @FXML
    private ToolBar toolBar;

    @FXML
    private Label toolBarCurrent;

    @FXML
    private Button toolBarButtonOpen;

    @FXML
    private Button toolBarButtonSelectAll;

    @FXML
    private Button toolBarButtonProperty;

    @FXML
    private SplitPane mainPane;

    @FXML
    private ScrollPane treeViewScrollPane;

    @FXML
    private TreeView<FileModel> treeView;

    @FXML
    private ContextMenu treeViewPopupMenu;

    @FXML
    private MenuItem treeViewPopupMenuCreateFile;

    @FXML
    private MenuItem treeViewPopupMenuCreateDirectory;

    @FXML
    private ScrollPane fileViewScrollPane;

    @FXML
    private TableView<Map<String, String>> fileView;

    @SuppressWarnings("all")
    @FXML
    private TableColumn<Map, String> fileViewColumnName;

    @SuppressWarnings("all")
    @FXML
    private TableColumn<Map, String> fileViewColumnType;

    @SuppressWarnings("all")
    @FXML
    private TableColumn<Map, String> fileViewColumnSize;

    @SuppressWarnings("all")
    @FXML
    private TableColumn<Map, String> fileViewColumnReadonly;

    @SuppressWarnings("all")
    @FXML
    private TableColumn<Map, String> fileViewColumnStartindex;

    @FXML
    private ContextMenu fileViewPopupMenu;

    @FXML
    private MenuItem fileViewPopupMenuCreateFile;

    @FXML
    private MenuItem fileViewPopupMenuCreateDirectory;

    @FXML
    private Accordion infomationStack;

    @FXML
    private TitledPane diskChart;

    @FXML
    private TitledPane FATTable;

    @FXML
    private TitledPane diskContent;

    @FXML
    void createDirectory(ActionEvent event) {
        try {
            FileService.createNew(this.current, FileModel.DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 整点弹窗
        }
        this.updateTreeView();
        this.updateFileView();
    }

    @FXML
    void createFile(ActionEvent event) {
        try {
            FileService.createNew(this.current, FileModel.FILE);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 整点弹窗
        }

        ui.ShowFilePane.show((FileModel) AttrForFS.getDisk().getDiskTable().get(2),FileModel.FILE,stage,showPane);
        this.updateTreeView();
        this.updateFileView();
    }

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void help(ActionEvent event) {

    }

    @FXML
    void infomation(ActionEvent event) {

    }

    @FXML
    void launchDirectoryProperty(ActionEvent event) {

    }

    @FXML
    void launchSelectedProperty(ActionEvent event) {

    }

    @FXML
    void openSelected(ActionEvent event) {
        for (Object o : FileService.getSubFiles(this.current)) {
            System.out.println(o);
            // for (Object oo: FileService.getSubFiles((FileModel) o)) {
            // System.out.println("2");
            // System.out.println(oo);
            // }
        }
    }

    @FXML
    void removeSelected(ActionEvent event) {

    }

    @FXML
    void renameSelected(ActionEvent event) {

    }

    @FXML
    void selectAll(ActionEvent event) {
        this.updateTreeView();
        this.updateFileView();
    }

    void configureTreeView() {

    }

    void switchDirectory(FileModel f) {
        if (f != null && (f.getAttribute() == 2 || f.getAttribute() == 3)) {
            this.current = f;
            this.toolBarCurrent.setText(f.getName());
            this.updateFileView();
        }
    }
    void updateTreeView() {
        this.treeView.setRoot(Explorer.createNode(AttrForFS.getRoot()));
    }
    
    void updateFileView() {
        ObservableList<Map<String, String>> oo = generateFile(this.current);
        // System.out.println(oo);
        this.fileView.setItems(oo);
    }
    
    ObservableList<Map<String, String>> generateFile(FileModel f) {
        ObservableList<Map<String, String>> all = FXCollections.observableArrayList();
        for (Object o: FileService.getSubFiles(f)) {
            if (o instanceof FileModel) {
                FileModel ff = (FileModel) o;
                Map<String, String> m = new HashMap<String, String>();
                if (ff.getAttribute() == 1) {
                    m.put(ui.FileModelListCell.COLUMN_1_MAP_KEY, ff.getNormalName());
                    m.put(ui.FileModelListCell.COLUMN_2_MAP_KEY, "文件");
                } else if (ff.getAttribute() == 2 || ff.getAttribute() == 3) {
                    m.put(ui.FileModelListCell.COLUMN_1_MAP_KEY, ff.getName());
                    m.put(ui.FileModelListCell.COLUMN_2_MAP_KEY, "目录");
                }
                m.put(ui.FileModelListCell.COLUMN_3_MAP_KEY, String.valueOf(ff.getSize()));
                m.put(ui.FileModelListCell.COLUMN_4_MAP_KEY, ff.isReadOnly()?"√":"×");
                m.put(ui.FileModelListCell.COLUMN_5_MAP_KEY, String.valueOf(ff.getStartIndex()));
                // System.out.println(ff);
                // System.out.println(m);
                all.add(m);
            }
        }
        return all;
    }

    public static TreeItem<FileModel> createNode(final FileModel f) {
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
                                children.add(createNode(c));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switchDirectory(AttrForFS.getRoot());
        this.updateTreeView();
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
                        this.tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {
                                if (event.getCode() == KeyCode.ENTER) {
                                    System.out.println("rename to: "+tf.getText());
                                    commitEdit(getTreeItem().getValue());
                                } else if (event.getCode() == KeyCode.ESCAPE) {
                                    cancelEdit();
                                }
                            }

                        });
                        this.tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if ((oldValue && (!newValue))) {
                                    // cancelEdit();
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

        this.fileViewColumnName.setCellValueFactory(new MapValueFactory<String>(ui.FileModelListCell.COLUMN_1_MAP_KEY));
        this.fileViewColumnType.setCellValueFactory(new MapValueFactory<String>(ui.FileModelListCell.COLUMN_2_MAP_KEY));
        this.fileViewColumnSize.setCellValueFactory(new MapValueFactory<String>(ui.FileModelListCell.COLUMN_3_MAP_KEY));
        this.fileViewColumnReadonly.setCellValueFactory(new MapValueFactory<String>(ui.FileModelListCell.COLUMN_4_MAP_KEY));
        this.fileViewColumnStartindex.setCellValueFactory(new MapValueFactory<String>(ui.FileModelListCell.COLUMN_5_MAP_KEY));
        this.fileView.setPlaceholder(new Label("当前目录无文件"));
        this.updateFileView();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent editor = FXMLLoader.load(getClass().getResource("/ui/explorer.fxml"));
            Scene scene = new Scene(editor);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setTitle("explore");
			primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
		}

    }

    public static void main(String[] args) {
        AttrForFS.init();
        launch(args);
    }
}
