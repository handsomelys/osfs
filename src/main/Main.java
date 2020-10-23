package main;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.Terminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import controller.AttrForFS;
import filesystem.service.DiskService;

public class Main extends Application {

	public static final String DISK = "disk.dat";
	public static final int UP_PADDING = 45;
	public static final int RIGHT_PADDING = 2000;
	public static final int DOWN_PADDING = 50;
	public static final int LEFT_PADDING = 50;
	public static final int PANEL_HEIGHT = 750;
	public static final int PANEL_WIDTH = 400;
	@Override
	public void start(Stage primaryStage) {

		FlowPane lauchPane = new FlowPane();

		ImageView fsManager = new ImageView(new Image("/resource/finder2.png"));
		ImageView diskManager = new ImageView(new Image("/resource/disk3.png"));
		ImageView processManager = new ImageView(new Image("/resource/process2.png"));
		
		/*
		 * fsManager.setImage(new Image("/resource/finder.png"));
		 * diskManager.setImage(new Image("file:/src/resource/disk.png"));
		 * processManager.setImage(new Image("file:/src/resource/process.png"));
		 */
		 
		Label fsm = new Label("FileSystemManager");
		fsm.setGraphic(fsManager);
		fsm.setTextFill(Color.WHITE);
		
		Label dm = new Label("DiskManager");
		dm.setGraphic(diskManager);
		dm.setTextFill(Color.WHITE);
		
		Label pm = new Label("ProcessManager");
		pm.setGraphic(processManager);
		pm.setTextFill(Color.WHITE);
		
		lauchPane.getChildren().add(fsm);
		lauchPane.getChildren().add(dm);
		lauchPane.getChildren().add(pm);
		
		lauchPane.setPadding(new Insets(UP_PADDING,RIGHT_PADDING,DOWN_PADDING,LEFT_PADDING));
		lauchPane.setVgap(50);
		
		fsm.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				fsm.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		fsm.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				fsm.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		fsm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if(event.getClickCount()==2) {
					System.out.println("enter the file system manager");
				}
			}
		});
		
		dm.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				dm.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		dm.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				dm.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		dm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if(event.getClickCount()==2) {
					System.out.println("enter the disk manager");
				}
			}
		});
		
		pm.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				pm.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		pm.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				pm.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		pm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if(event.getClickCount()==2) {
					System.out.println("enter the process manager");
				}
			}
		});
		
		BackgroundImage myBI= new BackgroundImage(new Image("/resource/haibao.png",PANEL_WIDTH+100,PANEL_HEIGHT+100,false,true),BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
		//then you set to your node
		lauchPane.setBackground(new Background(myBI));
		
		Scene scene = new Scene(lauchPane, PANEL_WIDTH, PANEL_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		/*
		 * try { Parent explorer =
		 * FXMLLoader.load(getClass().getResource("/src/ui/explorer.fxml"));
		 * primaryStage.setScene(new Scene(explorer, 900, 600)); primaryStage.show(); }
		 * catch (IOException e) { e.printStackTrace(); }
		 * 
		 * try { Parent editer =
		 * FXMLLoader.load(getClass().getResource("/src/ui/editor.fxml")); Scene scene =
		 * new Scene(editer,500,522); Stage editorStage = new Stage();
		 * editorStage.setScene(scene); editorStage.setResizable(true);
		 * editorStage.setTitle("editor"); editorStage.show(); } catch (IOException e) {
		 * e.printStackTrace(); } (new Terminal()).start(new Stage());
		 * 
		 * primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		 * 
		 * @Override public void handle(WindowEvent event) {
		 * DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK,
		 * AttrForFS.getFat()); } });
		 */
	}

	public static void main(String[] args) throws CloneNotSupportedException {
		AttrForFS.init();

		launch(args);
	}

}
