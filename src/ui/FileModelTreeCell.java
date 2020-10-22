package src.ui;

import filesystem.model.FileModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FileModelTreeCell extends TreeCell<FileModel> {

    public static final Node FILE_ICON = new ImageView(new Image("resource/file.png"));
    public static final Node DIRECTORY_ICON = new ImageView(new Image("resource/directory.png"));
    
    private TextField tf;

    @Override
    public void startEdit() {
        super.startEdit();
        if (this.tf == null) {
            createTextField();
        }
        this.setText(null);
        this.setGraphic(this.tf);
        this.tf.setText(this.getTreeItem().getValue().getName());
        this.tf.selectAll();
    }

    @Override
    public void cancelEdit() {
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
                    System.out.println("rename to: "+FileModelTreeCell.this.tf.getText());
                    commitEdit(getTreeItem().getValue());
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }

        });
        this.tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (oldValue && (!newValue)) {
                    cancelEdit();
                }
            }
        });
    }
    
}
