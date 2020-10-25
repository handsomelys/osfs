package ui;

import filesystem.model.FATItem;
import filesystem.model.FATModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.*;

import controller.AttrForFS;

public class FATViewer extends Application {
	private TableView<FATItem> tv;
    public static void main(String[] args) {
        launch(args);
    }

    public FATViewer(TableView<FATItem> tv,TableColumn<FATItem,Number> tcFATIndex,TableColumn<FATItem,Number> tcFATValue){
    	this.tv = tv;
    	FATModel fat = AttrForFS.getFat();
        List<Integer> fat_list = new ArrayList<>(fat.getTable().length);
        for(int i:fat.getTable()){
            fat_list.add(i);
        }
        System.out.println(fat_list);
        ObservableList<FATItem> list = FXCollections.observableArrayList();
        //System.out.println(list);


        /*TableColumn<FATItem,Number> tcFATIndex = new TableColumn<>("Index");
        TableColumn<FATItem,Number> tcFATValue = new TableColumn<>("Value");*/

        this.tv.getColumns().add(tcFATIndex);
        this.tv.getColumns().add(tcFATValue);


        for(int i=0;i<fat.getTable().length;i++){
            list.add(new FATItem(i,fat.getTable()[i]));
        }

        tcFATIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
        tcFATValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        tcFATIndex.prefWidthProperty().bind(tv.prefWidthProperty().multiply(0.5));
        tcFATValue.prefWidthProperty().bind(tv.prefWidthProperty().multiply(0.5));
        System.out.println(list);
        tv.setItems(list);

    }

    public void update(){
    	FATModel fat = AttrForFS.getFat();
        ObservableList<FATItem> list = FXCollections.observableArrayList();
        for(int i=0;i<fat.getTable().length;i++){
            list.add(new FATItem(i,fat.getTable()[i]));
        }
        this.tv.setItems(list);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        FATModel fat = new FATModel();
//        List<Integer> fat_list = new ArrayList<>(fat.getTable().length);
//        for(int i:fat.getTable()){
//            fat_list.add(i);
//        }
//        System.out.println(fat_list);
//        ObservableList<FATItem> list = FXCollections.observableArrayList();
//        //System.out.println(list);
//
//        AnchorPane ap = new AnchorPane();
//        TableView tv = new TableView<>();
//        TableColumn<FATItem,Number> tcFATIndex = new TableColumn<>("Index");
//        TableColumn<FATItem,Number> tcFATValue = new TableColumn<>("Value");
//
//
//
//        tv.getColumns().add(tcFATIndex);
//        tv.getColumns().add(tcFATValue);
//
//
//
//        for(int i=0;i<fat.getTable().length;i++){
//            list.add(new FATItem(i,fat.getTable()[i]));
//        }
//
//        tcFATIndex.setCellValueFactory(new PropertyValueFactory<>("Index"));
//        tcFATValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
//        tcFATIndex.prefWidthProperty().bind(tv.prefWidthProperty().multiply(0.5));
//        tcFATValue.prefWidthProperty().bind(tv.prefWidthProperty().multiply(0.5));
//        System.out.println(list);
//        tv.setItems(list);
//        ap.getChildren().addAll(tv);
//        tv.prefWidthProperty().bind(ap.widthProperty());
//        tv.prefHeightProperty().bind(ap.heightProperty());
//        Scene scene = new Scene(ap);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
    }
}
