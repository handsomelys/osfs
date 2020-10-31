package memory;

import javafx.beans.property.StringProperty;

public class RecordBlock {

    private StringProperty name;
    private int startpointer;//进程指令在程序区的起始地址
    private int endpointer;//进程指令在程序区的起始地址
    private int size;
    public RecordBlock(StringProperty name, int startpointer,int endpointer) {
        this.name = name;
        this.startpointer = startpointer;
        this.endpointer = endpointer;
        this.size=endpointer-startpointer+1;
    }

    public String getName() {
        return name.get();
    }

    public int getStartpointer() {
        return startpointer;
    }

    public void setStartpointer(int startpointer) {
        this.startpointer = startpointer;
    }

    public int getEndpointer() {
        return endpointer;
    }

    public void setEndpointer(int endpointer) {
        this.endpointer = endpointer;
    }

    public int getSize() {
        return size;
    }


}
