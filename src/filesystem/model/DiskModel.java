package filesystem.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskModel {
    public static final int BLOCK_COUNT = 256;
    public static final int BLOCK_SIZE = 64; // bytes

    private FATModel fat;
    private FileModel[] diskTable;
    
    public DiskModel() {
        this.diskTable = new FileModel[DiskModel.BLOCK_COUNT]; // initiate capacity of 128
        this.fat = new FATModel();
        FileModel root = new FileModel("root", 2); // root's attribute is directory, start index is 2
        this.diskTable[2] = root; // start begin is 2
    }

    /**
     * transform the disk model into byte stream, for saving to file
     * @return disk model in byte array
     */
    public byte[] toBytes() {
        byte[] result = new byte[DiskModel.BLOCK_COUNT*DiskModel.BLOCK_SIZE];
        byte[] fatInbyte = this.fat.toBytes();
        System.arraycopy(fatInbyte, 0, result, 0, fatInbyte.length);
        for (int i = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE; i < DiskModel.BLOCK_COUNT; ++i) {
            byte[] fileInbyte = new byte[DiskModel.BLOCK_SIZE];
            if (this.diskTable[i] != null) {
                fileInbyte = diskTable[i].contentToByte();
            }
            System.arraycopy(fileInbyte, 0, result, i*DiskModel.BLOCK_SIZE, fileInbyte.length);
        }
        return result;
    }

    public void saveToFile(String path) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(this.toBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void extractFromFile(String path) {
        File file = new File(path);
        byte[] data;
        try {
            FileInputStream fis = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FATModel getFat() {
        return this.fat;
    }
    public void setFat(FATModel fat) {
        this.fat = fat;
    }
    public FileModel[] getDiskTable() {
        return this.diskTable;
    }
    public void setDiskTable(FileModel[] diskTable) {
        this.diskTable = diskTable;
    }
}
