package CPU;

import controller.ScheduiUIController;
import javafx.application.Platform;
import memory.Memory;
import memory.MemoryDispatcher;
import process.PCB;
import process.ProcessDispatcher;

import java.util.Map;

public class CPU {
    private static final long serialVersionUID = 1L;
    public static int PC;
    public static int PSW;
    public static PCB pcb;
    public static String Instruction = new String();
    public static String resultString = new String();
    public static volatile boolean WakedUP = false;
    public static final int NONE_INTERMIT = 0;  //无中断
    public static final int TIME_INTERMIT = 1;  //时间片到中断
    public static final int NORMAL_INTERMIT = 2;  //正常中断
    public static final int EQUIP_INTERMIT = 3;   //设备中断
    public static final char[] IR = new char[3];
    public static ScheduiUIController UI_CONTROLLER;
    private static int timeSize = 6;   //时间片大小
    private volatile static CPU cpu;



    public static CPU getInstance() {
        if (cpu == null) {
            synchronized(CPU.class){
                if (cpu==null){
                    cpu = new CPU();
                }
            }

        }
        return cpu;
    }

    public static void run() {

        int time = timeSize;

        while (true) {
            if (ProcessDispatcher.isrunning) {
                if (WakedUP) {
                    UpdatePCB();
                    return;
                }
                if (PSW == CPU.NORMAL_INTERMIT) {
                    //System.out.println(pcb.getProgress());
                    UpdatePCB();
                    return;
                } else if (CPU.PSW == CPU.TIME_INTERMIT) {
                    UpdatePCB();
                    return;
                } else if (PSW == CPU.EQUIP_INTERMIT) {
                    UpdatePCB();
                    return;
                }
                Instruction = MemoryDispatcher.fetchInstruction(PC);
                if (!pcb.getName().equals("闲逛进程")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < 3; i++) IR[i] = Instruction.charAt(i);
                //执行指令
                char v = executeOrder();
                pcb.setResttime(pcb.getResttime() - 1);
                pcb.setProgress(1 - (pcb.getResttime() / pcb.getRuntime()));
                //显示中间结果
                resultString = "";
                if (!pcb.getVariableArea().isEmpty()) {
                    for (Map.Entry<Character, Integer> entry : pcb.getVariableArea().entrySet()) {
                        resultString += entry.getKey() + "=" + entry.getValue() + ";";
                    }
                }
                Platform.runLater(new Runnable() {
                    public void run() {
                        UI_CONTROLLER.getRunningOrder().setText(Instruction);
                        UI_CONTROLLER.getRunnningProcess().setText(pcb.getName());
                        UI_CONTROLLER.getTemporaryResults().setText(resultString);
                        UI_CONTROLLER.getRemanTime().setText(String.valueOf(pcb.getResttime()));

                    }
                });

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time--;
                //如果是设备中断，则保存这一条指令方便检查
                if (CPU.PSW == CPU.EQUIP_INTERMIT) {
                    UpdatePCB();
                    return;
                }

                /*如果时间片到了而程序还未正常结束就把PSW的状态设置成时间片到中断,
                 *但是如果时间片到了此时程序正好结束就把PSW设置成程序正常结束软中
                 *断而忽略时间片到中断*/
                if (time == 0 && CPU.PSW != CPU.NORMAL_INTERMIT) {
                    CPU.PSW = CPU.TIME_INTERMIT;
                }

                PC++;
            }
        }
    }

    private static char executeOrder() {

        if (IR[0] == 'e' && IR[1] == 'n' && IR[2] == 'd') {
            PSW = CPU.NORMAL_INTERMIT;
            pcb.OutputResuit();
            return '&';
        } else if (IR[0] == '!') {
            Memory.setPCBToBlock(pcb.getName());
            PSW = CPU.EQUIP_INTERMIT;
            pcb.setNeeddevice(IR[1]);
            pcb.setHoldTime(IR[2] - '0');
            return '&';
        }//设备管理
        else {
            char variable_name;
            int variable = 0;
            variable_name = IR[0];
            if (IR[1] == '=') {
                variable = IR[2] - 48;
            } else if (IR[1] == '+') {
                variable = pcb.getVariableArea().get(variable_name) + 1;

            } else if (IR[1] == '-') {
                variable = pcb.getVariableArea().get(variable_name) - 1;
            }
            pcb.getVariableArea().put(variable_name, variable);
            return IR[0];
        }
    }

    private static void UpdatePCB() {
        pcb.setPSW(PSW);
        if (pcb.getName().equals("闲逛进程")) {
            PC = 49;
            pcb.setResttime(0.0);
        }
        pcb.setPC(PC);
    }

}
