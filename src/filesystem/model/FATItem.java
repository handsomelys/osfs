package filesystem.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class FATItem {
    private IntegerProperty index;
    private IntegerProperty value;

    public FATItem(int index,int value){
        this.index = new SimpleIntegerProperty(index);
        this.value = new SimpleIntegerProperty(value);
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }
}
