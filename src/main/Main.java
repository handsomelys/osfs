package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent explorer = new Pane();
        try {
            explorer = FXMLLoader.load(getClass().getResource("/ui/explorer.fxml"));
            primaryStage.setScene(new Scene(explorer, 900, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws CloneNotSupportedException {
		Scanner sc = new Scanner(System.in);
		
		DiskModel disk = new DiskModel();
		AttrForFS.setDisk(DiskService.checkDisk(disk));
		AttrForFS.setFat(AttrForFS.getDisk().getFat());
		
		HashMap<String, Object> hash = DiskService.getDirsAndFiles(disk);
		// this have unchecked warning
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
		AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));

		FileModel parentFile = (FileModel) AttrForFS.getDisk().getDiskTable().get(2);
		// FileService.createFile(parentFile, FileModel.DIRECTORY);
		// System.out.println(AttrForFS.getDisk().getDiskFreeCount());
		// for (int i=0;i<3;i++) {
		// 	FileService.createFile(parentFile, FileModel.FILE);
		// }
		// FileService.copyFile((FileModel)AttrForFS.getDisk().getDiskTable().get(4));
		// System.out.println(AttrForFS.getDisk().getDiskFreeCount());
		launch(args);
	}
		
}
