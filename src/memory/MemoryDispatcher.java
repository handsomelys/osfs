package memory;

import javafx.scene.layout.Pane;
import process.PCB;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoryDispatcher
{
    private static final Memory memory= Memory.getInstance() ;
    private volatile static MemoryDispatcher memoryDispatcher;
    public static Pane memoryPane;
    private static Map<RecordBlock, MemoryRectangle> oldRecords;
    public static MemoryDispatcher getInstance() {
        if (memoryDispatcher == null) {
            synchronized (MemoryDispatcher.class){
                if (memoryDispatcher == null) {
                    memoryDispatcher = new MemoryDispatcher(Memory.getInstance());
                }
            }
        }
        return memoryDispatcher;
    }

    public static PCB applyPCB(String name){
        return memory.applyPCB(name);

    }

    public static void storeInstruction(String[] instructions,PCB pcb){
        memory.storeInstruction(instructions,pcb);
        return;
    }

    public static String fetchInstruction(int address){
        return memory.fetchInstruction(address);

    }
    private MemoryDispatcher(Memory memory) {

        this.oldRecords = new LinkedHashMap<>();
    }


    public static void mergeFragments() {
        List<RecordBlock> RecordBlocks = memory.getMAT();
        for (int i = 0; i < RecordBlocks.size(); i++) {
            int newStartAddress;
            RecordBlock record = RecordBlocks.get(i);
            MemoryRectangle rect1 = oldRecords.get(record);
            if (i == 0) {
                newStartAddress = 50;
            } else {
                newStartAddress = ((RecordBlocks.get(i - 1).getEndpointer()) + 1);
            }
            record.setStartpointer(newStartAddress);
            record.setEndpointer(newStartAddress+record.getSize()-1);
            MemoryRectangle rect = oldRecords.get(record);
            final double x=newStartAddress/512D;
            rect.layoutXProperty().bind(memoryPane.widthProperty().multiply(x));

        }

    }







    public int findBlankAddress(int size) {//寻找新的空白块地址,找到则返回,没有空白块就返回null
        List<RecordBlock> RecordBlocks = this.memory.getMAT();
        int startAddress = 0, endAddress = 0;
        if (RecordBlocks.size() == 0) {
            return 50;
        }

        for (int i = 0; i < RecordBlocks.size(); i++) {
            if (i == 0) {
                startAddress = 50;
                endAddress = ((RecordBlock)RecordBlocks.get(i)).getStartpointer()-1;
            } else {
                startAddress = ((RecordBlock)RecordBlocks.get(i - 1)).getEndpointer() + 1;
                endAddress = ((RecordBlock)RecordBlocks.get(i)).getStartpointer() -1;

            }
            if (endAddress - startAddress + 1 >= size) {
                return startAddress;
            }

        }
        if (RecordBlocks.size() > 0) {
            startAddress = ((RecordBlock)RecordBlocks.get(RecordBlocks.size() - 1)).getEndpointer() + 1;
            endAddress = 511;
            if (endAddress - startAddress + 1 >= size) {
                return startAddress;
            }
        }
        return -1;
    }
    public static void freePCB(PCB process) {

        memory.freePCB(process);
        return ;
    }

    public static void freeRecordBlock(PCB process) {
        String name = process.getName();
        memory.freeRecordBlock(name);
        return ;
    }

    public void setMemoryPane(Pane pane) {
            this.memoryPane = pane;
          }

    public static void updateUI() {
        List<RecordBlock> RecordBlocks = memory.getMAT();
        for (int i = 0; i < RecordBlocks.size(); i++) {
            RecordBlock newRecord = RecordBlocks.get(i);
            if (!oldRecords.keySet().contains(newRecord)) {//界面添加新的进程记录快(UI)
                PCB pcb = Memory.getPCBByName(newRecord.getName());
                MemoryRectangle rect = new MemoryRectangle(newRecord.getStartpointer(), newRecord.getSize(), memoryPane.getPrefHeight(), pcb);
                memoryPane.getChildren().add(rect);
                oldRecords.put(newRecord, rect);
            }
        }
        for (Map.Entry<RecordBlock, MemoryRectangle> entry : oldRecords.entrySet()) {//界面删除内存记录表中的进程(即运行完的进程)
            if (!RecordBlocks.contains(entry.getKey()))
                memoryPane.getChildren().remove(entry.getValue());
        }
    }
}
