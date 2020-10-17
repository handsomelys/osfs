package ui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import filesystem.model.FileModel;
import filesystem.service.FileService;

public class Tree extends TreeItem<FileModel> {

    public static final Node FILE_ICON = new ImageView(new Image("resource/file.png"));
    public static final Node DIRECTORY_ICON = new ImageView(new Image("resource/directory.png"));

    private boolean isLeaf;
    
    public Tree(FileModel fm) {
        this.setValue(fm);
        this.setGraphic(Tree.DIRECTORY_ICON);
    }

    @Override
    public ObservableList<TreeItem<FileModel>> getChildren() {
        super.getChildren().setAll(buildChildren());
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        FileModel f = (FileModel) this.getValue();
        return f.getAttribute() == 1;
    }

    public ObservableList<TreeItem<FileModel>> buildChildren() {
        FileModel f = this.getValue();
        if (f == null || f.getAttribute() == 1) {
            // file
            return FXCollections.emptyObservableList();
        } else if (f.getAttribute() == 2 || f.getAttribute() == 3) {
            // directory or root
            List<Object> files = FileService.getSubFiles(f);
            if (files != null) {
                ObservableList<TreeItem<FileModel>> children = FXCollections.observableArrayList();
                for (Object childFile : files) {
                    FileModel c = (FileModel) childFile;
                    children.add(new Tree(c));
                }
                return children;
            }
            return FXCollections.emptyObservableList();
        }
        return FXCollections.emptyObservableList();
    }
}
