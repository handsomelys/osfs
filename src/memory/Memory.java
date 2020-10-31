package memory;

import CPU.CPU;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import process.PCB;

import java.util.ArrayList;

public class Memory {
    private static String[] instructionArea;
    public static ObservableList<PCB> PCBlist;
    public static ArrayList<RecordBlock> MAT;
    private volatile static Memory memory;
    private static final int instructionAreaSize=512;
    private static final int PCBlistSize=10;
    public Memory(String[] instructionArea) {
        PCBlist=FXCollections.observableArrayList();
        this.instructionArea = instructionArea;
        this.PCBlist= FXCollections.observableArrayList();
        this.MAT=new ArrayList<>();

    }
    public static Memory getInstance() {//内存单例
        if (memory == null) {
            synchronized (Memory.class){
                if (memory==null){
                    memory = new Memory(new String[instructionAreaSize]);
                }
            }

        }
        return memory;
    }

    public PCB applyPCB( String name){
        if (PCBlist.size()==PCBlistSize){
            return null;
        }else {
            for (PCB pcb:PCBlist){
                if (pcb.getName().equals(name)){
                    System.out.println("进程名以被占用");
                    return null;
                }
            }
            PCB pcb=new PCB(name);
            PCBlist.add(pcb);
            return pcb;
        }

    }
    public  void storeInstruction(String[] instructions,PCB pcb){
        System.arraycopy(instructions,0,this.instructionArea,pcb.getStartpointer(),instructions.length);

    }
    public String fetchInstruction(int address){
        return this.instructionArea[address];
    }

    public void freePCB(PCB pcb){
        if (PCBlist.size()>0){
            PCBlist.remove(pcb);
        }
    }

    public void freeRecordBlock(String name){
        for (RecordBlock recordBlock:MAT){
            if (recordBlock.getName().equals(name)){
                MAT.remove(recordBlock);
                return;
            }
        }
    }
    public static void  setPCBToBlock(String name){

        for (PCB pcb:PCBlist){
            if (pcb.getName().equals(name)){
                pcb.setPSW(CPU.EQUIP_INTERMIT);
                //ProcessDispatcher.getBlockedProcessList().add(pcb);
            }
        }

    }

    public static void  setPCBToReady(String name){

        for (PCB pcb:PCBlist){
            if (pcb.getName().equals(name)){
                pcb.setPSW(CPU.NONE_INTERMIT);
            }
        }

    }
    public static  PCB getPCBByName(String  name){
        for (PCB pcb:PCBlist){
            if(pcb.getName().equals(name)){
                return pcb;
            }
        }
        return null;
    }

    public static int getMemoryUsage(){
        int size=49;
        for (PCB pcb:PCBlist){
            size+=pcb.getLength();
        }
        return size;
    }

    public static int getPCBlistSize(){
        return PCBlist.size();
    }

    public ObservableList<PCB> getPCBlist() {
        return PCBlist;
    }

    public  ArrayList<RecordBlock> getMAT() {
        return MAT;
    }


}
