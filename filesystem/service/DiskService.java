package filesystem.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;

public class DiskService {
	
	//save directory or file into the disk
	public static void saveFile(FileModel file, DiskModel disk) {
		disk.getDiskTable().set(file.getStartIndex(), file);
	}
	
	//archieve content from disk by index
	public static Object getDiskContent(int index,DiskModel disk) {
		return disk.getDiskTable().get(index);
	}
	
	//save content into the disk
	//content's type is object, it can be file model or string or others
	public static void saveContent(Object content,DiskModel disk,int index) {
		filesystem.service.FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		disk.getDiskTable().set(index, content);
	}
	
	//apply free block, return the vacant index of disk, -1 stand for disk full
	public static int applyFreeBlock(FATModel fat) {
		for(int i=3;i<fat.getTable().length;i++) {
			if(fat.getTable()[i]==0) {
				return i;
			}
		}
		return -1;
	}
	
	//delete object from disk
	public static void deleteObject(DiskModel disk,int index) {
		filesystem.service.FATService.freeBlock(index, AttrForFS.getFat());
		disk.getDiskTable().set(index, null);
	}
	
	//check if the disk is valid
	public static DiskModel checkDisk(DiskModel disk) {
		if(disk==null) {
			return new DiskModel();
		}
		return disk;
	}
	
	//get the free blocks counts
	public static int getDiskFreeCnt() {
		return AttrForFS.getFat().getFreeCount();
	}
	

	public static HashMap<String, Object> getDirsAndFiles(DiskModel disk) {
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		List<Object> files = new ArrayList<>();
		List<Object> dirs = new ArrayList<>();
		List<Object> allFiles = new ArrayList<>();
		List<Object> contents = disk.getDiskTable();
		
		for(int i=0;i<DiskModel.BLOCK_SIZE;i++) {
			if(contents.get(i) instanceof FileModel) {
				FileModel file = (FileModel) contents.get(i);
				allFiles.add(file);
				if(file.getAttribute()==2) {
					files.add(file);
				}
				else {
					dirs.add(file);
				}
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
	
	//save the virtual disk to the real disk
	public static void save2Disk(DiskModel disk,String filename,FATModel fat) {
		File file = new File(filename);
		disk.setFat(fat);
		try{
	           FileOutputStream output=new FileOutputStream(file);
	           ObjectOutputStream oos=new ObjectOutputStream(output);
	           oos.writeObject(disk);
	           oos.flush();
	           oos.close();
	           output.close();
	       }catch (Exception e){
	            e.printStackTrace();
	       }
	}


	//achieve the disk data from the real disk
	public static Object achieve2Disk(String filename) {
		File file=new File(filename);
        try{
            FileInputStream input=new FileInputStream(file);
            ObjectInputStream ois=new ObjectInputStream(input);
            Object object=ois.readObject();
            ois.close();
            input.close();
            return object;
        } catch (java.io.FileNotFoundException e) {
            // do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
      return null;
	}
}
