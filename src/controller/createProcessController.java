package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import memory.Memory;
import process.PCB;
import process.ProcessDispatcher;
import process.process;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class createProcessController implements Initializable {

    private Memory memory=Memory.getInstance();
    private ProcessDispatcher  processDispatcher=ProcessDispatcher.getInstance();
    @FXML
    private TextArea instructiontext;

    @FXML
    private TextField processNameText;

    @FXML
    private Button createprocessBtn;

    @FXML
    public void createprocess(ActionEvent event) {
        String name=new String();
        name=processNameText.getText();
        ArrayList<String> instructionList=new ArrayList<>();
        String instructions=instructiontext.getText();
        int flag=process.parseCommand(instructionList,instructions,name);
        if (flag==0){
            alertinstruction();
            return;
        }else if (flag==1){
            alertEnd();
            return;
        }else {
            Stage stage = (Stage)createprocessBtn.getScene().getWindow();
            stage.close();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instructiontext.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (IsRepeated(processNameText.getText())){
                    alertname();
                }
            }
        });
    }
    private boolean IsRepeated(String name){
        if (memory.getPCBlist().size()==0){
            return false;
        }
        for (PCB pcb:memory.getPCBlist()){
            if (pcb.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private void alertname(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.setHeaderText(null);
        alert.headerTextProperty().set("进程名已存在");
        alert.showAndWait();
    }

    private void alertinstruction(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.setHeaderText(null);
        alert.headerTextProperty().set("输出指令为空,无法进行创建进程");
        alert.showAndWait();
    }

    private void alertEnd(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("提示");
        alert.setHeaderText(null);
        alert.headerTextProperty().set("程序未已\"end\"结尾,请程序结束处添加end指令");
        alert.showAndWait();
    }


}
