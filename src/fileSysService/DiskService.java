package fileSysService;

import controller.AttrForFS;
import fileSysModel.DiskModel;
import fileSysModel.FATModel;
import fileSysModel.FileModel;

public class DiskService {
	
	//save directory or file into the disk
	public static void saveFile(FileModel file,DiskModel disk) {
		disk.getDisk().add(file.getStartIndex(),file);
	}
	
	//achieve content from disk by index
	public static Object getDiskContent(int index,DiskModel disk) {
		return disk.getDisk().get(index);
	}
	
	//save content into the disk
	public static void saveContent(Object content,DiskModel disk,int index) {
		FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		disk.getDisk().set(index, content);
	}
	
	//apply free block, return the vacant index of disk, -1 stand for disk full
	public static int applyFreeBlock(FATModel fat) {
		for(int i=3;i<fat.getFat().length;i++) {
			if(fat.getFat()[i]==0) {
				return i;
			}
		}
		return -1;
	}
	
	//delete object from disk
	public static void deleteObject(DiskModel disk,int index) {
		FATService.freeBlock(index, AttrForFS.getFat());
		disk.getDisk().set(index, null);
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
		return AttrForFS.getFat().getFreeCnt();
	}
	
	
}
