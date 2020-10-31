package device;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class device {
    private Character name;
    private StringProperty process;
    private boolean occupied;
    private IntegerProperty occupiedTime;

    public device(char name) {
        this.name = name;
        process=new SimpleStringProperty();
        process.set("");
        occupiedTime=new SimpleIntegerProperty();
        occupiedTime.set(0);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getProcess() {
        return process.get();
    }

    public void setProcess(String process) {
        this.process.set(process);
    }

    public void setOccupiedTime(int occupiedTime) {
        this.occupiedTime.set(occupiedTime);
    }

    public void requestDevice(String name, int time){
            this.process.set(name);
            this.occupiedTime.set(time);
            setOccupied(true);
            return;

    }

    public Character getName() {
        return name;
    }

    public void setName(Character name) {
        this.name = name;
    }

    public StringProperty processProperty() {
        return process;
    }

    public int getOccupiedTime() {
        return occupiedTime.get();
    }

    public IntegerProperty occupiedTimeProperty() {
        return occupiedTime;
    }
}
