package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.DiskViewer;
import ui.FATViewer;
import ui.Terminal;
import util.UIError;

import java.io.IOException;

import controller.AttrForFS;
import filesystem.model.FATItem;
import filesystem.service.DiskService;

public class Main extends Application {

	public static final String DISK = "disk.dat";
	public static final int UP_PADDING = 0;
	public static final int RIGHT_PADDING = 0;
	public static final int DOWN_PADDING = 0;
	public static final int LEFT_PADDING = 0;
	public static final int PANEL_HEIGHT = 600;
	public static final int PANEL_WIDTH = 900;

	@Override
	public void start(Stage primaryStage) {

		HBox lauchPane = new HBox();
		
		ImageView fsManager = new ImageView(new Image("/resource/explorer.png"));
		ImageView diskManager = new ImageView(new Image("/resource/terminal.png"));
		ImageView processManager = new ImageView(new Image("/resource/process.png"));
		 
		Label em = new Label("Explorer");
		em.setGraphic(fsManager);
		em.setTextFill(Color.WHITE);
		em.setWrapText(true);
		em.setContentDisplay(ContentDisplay.TOP);
		em.setFont(Font.font(24));
		
		Label tm = new Label("Terminal");
		tm.setGraphic(diskManager);
		tm.setTextFill(Color.WHITE);
		tm.setWrapText(true);
		tm.setContentDisplay(ContentDisplay.TOP);
		tm.setFont(Font.font(24));
		
		Label pm = new Label(" Process\nManager");
		pm.setGraphic(processManager);
		pm.setTextFill(Color.WHITE);
		pm.setWrapText(true);
		pm.setContentDisplay(ContentDisplay.TOP);
		pm.setFont(Font.font(24));
		
		
		lauchPane.getChildren().addAll(em,tm,pm);
		lauchPane.setSpacing(125);
		lauchPane.setAlignment(Pos.CENTER);
//		lauchPane.setPadding(new Insets(UP_PADDING,RIGHT_PADDING,DOWN_PADDING,LEFT_PADDING));
		//lauchPane.setVgap(50);
		
		em.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				em.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		em.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				em.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		em.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2) {
					System.out.println("enter the file system manager");
				}
			}
		});
		
		tm.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tm.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		tm.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				tm.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		tm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2) {
					System.out.println("enter the disk manager");
				}
			}
		});
		
		pm.addEventFilter(MouseDragEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				pm.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,null,null)));
			}			
		});
		
		pm.addEventFilter(MouseDragEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				pm.setBackground(new Background(new BackgroundFill(null,null,null)));
			}
		});
		
		pm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount()==2) {
					System.out.println("enter the process manager");
				}
			}
		});
		
		BackgroundImage myBI= new BackgroundImage(new Image("/resource/background.jpg",PANEL_WIDTH+15,PANEL_HEIGHT+50,false,true),BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
		//then you set to your node
		lauchPane.setBackground(new Background(myBI));
		lauchPane.setPadding(new Insets(100,100,100,100));
		Scene scene = new Scene(lauchPane, PANEL_WIDTH, PANEL_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
//		UIError.alertInformation("hello", "world", primaryStage);
		primaryStage.getIcons().add(new Image("/resource/Logo.png"));
		primaryStage.setTitle("Operation System Lauch");
		primaryStage.show();
	
//		try {
//			Parent explorer = FXMLLoader.load(getClass().getResource("/src/ui/explorer.fxml"));
//			primaryStage.setScene(new Scene(explorer, 900, 600));
//			primaryStage.show();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			Parent editer = FXMLLoader.load(getClass().getResource("/src/ui/editor.fxml"));
//			Scene scene = new Scene(editer, 500, 522);
//			Stage editorStage = new Stage();
//			editorStage.setScene(scene);
//			editorStage.setResizable(true);
//			editorStage.setTitle("editor");
//			editorStage.show();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		(new Terminal()).start(new Stage());
//
//		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//
//			@Override
//			public void handle(WindowEvent event) {
//				DiskService.save2Disk(AttrForFS.getDisk(), main.Main.DISK, AttrForFS.getFat());
//			}
//		});
		//-----------------FATVIEWER--------------------------- 
//		AnchorPane pane = new AnchorPane();
//		TableView<FATItem> tv = new TableView<FATItem>();
//		TableColumn<FATItem,Number> tcFATIndex = new TableColumn<>("index");
//		TableColumn<FATItem,Number> tcFATValue = new TableColumn<>("value");
//		FATViewer fatviewer = new FATViewer(tv, tcFATIndex, tcFATValue);
//		pane.getChildren().add(tv);
//		fatviewer.update();
//		tv.prefWidthProperty().bind(pane.widthProperty());
//        tv.prefHeightProperty().bind(pane.heightProperty());
//		Scene scene = new Scene(pane,500,500);
//		primaryStage.setScene(scene);
//		primaryStage.show();
		//-----------------DISKVIWER-----------------------------

//		DiskViewer diskviewer = new DiskViewer();
//		diskviewer.show();	//directory use disk viewer that its private stage will show, or you might want to change the show method
		
//		Scene scene = new Scene(diskpane);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//		
	}

	public static void main(String[] args) throws CloneNotSupportedException {
		AttrForFS.init();

		launch(args);
	}

}
