package filesystem.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import filesystem.service.FATService;

public class DiskModel {
    public static final int BLOCK_COUNT = 256;
    public static final int BLOCK_SIZE = 64;    // bytes

    private FATModel fat;
    private FileModel[] diskTable;
    
    public DiskModel() {
        this.diskTable = new FileModel[DiskModel.BLOCK_COUNT];  // initiate capacity of 128
        this.fat = new FATModel();
        FileModel root = new FileModel("root", 2);  // root's attribute is directory, start index is 2
        this.diskTable[2] = root;   // start begin is 2
    }

    /**
     * save directory or file into the disk
     * @param file
     */
    public void saveFile(FileModel file) {
        this.diskTable[file.getStartIndex()] = file;
    }
    
    /**
     * delete object from disk
     * @param index
     */
    public void deleteFile(int index) {
        FATService.freeBlock(index, this.fat);
        this.diskTable[index] = null;
    }

    /**
     * get content from disk by index
     * @param index
     */
    public FileModel getDiskContent(int index) {
        return this.diskTable[index];
    }
    
    /**
     * save content into the disk
     * @param content
     * @param index
     */
    public void saveContent(FileModel content, int index) {
        FATService.SetBlockValue(255, this.fat, index);
        this.diskTable[index] = content;
    }
    
    // TODO: ?
    // apply free block, return the vacant index of disk, -1 stand for disk full
    // public static int applyFreeBlock(FATModel fat) {
    //     for(int i=3;i<fat.getTable().length;i++) {
    //         if(fat.getTable()[i]==0) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
    
    // check if the disk is valid
    // public static DiskModel checkDisk(DiskModel disk) {
    //     if(disk==null) {
    //         return new DiskModel();
    //     }
    //     return disk;
    // }
    
    //get the free blocks counts
    public int getDiskFreeCount() {
        return this.fat.getFreeCount();
    }
    
    public static HashMap<String, Object> getDirsAndFiles(DiskModel disk) {
        HashMap<String, Object> hashmap = new HashMap<String, Object>();
        List<Object> files = new ArrayList<>();
        List<Object> dirs = new ArrayList<>();
        List<Object> allFiles = new ArrayList<>();
        FileModel[] contents = disk.getDiskTable();
        
        for(int i=0;i<128;i++) {
            FileModel file = (FileModel) contents[i];
            allFiles.add(file);
            if(file.getAttribute()==1) //file
                files.add(file);
            else {
                dirs.add(file);	//directory
            }
            
        }
        
        hashmap.put("files", files);
        hashmap.put("dirs", dirs);
        hashmap.put("allFiles", allFiles);
        return hashmap;
    }
    
    public static int calculateNeedBlock(String Data) {
        if (Data != null) {
            double requireBlock = Data.length() * 1.0 / 64;
            return (int) Math.ceil(requireBlock);
        } else {
            return 0;
        }
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
            data = fis.readAllBytes();
            fis.close();
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
