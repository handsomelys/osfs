package process;

import memory.Memory;
import memory.MemoryDispatcher;
import memory.RecordBlock;

import java.util.ArrayList;
import java.util.Map;

public class process {
    private PCB pcb;
    private int size;//进程大小;指条数
    private static MemoryDispatcher memoryDispatcher = MemoryDispatcher.getInstance();

    public static process createProcess(String[] instructions, String name) {
        process proc = new process();
        proc.size = instructions.length;
        proc.pcb = MemoryDispatcher.applyPCB(name);
        if (proc.pcb == null) {
            System.out.println("进程过多");
            return null;
        } else {
            setVariableArea(instructions, proc.pcb.getVariableArea());
            int testaddress;
            if (name.equals("闲逛进程")) {
                testaddress = 49;
            } else {
                testaddress = memoryDispatcher.findBlankAddress(instructions.length);
            }
            if (testaddress == -1) {
                System.out.println("内存空间不足");
                return null;
            } else {
                proc.pcb.setStartpointer(testaddress);
                proc.pcb.setPC(testaddress);
                proc.pcb.setEndpointer(testaddress + instructions.length * 10 - 1);
                MemoryDispatcher.storeInstruction(instructions, proc.pcb);
                // proc.pcb.setName(name);
                proc.pcb.setRuntime(instructions.length * 1.0);
                proc.pcb.setResttime(instructions.length * 1.0);
                proc.pcb.setName(name);
                proc.pcb.setLength(instructions.length);
                if (name.equals("闲逛进程")) {
                    return proc;
                }
                Memory.MAT.add(new RecordBlock(proc.pcb.nameProperty(), proc.pcb.getStartpointer(), proc.pcb.getEndpointer()));
                return proc;
            }

        }

    }

    public static int parseCommand(ArrayList<String> instructionList, String instructions, String name) {
        String instruction = new String();
        if (instructions.equals("")) {
            System.out.println("程序为空");
            return 0;
        }
        for (int i = 0; i < instructions.length(); i++) {
            if (instructions.charAt(i) == ';' || instructions.charAt(i) == ' ' || instructions.charAt(i) == '\n') {
                continue;
            } else {
                instruction += instructions.charAt(i);
            }
            if (instruction.length() == 3) {
                instructionList.add(instruction);
                instruction = "";
            }

        }
        if (!instructionList.get(instructionList.size() - 1).equals("end")) {
            System.out.println("程序未已end结束");
            return 1;
        }
        ProcessDispatcher.getReadyProcessList().add(process.createProcess((String[]) instructionList.toArray(new String[instructionList.size()]), name).getPcb());
        MemoryDispatcher.updateUI();
        ProcessDispatcher.showresources();
        return 2;

    }

    private static void setVariableArea(String[] program, Map<Character, Integer> VariableArea) {
        for (String str : program) {
            if (str.charAt(1) == '=') {
                VariableArea.put(new Character(str.charAt(0)), Integer.valueOf(str.substring(2)));
            }

        }

    }

    public PCB getPcb() {
        return pcb;
    }


}
