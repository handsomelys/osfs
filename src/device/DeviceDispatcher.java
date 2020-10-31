package device;

import CPU.CPU;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import memory.Memory;
import process.PCB;
import process.ProcessDispatcher;

import javax.swing.*;

public class DeviceDispatcher {

    private static int size = 8;
    public static ObservableList<device> deviceList;
    private static Timer[] equipTime = new Timer[size];
    private static Timer[] clockTime = new Timer[size];
    private volatile static DeviceDispatcher deviceDispatcher;

    static {
        deviceList = FXCollections.observableArrayList();
        deviceList.add(new device('A'));
        deviceList.add(new device('A'));
        deviceList.add(new device('B'));
        deviceList.add(new device('B'));
        deviceList.add(new device('B'));
        deviceList.add(new device('C'));
        deviceList.add(new device('C'));
        deviceList.add(new device('C'));
        ProcessDispatcher.setDeviceList(deviceList);

    }

    public static synchronized DeviceDispatcher getInstance() {
        if (deviceDispatcher == null) {
            synchronized(DeviceDispatcher.class){
                if (deviceDispatcher==null){
                    deviceDispatcher = new DeviceDispatcher();
                }
            }

        }
        return deviceDispatcher;
    }

    public static boolean checkEquipment(char name) {
        if (name == 'A' || name == 'a') {
            //System.out.println("检查A设备");
            if (!deviceList.get(0).isOccupied() || !deviceList.get(1).isOccupied()) {
                //System.out.println("得到A设备");
                return true;
            }

        } else if (name == 'B' || name == 'b') {
            if (!deviceList.get(2).isOccupied() || !deviceList.get(3).isOccupied() || !deviceList.get(4).isOccupied()) {
                return true;
            }

        } else if (name == 'C' || name == 'c') {
            if (!deviceList.get(5).isOccupied() || !deviceList.get(6).isOccupied() || !deviceList.get(7).isOccupied()) {
                return true;
            }

        }
        return false;
    }

    public static void requestDevice(PCB pcb, char name, int time) {
        Thread thread = new Thread() {
            public void run() {
                if (name == 'A') {
                    if (!deviceList.get(0).isOccupied()) {
                        deviceList.get(0).requestDevice(pcb.getName(), time);
                        setTimerState(time, 0,pcb);
                        return;
                    } else if (!deviceList.get(1).isOccupied()) {
                        deviceList.get(1).requestDevice(pcb.getName(), time);
                        setTimerState(time, 1,pcb);
                        return;
                    } else {
                        return;
                    }

                } else if (name == 'B') {
                    if (!deviceList.get(2).isOccupied()) {
                        deviceList.get(2).requestDevice(pcb.getName(), time);
                        setTimerState(time, 2,pcb);
                        return ;
                    } else if (!deviceList.get(3).isOccupied()) {
                        deviceList.get(3).requestDevice(pcb.getName(), time);
                        setTimerState(time, 3,pcb);
                        return ;
                    } else if (!deviceList.get(4).isOccupied()) {
                        deviceList.get(4).requestDevice(pcb.getName(), time);
                        setTimerState(time, 4,pcb);
                        return ;
                    } else {
                        return ;
                    }
                } else if (name == 'C') {
                    if (!deviceList.get(5).isOccupied()) {
                        deviceList.get(5).requestDevice(pcb.getName(), time);
                        setTimerState(time, 5,pcb);
                        return ;
                    } else if (!deviceList.get(6).isOccupied()) {
                        deviceList.get(6).requestDevice(pcb.getName(), time);
                        setTimerState(time, 6,pcb);
                        return;
                    } else if (!deviceList.get(7).isOccupied()) {
                        deviceList.get(7).requestDevice(pcb.getName(), time);
                        setTimerState(time, 7,pcb);
                        return ;
                    } else {
                        return ;
                    }
                } else
                    return ;

            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void setTimerState(int time, int index,PCB pcb) {

        equipTime[index] = new Timer(1000 * (time+1), e -> {//延时time秒
            Memory.setPCBToReady(deviceList.get(index).getProcess());
            deviceList.get(index).setOccupied(false);
            deviceList.get(index).setProcess("");
            ProcessDispatcher.showresources();
            CPU.WakedUP=true;
            //System.out.println("pcb时间"+pcb.getHoldTime());
            clockTime[index].stop();
            equipTime[index].stop();
        });

        clockTime[index] = new Timer(1000, e -> {//更新剩余时间
            pcb.setHoldTime(pcb.getHoldTime()-1);

            deviceList.get(index).setOccupiedTime(pcb.getHoldTime());

        });
        equipTime[index].start();
        clockTime[index].start();

    }

    public static int getFreeDeviceNum(){
        int num=8;
        for (device device:deviceList){
            if(device.isOccupied()){
                num--;
            }
        }
        return num;
    }

    public static ObservableList<device> getDeviceList() {
        return deviceList;
    }

}
