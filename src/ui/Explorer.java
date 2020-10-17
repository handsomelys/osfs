package ui;

import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.FileService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
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

    @FXML
    private ContextMenu treeViewPopupMenu;

    @FXML
    private MenuItem treeViewPopupMenuCreateFile;

    @FXML
    private MenuItem treeViewPopupMenuCreateDirectory;

    @FXML
    private ScrollPane fileViewScrollPane;

    @FXML
    private FlowPane fileViewContent;

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
        this.treeView.refresh();
    }

    @FXML
    void createFile(ActionEvent event) {
        FileService.createFile(this.current, 1);
        this.treeView.refresh();
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

    }

    @FXML
    void removeSelected(ActionEvent event) {

    }

    @FXML
    void renameSelected(ActionEvent event) {

    }

    @FXML
    void selectAll(ActionEvent event) {

    }

    void configureTreeView() {
        
    }

    void switchDirectory(FileModel f) {
        if (f != null && (f.getAttribute() == 2 || f.getAttribute() == 3)) {
            this.current = f;
            this.toolBarCurrent.setText(f.getName());
        }
    }

    public static TreeItem<FileModel> createNode(final FileModel f) {
        return new TreeItem<FileModel>(f) {

            @Override
            public ObservableList<TreeItem<FileModel>> getChildren() {
                super.getChildren().setAll(buildChildren(this));
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                FileModel f1 = (FileModel) this.getValue();
                if (f1.getAttribute() == 1) {
                    return true;
                } else if (f1.getAttribute() == 2 || f1.getAttribute() == 3) {
                    List<Object> files = FileService.getSubFiles(f);
                    return files.isEmpty();
                }
                return false;
            }

            private ObservableList<TreeItem<FileModel>> buildChildren(TreeItem<FileModel> i) {
                FileModel f = i.getValue();
                if (f == null) {
                    return FXCollections.emptyObservableList();
                } else if (f.getAttribute() == 2 || f.getAttribute() == 3) {
                    List<Object> files = FileService.getSubFiles(f);
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
                }
                return FXCollections.emptyObservableList();
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switchDirectory(AttrForFS.getRoot());

        this.treeView.setRoot(Explorer.createNode(AttrForFS.getRoot()));
        
        this.treeView.setCellFactory(new Callback<TreeView<FileModel>, TreeCell<FileModel>>() {
            @Override
            public TreeCell<FileModel> call(TreeView<FileModel> param) {
                return new TreeCell<FileModel>() {
                    private TextField tf;
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        if (this.tf == null) {
                            createTextField();
                        }
                        this.setText(null);
                        this.setGraphic(this.tf);
                        tf.selectAll();
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(this.getTreeItem().getValue().getName());
                        this.setGraphic(getTreeItem().getGraphic());
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
                                this.getTreeItem().getChildren();
                                this.setText(this.getTreeItem().getValue().getName());
                                this.setGraphic(getTreeItem().getGraphic());
                            }
                            // this.setGraphic(Explorer.DIRECTORY_ICON);
                        }
                    }

                    public void createTextField() {
                        this.tf = new TextField(this.getTreeItem().getValue().getName());
                        this.tf.setOnKeyReleased(new EventHandler<KeyEvent>(){
                            @Override
                            public void handle(KeyEvent event) {
                                if (event.getCode() == KeyCode.ENTER) {
                                    // TODO: rename
                                    commitEdit(getTreeItem().getValue());
                                } else if (event.getCode() == KeyCode.ESCAPE) {
                                    cancelEdit();
                                }
                            }
                            
                        });
                    }
                };
            }
        });
    }

}
