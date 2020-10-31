package process;

import CPU.CPU;
import controller.ScheduiUIController;
import device.DeviceDispatcher;
import device.device;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import memory.Memory;
import memory.MemoryDispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessDispatcher {

    private process runningProcess;
    public static volatile boolean isrunning = false;
    private static PCB hangOutProcess;
    private static PCB processRegister;
    private static ObservableList<PCB> endProcessList;
    private static ObservableList<PCB> readyProcessList;
    private static ObservableList<PCB> blockedProcessList;
    private static ObservableList<device> deviceList;
    //private final Thread dispatchThread;D
    private volatile static ProcessDispatcher processDispatcher;
    public static ScheduiUIController scheduiUIController;
    private static DeviceDispatcher DEVICE_DISPATCHER;
    private static Path pathrunToEnd;
    private static Path pathreadyToRun;
    private static Path pathblockTodevice;
    private static Path pathrunToReady;
    private static PathTransition runToEnd;
    private static PathTransition readyToRun;
    private static PathTransition blockTodevice;
    private static PathTransition runToReady;
    private static Pane panerunToEnd;
    private static Pane panereadyToRun;
    private static Pane paneblockTodevice;
    private static Pane panerunToReady;
    public static Thread DispatherThread;
    private static volatile DoubleProperty runningtime;
    private static volatile double laststoptime;
    private static volatile double custarttime;

    public static ProcessDispatcher getInstance() {

        if (processDispatcher == null) {
            synchronized (ProcessDispatcher.class) {
                if (processDispatcher == null) {
                    processDispatcher = new ProcessDispatcher();
                }
            }
        }
        return processDispatcher;
    }

    static {

        runningtime = new SimpleDoubleProperty();
        laststoptime = 0.0;
        custarttime = 0.0;
        blockedProcessList = FXCollections.observableArrayList();
        readyProcessList = FXCollections.observableArrayList();
        endProcessList = FXCollections.observableArrayList();
        runToEnd = new PathTransition();
        readyToRun = new PathTransition();
        blockTodevice = new PathTransition();
        runToReady = new PathTransition();
        //processRegister=new process();
        String[] end = new String[]{"end"};
        hangOutProcess = process.createProcess(end, "闲逛进程").getPcb();
        deviceList = DeviceDispatcher.deviceList;
    }

    // 调度进程
    public static void dispatchProcess() {
        DispatherThread = new Thread() {
            public void run() {
                while (true) {
                    if (isrunning) {
                        if (CPU.WakedUP) {
                            saveCPUprocessRegister();
                            awakeProcess();
                        }
                        if (CPU.PSW == CPU.NONE_INTERMIT || CPU.PSW == CPU.NORMAL_INTERMIT) {
                            if (CPU.PSW == CPU.NORMAL_INTERMIT && processRegister != null) {
                                destroyProcess();
                            }
                            dispatchReadyProcess();
                            CPU.run();

                        }else if (CPU.PSW == CPU.TIME_INTERMIT) {
                            if (processRegister == null) {
                                CPU.PSW = 0;
                                CPU.run();
                            } else {
                                saveCPUprocessRegister();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dispatchReadyProcess();
                                CPU.run();
                            }
                        }
                        else {
                            if (processRegister == null) {
                                CPU.PSW = CPU.NONE_INTERMIT;
                            } else {
                                blockPocess();

                                dispatchReadyProcess();
                            }
                            CPU.run();
                        }
                    }
                }
            }
        };
        DispatherThread.setDaemon(true);
        DispatherThread.start();

    }

    public static void timekeeping() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    if (isrunning) {
                        runningtime.set(laststoptime + System.currentTimeMillis() - custarttime);
                    }
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void awakeProcess() {
        if (blockedProcessList.size() != 0) {
            int i = -1;
            for (PCB pcb : blockedProcessList) {
                if (pcb.getHoldTime() == 0) {
                    i = blockedProcessList.indexOf(pcb);
                    break;
                }
            }
            if (i != -1) {
                PCB pcb = blockedProcessList.get(i);
                pcb.setPC(pcb.getPC() + 1);
                blockedProcessList.remove(pcb);
                checkBlock(pcb.getTakeddevice());
                processRegister = pcb;
                setCPURegister(processRegister);
                CPU.WakedUP = false;
                CPU.run();
            }
        }

        return;
    }

    public static void destroyProcess() {
        playrunToEnd();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endProcessList.add(CPU.pcb);
        MemoryDispatcher.freePCB(processRegister);
        MemoryDispatcher.freeRecordBlock(processRegister);
        showresources();
        updateUI();
    }

    public static void dispatchReadyProcess() {
        if (readyProcessList.size() != 0) {
            playreadyToRun();
            processRegister = readyProcessList.remove(0);
            setCPURegister(processRegister);
        } else {
            processRegister = null;
            setCPURegister(hangOutProcess);
        }

    }

    public static void saveCPUprocessRegister() {
        if (processRegister != null) {
            processRegister = CPU.pcb;
            playrunToReady();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readyProcessList.add(processRegister);
        }
    }

    public static void blockPocess() {
        processRegister = CPU.pcb;
        blockedProcessList.add(processRegister);
        takeDevice(processRegister);

    }

    //检查阻塞队列是否获得设备
    public static void checkBlock(Character devicename) {

        if (blockedProcessList.size() != 0) {
            for (PCB pcb : blockedProcessList) {
                if (pcb.getNeeddevice().equals(devicename) && pcb.getTakeddevice() == 'x') {
                    if (DeviceDispatcher.checkEquipment(pcb.getNeeddevice())) {
                        showresources();
                        pcb.setTakeddevice(devicename.charValue());
                        playblockTodevice();
                        DeviceDispatcher.requestDevice(pcb, pcb.getNeeddevice(), pcb.getHoldTime());
                    }
                }
            }
        }
    }

    public static void takeDevice(PCB pcb) {
        String Instruction = MemoryDispatcher.fetchInstruction(pcb.getPC());
        System.out.println();
        if (DeviceDispatcher.checkEquipment(Instruction.charAt(1))) {
            showresources();
            pcb.setTakeddevice(Instruction.charAt(1));
            playblockTodevice();
            DeviceDispatcher.requestDevice(pcb, Instruction.charAt(1), Instruction.charAt(2) - '0');
        }

    }

    public static void updateUI() {
        Platform.runLater(new Runnable() {
            public void run() {
                MemoryDispatcher.updateUI();
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setCPURegister(PCB p) {
        CPU.PC = p.getPC();
        CPU.PSW = CPU.NONE_INTERMIT;
        CPU.pcb = p;
    }

    public static void showresources() {
        Platform.runLater(new Runnable() {
            public void run() {
                scheduiUIController.getMemoryUsage().setText(String.valueOf(Memory.getMemoryUsage()) + "/512字节");
                scheduiUIController.getProcessUsage().setText(String.valueOf(Memory.getPCBlistSize() + "/10个PCB"));
                scheduiUIController.getDeviceUsage().setText(String.valueOf(DeviceDispatcher.getFreeDeviceNum()) + "/8台设备");
            }
        });
    }

    public static void stop() {
        isrunning = false;
        laststoptime = runningtime.get();

    }

    public static void play() {
        isrunning = true;
        custarttime = System.currentTimeMillis();
    }

    public static void setPanerunToEnd(Pane panerunToEnd1) {
        panerunToEnd = panerunToEnd1;
    }

    public static void setPanereadyToRun(Pane panereadyToRun1) {
        panereadyToRun = panereadyToRun1;
    }

    public static void setPaneblockTodevice(Pane paneblockTodevice1) {
        paneblockTodevice = paneblockTodevice1;
    }

    public static void setPanedeviceToReady(Pane panerunToReady1) {
        panerunToReady = panerunToReady1;
    }

    public void setReadyTableView(TableView<PCB> tableView, TableColumn<PCB, StringProperty> Readyname, TableColumn<PCB, DoubleProperty> ReadyRt, TableColumn<PCB, DoubleProperty> ReadyRestTime, TableColumn<PCB, IntegerProperty> Readylength, TableColumn<PCB, Double> processBar) {
        Readyname.setCellValueFactory(new PropertyValueFactory<>("name"));
        ReadyRt.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        ReadyRestTime.setCellValueFactory(new PropertyValueFactory<>("resttime"));
        Readylength.setCellValueFactory(new PropertyValueFactory<>("length"));
        processBar.setCellValueFactory(new PropertyValueFactory<>("progress"));
        processBar.setCellFactory(ProgressBarTableCell.forTableColumn());
        tableView.setItems(this.readyProcessList);
    }

    public void setBlockedTableView(TableView<PCB> BlockedProcess, TableColumn<PCB, StringProperty> BlockedName, TableColumn<PCB, Character> BlockedDeviceType, TableColumn<PCB, IntegerProperty> BlockeddeviceTime, TableColumn<PCB, DoubleProperty> BlockedTRT, TableColumn<PCB, DoubleProperty> BlockedRRT) {
        BlockedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        BlockedTRT.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        BlockedRRT.setCellValueFactory(new PropertyValueFactory<>("resttime"));
        BlockedDeviceType.setCellValueFactory(new PropertyValueFactory<>("needdevice"));
        BlockeddeviceTime.setCellValueFactory(new PropertyValueFactory<>("holdTime"));
        BlockedProcess.setItems(this.blockedProcessList);

    }

    public void setEndTableView(TableView<PCB> EndedTable, TableColumn<PCB, DoubleProperty> endedRT, TableColumn<PCB, StringProperty> endedName, TableColumn<PCB, StringProperty> endedresult) {
        endedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        endedRT.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        endedresult.setCellValueFactory(new PropertyValueFactory<>("result"));
        EndedTable.setItems(this.endProcessList);

    }

    public void setDeviceTableView(TableView<device> DeviceTable, TableColumn<device, Character> device, TableColumn<device, StringProperty> DevicePName, TableColumn<device, IntegerProperty> DeviceRT) {
        device.setCellValueFactory(new PropertyValueFactory<>("name"));
        DevicePName.setCellValueFactory(new PropertyValueFactory<>("process"));
        DeviceRT.setCellValueFactory(new PropertyValueFactory<>("occupiedTime"));
        DeviceTable.setItems(DeviceDispatcher.deviceList);

    }

    public static void setPathrunToEnd(Path pathrunToEnd1) {
        pathrunToEnd = pathrunToEnd1;
        runToEnd.setPath(pathrunToEnd);
        runToEnd.setCycleCount(1);
        runToEnd.setDuration(Duration.seconds(0.5));
    }

    public static void setPathreadyToRun(Path pathreadyToRun1) {
        pathreadyToRun = pathreadyToRun1;
        readyToRun.setPath(pathreadyToRun);
        readyToRun.setCycleCount(1);
        readyToRun.setDuration(Duration.seconds(1));
    }

    public static void setPathblockTodevice(Path pathblockTodevice1) {
        pathblockTodevice = pathblockTodevice1;
        blockTodevice.setPath(pathblockTodevice);
        blockTodevice.setCycleCount(1);
        blockTodevice.setDuration(Duration.seconds(1));
    }

    public static void setPathrunToReady(Path pathrunToReady1) {
        pathrunToReady = pathrunToReady1;
        runToReady.setPath(pathrunToReady);
        runToReady.setCycleCount(1);
        runToReady.setDuration(Duration.seconds(1));
    }

    public static void playrunToEnd() {//队列之间的小球转移
        final Rectangle R = new Rectangle(20, 20);
        R.setFill(Paint.valueOf("#48D1CC"));
        (new Thread(new Runnable() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        panerunToEnd.getChildren().add(R);
                    }
                });
            }
        })).start();
        runToEnd.setNode(R);
        runToEnd.play();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessDispatcher.class.getName()).log(Level.SEVERE, (String) null, ex);
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        panerunToEnd.getChildren().remove(R);
                    }
                });
            }
        })).start();
    }

    public static void playreadyToRun() {//队列之间的小球转移
        final Rectangle R = new Rectangle(20, 20);
        R.setFill(Paint.valueOf("#FFA500"));
        (new Thread(new Runnable() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        panereadyToRun.getChildren().add(R);
                    }
                });
            }
        })).start();

        readyToRun.setNode(R);
        readyToRun.play();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessDispatcher.class.getName()).log(Level.SEVERE, (String) null, ex);
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        panereadyToRun.getChildren().remove(R);
                    }
                });
            }
        })).start();
    }

    public static void playblockTodevice() {//队列之间的小球转移
        final Rectangle R = new Rectangle(20, 20);
        R.setFill(Paint.valueOf("#FF6347"));
        (new Thread(new Runnable() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        paneblockTodevice.getChildren().add(R);
                    }
                });
            }
        })).start();
        blockTodevice.setNode(R);
        blockTodevice.play();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessDispatcher.class.getName()).log(Level.SEVERE, (String) null, ex);
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        paneblockTodevice.getChildren().remove(R);
                    }
                });
            }
        })).start();
    }

    public static void playrunToReady() {//队列之间的小球转移
        final Rectangle R = new Rectangle(20, 20);
        R.setFill(Paint.valueOf("#48D1CC"));
        (new Thread(new Runnable() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        panerunToReady.getChildren().add(R);
                    }
                });
            }
        })).start();
        runToReady.setNode(R);
        runToReady.play();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessDispatcher.class.getName()).log(Level.SEVERE, (String) null, ex);
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        panerunToReady.getChildren().remove(R);
                    }
                });
            }
        })).start();
    }

    public static ObservableList<PCB> getReadyProcessList() {
        return readyProcessList;
    }

    public static void setReadyProcessList(ObservableList<PCB> readyProcessList) {
        ProcessDispatcher.readyProcessList = readyProcessList;
    }

    public static ObservableList<PCB> getBlockedProcessList() {
        return blockedProcessList;
    }

    public static ObservableList<device> getDeviceList() {
        return deviceList;
    }

    public static void setDeviceList(ObservableList<device> deviceList) {
        ProcessDispatcher.deviceList = deviceList;
    }

    public static double getRunningtime() {
        return runningtime.get();
    }

}
