/**
 * Sample Skeleton for 'ui.fxml' Controller Class
 */

package ui;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class UIController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="filesystemScene"
    private VBox filesystemScene; // Value injected by FXMLLoader

    @FXML // fx:id="menuBar"
    private MenuBar menuBar; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarFile"
    private Menu menuBarFile; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarFileClose"
    private MenuItem menuBarFileClose; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarEdit"
    private Menu menuBarEdit; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarEditRemove"
    private MenuItem menuBarEditRemove; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarHelp"
    private Menu menuBarHelp; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarHelpAbout"
    private MenuItem menuBarHelpAbout; // Value injected by FXMLLoader

    @FXML // fx:id="toolBar"
    private ToolBar toolBar; // Value injected by FXMLLoader

    @FXML // fx:id="toolButton1"
    private Button toolButton1; // Value injected by FXMLLoader

    @FXML // fx:id="mainPane"
    private SplitPane mainPane; // Value injected by FXMLLoader

    @FXML // fx:id="treeScrollPane"
    private ScrollPane treeScrollPane; // Value injected by FXMLLoader

    @FXML // fx:id="tree"
    private TreeView<FileModel> tree; // Value injected by FXMLLoader

    @FXML // fx:id="TreePopupMenu"
    private ContextMenu TreePopupMenu; // Value injected by FXMLLoader

    @FXML // fx:id="TreePopupMenuCreate"
    private MenuItem TreePopupMenuCreate; // Value injected by FXMLLoader

    @FXML // fx:id="viewScrollPane"
    private ScrollPane viewScrollPane; // Value injected by FXMLLoader

    @FXML // fx:id="viewContent"
    private FlowPane viewContent; // Value injected by FXMLLoader

    @FXML // fx:id="viewPopupMenu"
    private ContextMenu viewPopupMenu; // Value injected by FXMLLoader

    @FXML // fx:id="ViewPopupMenuCreate"
    private MenuItem ViewPopupMenuCreate; // Value injected by FXMLLoader

    @FXML // fx:id="infomationStack"
    private Accordion infomationStack; // Value injected by FXMLLoader

    @FXML // fx:id="diskChart"
    private TitledPane diskChart; // Value injected by FXMLLoader

    @FXML // fx:id="FATTable"
    private TitledPane FATTable; // Value injected by FXMLLoader

    @FXML // fx:id="diskContent"
    private TitledPane diskContent; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
	@SuppressWarnings("unchecked")
    void initialize() {
        assert filesystemScene != null : "fx:id=\"filesystemScene\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarFile != null : "fx:id=\"menuBarFile\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarFileClose != null : "fx:id=\"menuBarFileClose\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarEdit != null : "fx:id=\"menuBarEdit\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarEditRemove != null : "fx:id=\"menuBarEditRemove\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarHelp != null : "fx:id=\"menuBarHelp\" was not injected: check your FXML file 'ui.fxml'.";
        assert menuBarHelpAbout != null : "fx:id=\"menuBarHelpAbout\" was not injected: check your FXML file 'ui.fxml'.";
        assert toolBar != null : "fx:id=\"toolBar\" was not injected: check your FXML file 'ui.fxml'.";
        assert toolButton1 != null : "fx:id=\"toolButton1\" was not injected: check your FXML file 'ui.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'ui.fxml'.";
        assert treeScrollPane != null : "fx:id=\"treeScrollPane\" was not injected: check your FXML file 'ui.fxml'.";
        assert tree != null : "fx:id=\"tree\" was not injected: check your FXML file 'ui.fxml'.";
        assert TreePopupMenu != null : "fx:id=\"TreePopupMenu\" was not injected: check your FXML file 'ui.fxml'.";
        assert TreePopupMenuCreate != null : "fx:id=\"TreePopupMenuCreate\" was not injected: check your FXML file 'ui.fxml'.";
        assert viewScrollPane != null : "fx:id=\"viewScrollPane\" was not injected: check your FXML file 'ui.fxml'.";
        assert viewContent != null : "fx:id=\"viewContent\" was not injected: check your FXML file 'ui.fxml'.";
        assert viewPopupMenu != null : "fx:id=\"viewPopupMenu\" was not injected: check your FXML file 'ui.fxml'.";
        assert ViewPopupMenuCreate != null : "fx:id=\"ViewPopupMenuCreate\" was not injected: check your FXML file 'ui.fxml'.";
        assert infomationStack != null : "fx:id=\"infomationStack\" was not injected: check your FXML file 'ui.fxml'.";
        assert diskChart != null : "fx:id=\"diskChart\" was not injected: check your FXML file 'ui.fxml'.";
        assert FATTable != null : "fx:id=\"FATTable\" was not injected: check your FXML file 'ui.fxml'.";
        assert diskContent != null : "fx:id=\"diskContent\" was not injected: check your FXML file 'ui.fxml'.";
        
		DiskModel disk = new DiskModel();
		AttrForFS.setDisk(DiskService.checkDisk(disk));
		AttrForFS.setFat(AttrForFS.getDisk().getFat());
		HashMap<String, Object> hash = DiskService.getDirsAndFiles(disk);
		// this have unchecked warning
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
        AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));
        FileModel rootFileModel = (FileModel) DiskService.getDiskContent(FATModel.RESERVED_BLOCK_COUNT-1, AttrForFS.getDisk());
        Tree root1 = new Tree(rootFileModel);
        this.currentOpen = rootFileModel;
        root1.setExpanded(true);
        this.tree.setRoot(root1);
    }
    @FXML
    public void createFile(ActionEvent event) {
        FileService.createFile(this.currentOpen, 1);
        this.tree.refresh();
    }
    @FXML
    public void createDirectory(ActionEvent event) {
        FileService.createFile(this.currentOpen, 2);
        this.tree.refresh();
    }

    public FileModel currentOpen;
    public void configureTreeView() {
        
    }
}
