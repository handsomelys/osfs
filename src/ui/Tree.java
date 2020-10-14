package ui;

import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import filesystem.model.FileModel;
import filesystem.service.FileService;

public class Tree extends TreeItem<FileModel> {

    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    
    @Override
    public ObservableList<TreeItem<FileModel>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren());
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            FileModel f = (FileModel) this.getValue();
            return f.getAttribute() == 1;
        }
        return isLeaf;
    }

    private ObservableList<TreeItem<FileModel>> buildChildren() {
    FileModel f = this.getValue();
    if (f == null || f.getAttribute() == 1) {
        return FXCollections.emptyObservableList();
    } else if (f.getAttribute() == 2) {
        List<FileModel> files = getSubFiles(f);

    }
    File[] files = f.listFiles();
    if (files != null) {
        ObservableList<TreeItem<File>> children = FXCollections
            .observableArrayList();
        for (File childFile : files) {
        children.add(createNode(childFile));
        }
        return children;
    }
    return FXCollections.emptyObservableList();
    }
        
}
