package filesystem.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DiskModel {
    public static final int BLOCK_COUNT = 128;
    public static final int BLOCK_SIZE = 64; // bytes

    private FATModel fat;
    private List<Object> diskTable;
    
    public DiskModel() {
        this.diskTable = new ArrayList<>(BLOCK_COUNT); // initiate capacity of 128
        this.fat = new FATModel();
        initDiskModel();
    }

    private void initDiskModel() {
    	 
         for(int i=1;i<BLOCK_COUNT;i++){
            this.diskTable.add(null);
         }
         FileModel root = new FileModel();
         root.setParentFile(null);
         root.setAttribute(3);
         root.setStartIndex(2);
         root.setName("¸ùÄ¿Â¼");
         this.diskTable.add(2, root);;
    }
    /**
     * transform the disk model into byte stream, for saving to file
     * @return disk model in byte array
     */
    /*
    public byte[] toBytes() {
        byte[] result = new byte[DiskModel.BLOCK_COUNT*DiskModel.BLOCK_SIZE];
        byte[] fatInbyte = this.fat.toBytes();
        System.arraycopy(fatInbyte, 0, result, 0, fatInbyte.length);
        for (int i = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE; i < DiskModel.BLOCK_COUNT; ++i) {
            byte[] fileInbyte = new byte[DiskModel.BLOCK_SIZE];
            if (this.diskTable.get(i) != null) {
                fileInbyte = diskTable[i].contentToByte();
            }
            System.arraycopy(fileInbyte, 0, result, i*DiskModel.BLOCK_SIZE, fileInbyte.length);
        }
        return result;
    }
     */
    /*
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
	*/
    public FATModel getFat() {
        return this.fat;
    }
    public void setFat(FATModel fat) {
        this.fat = fat;
    }
    public List<Object> getDiskTable() {
        return this.diskTable;
    }
    public void setDiskTable(List<Object> diskTable) {
        this.diskTable = diskTable;
    }
    
    @Override
    public String toString() {
        return "Disk{" +
                "disk=" + this.getDiskTable() +
                '}';
    }
}
