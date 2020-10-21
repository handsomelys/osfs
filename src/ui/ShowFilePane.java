package ui;
import controller.AttrForFS;
import filesystem.model.FileModel;
import filesystem.service.FileService;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import javax.swing.*;
import java.util.List;


public class ShowFilePane extends Application {
    private static Stage viewStage;
    private static String iconPath;
    public  static void main(String[] args){
    launch(args);
    }

    public static void show(FileModel parentFile, int fileAttribute, Stage stage, TilePane showPane){
        List<Object> currentSubs = FileService.getSubFiles(parentFile);
        //or this might be alert when the sub files is 7
        //go fucking it
        if(!FileService.addSubFileValid(parentFile)){
            System.out.println("this directory has 8 sub files");
        }

        stage = new Stage();
        stage.getIcons().add(new Image("src/resource/file.png"));
        BorderPane bp = new BorderPane();
        HBox fileNamePane = new HBox();
        fileNamePane.setPadding(new Insets(5,20,75,75));    //set the padding to the stage
        //random param, fix it later
        Button yes = new Button("yes");
        Button no = new Button("no");

        VBox selectBox = new VBox(new Node[]{yes,no});
        /*
        selectBox.getChildren().add(yes);
        selectBox.getChildren().add(no);
        */

        selectBox.setPadding(new Insets(50));
        selectBox.setSpacing(10);
        Label title = new Label("Input the name");
        TextField nameText = new TextField();
        HBox name = new HBox(new Node[]{title,nameText});

        fileNamePane.getChildren().addAll(new Node[]{selectBox,name});
        yes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){   //mouse one click event
                    FileModel newFile = new FileModel();
                    newFile.setAttribute(fileAttribute);
                    if (!FileService.validInputName(nameText.getText())){
                        System.out.println("error");
                        //raise error;
                    }
                    else{
                        newFile.setName(nameText.getText());
                        newFile.setParentFile(parentFile);

                        //judge the filetype to select the file icon
                        if(fileAttribute==FileModel.FILE){
                            //set the correspondent icon
                            iconPath = "src/resource/file.png";
                        }
                        else if(fileAttribute==FileModel.DIRECTORY){
                            //set the correspondent icon
                        }

                        newFile.setReadOnly(false); //set only read

                        if(FileService.addFile(newFile, AttrForFS.getDisk(),AttrForFS.getFat())){
                            //add new file to the panel
                            //showPane.getChildren().add()
                        }
                        else{
                            System.out.println("error");
                        }
                    }
                }
            }
        });

        no.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                viewStage.close();
            }
        });

        bp.setCenter(name);
        bp.setBottom(selectBox);
        Scene scene = new Scene(bp,300,100, Color.WHITE); //random param, fix it later
        viewStage.setScene(scene);
        viewStage.show();


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button yes = new Button("yes");
        Button no = new Button("no");
        GridPane gp = new GridPane();
        HBox selectBox = new HBox();
        selectBox.getChildren().add(yes);
        selectBox.getChildren().add(no);
        selectBox.setSpacing(10);
        selectBox.setPadding(new Insets(20));
        selectBox.setAlignment(Pos.CENTER);

        //gp.add(selectBox,0,1);
        //gp.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gp,800,800); //random param, fix it later
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
