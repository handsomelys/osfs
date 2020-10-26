package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import filesystem.model.FATModel;

public class FATViewer extends TableView<FATItem> {

    private FATModel fat;
    
    public FATViewer(FATModel fat) {
        super();
        this.fat = fat;

        ObservableList<FATItem> list = FXCollections.observableArrayList();

        TableColumn<FATItem, Integer> tvi = new TableColumn<>("index");
        TableColumn<FATItem, Integer> tvv = new TableColumn<>("value");
        this.getColumns().add(tvi);
        this.getColumns().add(tvv);

        for(int i = 0; i < this.fat.getTable().length; i++) {
            list.add(new FATItem(i, fat.getTable()[i]));
        }

        tvi.setCellValueFactory(new PropertyValueFactory<>("index"));
        tvv.setCellValueFactory(new PropertyValueFactory<>("value"));
        tvi.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
        tvv.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
        this.setItems(list);
    }

    public void update() {
        ObservableList<FATItem> list = FXCollections.observableArrayList();
        for(int i = 0; i < this.fat.getTable().length; i++) {
            list.add(new FATItem(i, fat.getTable()[i]));
        }
        this.setItems(list);
    }
}