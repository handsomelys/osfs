package controller;

import Action.createprocess;
import CPU.CPU;
import device.DeviceDispatcher;
import device.device;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import memory.MemoryDispatcher;
import process.PCB;
import process.ProcessDispatcher;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduiUIController implements Initializable{

    private CPU cpu;
    private MemoryDispatcher memoryDispatcher;
    private ProcessDispatcher processDispatcher;
    private DeviceDispatcher deviceDispatcher;
    private  FXMLLoader fxmlLoader;
    public static ScheduiUIController scheduiUIController;

    @FXML // fx:id="BlockedRRT"
    private TableColumn<PCB, DoubleProperty> BlockedRRT; // Value injected by FXMLLoader

    @FXML // fx:id="endedRT"
    private TableColumn<PCB, DoubleProperty> endedRT; // Value injected by FXMLLoader

    @FXML // fx:id="BlockedTRT"
    private TableColumn<PCB, DoubleProperty> BlockedTRT; // Value injected by FXMLLoader

    @FXML // fx:id="CPU"
    private ImageView CPU1; // Value injected by FXMLLoader

    @FXML // fx:id="endedName"
    private TableColumn<PCB, StringProperty> endedName; // Value injected by FXMLLoader

    @FXML // fx:id="DevicePName"
    private TableColumn<device, StringProperty> DevicePName; // Value injected by FXMLLoader

    @FXML // fx:id="processBar"
    private TableColumn<PCB, Double> processBar; // Value injected by FXMLLoader

    @FXML // fx:id="DeviceRT"
    private TableColumn<device, IntegerProperty> DeviceRT; // Value injected by FXMLLoader

    @FXML // fx:id="endedresult"
    private TableColumn<PCB, StringProperty> endedresult; // Value injected by FXMLLoader

    @FXML // fx:id="RemanTime"
    private TextField RemanTime; // Value injected by FXMLLoader

    @FXML // fx:id="RunningOrder"
    private TextField RunningOrder; // Value injected by FXMLLoader

    @FXML // fx:id="BlockedDeviceType"
    private TableColumn<PCB, Character> BlockedDeviceType; // Value injected by FXMLLoader

    @FXML // fx:id="Readylength"
    private TableColumn<PCB, IntegerProperty> Readylength; // Value injected by FXMLLoader

    @FXML // fx:id="ReadyRestTime"
    private TableColumn<PCB, DoubleProperty> ReadyRestTime; // Value injected by FXMLLoader

    @FXML // fx:id="BlockedProcess"
    private TableView<PCB> BlockedProcess; // Value injected by FXMLLoader

    @FXML // fx:id="ReadyRT"
    private TableColumn<PCB, DoubleProperty> ReadyRT; // Value injected by FXMLLoader

    @FXML // fx:id="BlockedName"
    private TableColumn<PCB, StringProperty> BlockedName; // Value injected by FXMLLoader

    @FXML // fx:id="ProcessUsage"

    private TextField ProcessUsage; // Value injected by FXMLLoader

    @FXML // fx:id="playBTN"
    private Button playBTN; // Value injected by FXMLLoader

    @FXML // fx:id="EndedTable"
    private TableView<PCB> EndedTable; // Value injected by FXMLLoader

    @FXML // fx:id="TemporaryResults"
    private TextField TemporaryResults; // Value injected by FXMLLoader

    @FXML // fx:id="RunnningProcess"
    private TextField RunnningProcess; // Value injected by FXMLLoader

    @FXML // fx:id="createBTN"
    private Button createBTN; // Value injected by FXMLLoader

    @FXML // fx:id="stackPane"
    private StackPane stackPane; // Value injected by FXMLLoader

    @FXML // fx:id="MemoryUsage"
    private TextField MemoryUsage; // Value injected by FXMLLoader

    @FXML // fx:id="DeviceUsage"
    private TextField DeviceUsage; // Value injected by FXMLLoader

    @FXML // fx:id="Ready"
    private TableView<PCB> Ready; // Value injected by FXMLLoader

    @FXML // fx:id="Readyname"
    private TableColumn<PCB, StringProperty> Readyname; // Value injected by FXMLLoader

    @FXML // fx:id="BlockeddeviceTime"
    private TableColumn<PCB, IntegerProperty> BlockeddeviceTime; // Value injected by FXMLLoader

    @FXML // fx:id="integrateBTN"
    private Button integrateBTN; // Value injected by FXMLLoader

    @FXML // fx:id="DeviceTable"
    private TableView<device> DeviceTable; // Value injected by FXMLLoader

    @FXML // fx:id="device"
    private TableColumn<device, Character> devicename; // Value injected by FXMLLoader

    @FXML
    private BorderPane borderPane;
    @FXML
    public Pane memorypane;
    @FXML
    private Path pathrunToEnd;
    @FXML
    private Path pathreadyToRun;
    @FXML
    private Path pathrunToReady;
    @FXML
    private Path pathblockTodevice;
    @FXML
    private VBox OSbox;
    @FXML
    private Pane panerunToEnd;
    @FXML
    private Pane panereadyToRun;
    @FXML
    private Pane panerunToReady;
    @FXML
    private Pane paneblockTodevice;
    @FXML
    private TextField Runnningtime;
    private ObservableList<Pane> panes;




    @FXML
    private void playorstop(ActionEvent event) {
        if(ProcessDispatcher.isrunning){
            ProcessDispatcher.stop();
            playBTN.setText("开始运行");
        }else{
            ProcessDispatcher.play();
            playBTN.setText("暂停运行");
        }
        return;
    }

    @FXML
    private void createprocess(ActionEvent event) {
        new createprocess();
        return;
    }

    @FXML
    private void integrateMemory(ActionEvent event) {
        MemoryDispatcher.mergeFragments();
        return;

    }

    @FXML
    private void jobScheduling(ActionEvent event){
        return;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     this.cpu= CPU.getInstance();
     this.memoryDispatcher= MemoryDispatcher.getInstance();
     this.processDispatcher= ProcessDispatcher.getInstance();
     this.deviceDispatcher= DeviceDispatcher.getInstance();
     this.CPU1.setImage(new Image("/resource/cpu1.png"));
     this.processDispatcher.setBlockedTableView(this.BlockedProcess,this.BlockedName,this.BlockedDeviceType,this.BlockeddeviceTime,this.BlockedTRT,this.BlockedRRT);
     this.processDispatcher.setDeviceTableView(this.DeviceTable,this.devicename,this.DevicePName,this.DeviceRT);
     this.processDispatcher.setEndTableView(this.EndedTable,this.endedRT,this.endedName,this.endedresult);
     this.processDispatcher.setReadyTableView(this.Ready,this.Readyname,this.ReadyRT,this.ReadyRestTime,this.Readylength,this.processBar);

     ProcessDispatcher.setPaneblockTodevice(this.paneblockTodevice);
     ProcessDispatcher.setPanedeviceToReady(this.panerunToReady);
     ProcessDispatcher.setPanereadyToRun(this.panereadyToRun);
     ProcessDispatcher.setPanerunToEnd(this.panerunToEnd);

     this.processDispatcher.setPathblockTodevice(this.pathblockTodevice);
     this.processDispatcher.setPathrunToReady(this.pathrunToReady);
     this.processDispatcher.setPathreadyToRun(this.pathreadyToRun);
     this.processDispatcher.setPathrunToEnd(this.pathrunToEnd);
     this.OSbox.prefWidthProperty().bind(memorypane.widthProperty().multiply((double)50/512));
     this.OSbox.prefHeightProperty().bind(memorypane.heightProperty());

     CPU.UI_CONTROLLER=this;
     ProcessDispatcher.scheduiUIController=this;
     MemoryDispatcher.memoryPane=memorypane;
     ProcessDispatcher.timekeeping();
     updateRunTime();

    }

    private void updateRunTime(){
        Thread thread=new Thread(){
            public void run(){
                while (true){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Runnningtime.setText(String.valueOf(ProcessDispatcher.getRunningtime()/1000.0D));
                        }
                    });
                    try {
                        Thread.sleep(1L);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
    public TextField getRemanTime() {
        return RemanTime;
    }

    public TextField getRunningOrder() {
        return RunningOrder;
    }


    public TextField getProcessUsage() {
        return ProcessUsage;
    }


    public TextField getTemporaryResults() {
        return TemporaryResults;
    }

    public TextField getRunnningProcess() {
        return RunnningProcess;
    }


    public TextField getMemoryUsage() {
        return MemoryUsage;
    }


    public TextField getDeviceUsage() {
        return DeviceUsage;
    }




}
