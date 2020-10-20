package ui;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.FileService;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Explorer implements Initializable {

    public static final Node DIRECTORY_ICON = new ImageView(new Image("resource/directory.png"));

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
    private ContextMenu treeViewPopupMenu;

    @FXML
    private MenuItem treeViewPopupMenuCreateFile;

    @FXML
    private MenuItem treeViewPopupMenuCreateDirectory;

    @FXML
    private ScrollPane fileViewScrollPane;

    @FXML
    private TableView<Map<String, String>> fileView;

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
        FileService.createFile(this.current, 2);
        this.updateTreeView();
        this.updateFileView();
    }

    @FXML
    void createFile(ActionEvent event) {
        FileService.createFile(this.current, 1);
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
        }
    }
    void updateTreeView() {
        this.treeView.setRoot(Explorer.createNode(AttrForFS.getRoot()));
    }
    
    void updateFileView() {
        ObservableList<Map<String, String>> oo = generateFile(this.current);
        System.out.println(oo);
        this.fileView.setItems(oo);
    }
    
    ObservableList<Map<String, String>> generateFile(FileModel f) {
        ObservableList<Map<String, String>> all = FXCollections.observableArrayList();
        for (Object o: FileService.getSubFiles(f)) {
            if (o instanceof FileModel) {
                FileModel ff = (FileModel) o;
                Map<String, String> m = new HashMap<String, String>();
                if (ff.getAttribute() == 1) {
                    m.put(FileModelListCell.COLUMN_1_MAP_KEY, ff.getName()+"."+ff.getType());
                    m.put(FileModelListCell.COLUMN_2_MAP_KEY, "文件");
                } else if (ff.getAttribute() == 2 || ff.getAttribute() == 3) {
                    m.put(FileModelListCell.COLUMN_1_MAP_KEY, ff.getName());
                    m.put(FileModelListCell.COLUMN_2_MAP_KEY, "目录");
                }
                m.put(FileModelListCell.COLUMN_3_MAP_KEY, String.valueOf(ff.getSize()));
                m.put(FileModelListCell.COLUMN_4_MAP_KEY, ff.isReadOnly()?"√":"×");
                m.put(FileModelListCell.COLUMN_5_MAP_KEY, String.valueOf(ff.getStartIndex()));
                System.out.println(ff);
                System.out.println(m);
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
                    if (file.getAttribute() == 1) {
                        this.isLeaf = true;
                    } else if (file.getAttribute() == 2 || file.getAttribute() == 3) {
                        List<Object> files = FileService.getSubFiles(file);
                        this.isLeaf = files.isEmpty();
                    } else {
                        this.isLeaf = false;
                    }
                }
                return this.isLeaf;
            }

            private ObservableList<TreeItem<FileModel>> buildChildren(TreeItem<FileModel> i) {
                FileModel file = i.getValue();
                if (file == null) {
                    return FXCollections.emptyObservableList();
                } else if (file.getAttribute() == 2 || file.getAttribute() == 3) {
                    List<Object> files = FileService.getSubFiles(file);
                    if (!files.isEmpty()) {
                        ObservableList<TreeItem<FileModel>> children = FXCollections.observableArrayList();
                        for (Object childFile : files) {
                            FileModel c = (FileModel) childFile;
                            if (c.getAttribute() == 2 || c.getAttribute() == 3) {
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
        return treeItem;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switchDirectory(AttrForFS.getRoot());
        this.updateTreeView();

        this.fileViewColumnName.setCellValueFactory(new MapValueFactory<String>(FileModelListCell.COLUMN_1_MAP_KEY));
        this.fileViewColumnType.setCellValueFactory(new MapValueFactory<String>(FileModelListCell.COLUMN_2_MAP_KEY));
        this.fileViewColumnSize.setCellValueFactory(new MapValueFactory<String>(FileModelListCell.COLUMN_3_MAP_KEY));
        this.fileViewColumnReadonly.setCellValueFactory(new MapValueFactory<String>(FileModelListCell.COLUMN_4_MAP_KEY));
        this.fileViewColumnStartindex.setCellValueFactory(new MapValueFactory<String>(FileModelListCell.COLUMN_5_MAP_KEY));
        this.updateFileView();

        this.treeView.setCellFactory(new Callback<TreeView<FileModel>, TreeCell<FileModel>>() {
            @Override
            public TreeCell<FileModel> call(TreeView<FileModel> param) {
                FileModelTreeCell c = new FileModelTreeCell();
                c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Explorer.this.switchDirectory(c.getItem());
                    }
                });
                return c;
            }
        });
    }

}