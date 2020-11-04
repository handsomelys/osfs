package filesystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import filesystem.service.FATService;


public class DiskModel implements Serializable {
    private static final long serialVersionUID = -5996007659520449539L;
    
    public static final int BLOCK_COUNT = 128;
    public static final int BLOCK_SIZE = 64; // bytes


    private filesystem.model.FATModel fat;
    private List<Object> diskTable;
    
    public DiskModel() {

        this.diskTable = new ArrayList<>(BLOCK_COUNT); // initiate capacity of 128
        this.fat = new filesystem.model.FATModel();
        initDiskModel();
    }

    private void initDiskModel() {
    	 
         for(int i=1;i<BLOCK_COUNT;i++){
            this.diskTable.add(null);
         }
         FileModel root = new FileModel();
         root.setParentFile(null);
         root.setAttribute(FileModel.ROOT);
         root.setStartIndex(2);
         root.setName("root");
         this.diskTable.add(2, root);;
    }
    /**
     * save directory or file into the disk
     * @param file
     */
    public void saveFile(FileModel file) {
        this.diskTable.set(file.getStartIndex(),file);
    }
    
    /**
     * delete object from disk
     * @param index
     */
    public void deleteFile(int index) {
        FATService.freeBlock(index, this.fat);
        this.diskTable.set(index, null) ;
    }

    /**
     * get content from disk by index
     * @param index
     */
    public FileModel getDiskContent(int index) {
        return (FileModel)this.diskTable.get(index);
    }
    
    /**
     * save content into the disk
     * @param content
     * @param index
     */
    public void saveContent(FileModel content, int index) {
        FATService.SetBlockValue(255, this.fat, index);
        this.diskTable.set(index,content);
    }
    
    //get the free blocks counts
    public int getDiskFreeCount() {
        return this.fat.getFreeCount();
    }
    
//    public static HashMap<String, Object> getDirsAndFiles(DiskModel disk) {
//        HashMap<String, Object> hashmap = new HashMap<String, Object>();
//        List<Object> files = new ArrayList<>();
//        List<Object> dirs = new ArrayList<>();
//        List<Object> allFiles = new ArrayList<>();
//        List<Object> contents = disk.getDiskTable();
//        
//        for(int i=0;i<128;i++) {
//            FileModel file = (FileModel) contents.get(i);
//            allFiles.add(file);
//            if(file.getAttribute()==1) //file
//                files.add(file);
//            else {
//                dirs.add(file);	//directory
//            }
//            
//        }
//        
//        hashmap.put("files", files);
//        hashmap.put("dirs", dirs);
//        hashmap.put("allFiles", allFiles);
//        return hashmap;
//    }
    
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
    /* reserved code
    public byte[] toBytes() {
        byte[] result = new byte[DiskModel.BLOCK_COUNT*DiskModel.BLOCK_SIZE];
        byte[] fatInbyte = this.fat.toBytes();
        // save the fat table to the head of disk
        System.arraycopy(fatInbyte, 0, result, 0, fatInbyte.length);
        // first value of i is the length of fat table
        for (int i = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE; i < DiskModel.BLOCK_COUNT; ++i) {
            byte[] fileInbyte = new byte[DiskModel.BLOCK_SIZE];
            Object o = this.diskTable.get(i);
            if (o != null) {
                if (o instanceof FileModel) {
                    fileInbyte = ((FileModel) o).toBytes();
                } else if (o instanceof String) {
                    fileInbyte = ((String) o).getBytes();
                }
            }
            System.arraycopy(fileInbyte, 0, result, i*DiskModel.BLOCK_SIZE, fileInbyte.length);
        }
        return result;
    }
    public void saveToFile(String path) throws FileNotFoundException, IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(this.toBytes());
        fos.close();
    }
    public void extractFromFile(String path) throws FileNotFoundException, IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data;
        data = fis.readAllBytes();
        fis.close();

        byte[] fat = new byte[DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE];
        System.arraycopy(data, 0, fat, 0, DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE);
        this.fat.formBytes(fat);
        
        for (int i = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE; i < DiskModel.BLOCK_COUNT; ++i) {
            byte[] fileInbyte = new byte[DiskModel.BLOCK_SIZE];
            Object o = this.diskTable.get(i);
            if (o != null) {
                if (o instanceof FileModel) {
                    fileInbyte = ((FileModel) o).toBytes();
                } else if (o instanceof String) {
                    fileInbyte = ((String) o).getBytes();
                }
            }
            System.arraycopy(data, i*DiskModel.BLOCK_SIZE, fileInbyte, 0, fileInbyte.length);
        }
    }
    */
    public filesystem.model.FATModel getFat() {
        return this.fat;
    }
    public void setFat(filesystem.model.FATModel fat) {
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
