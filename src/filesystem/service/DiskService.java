package filesystem.service;

import controller.AttrForFS;
import filesystem.model.Disk;
import filesystem.model.FAT;
import filesystem.model.File;

public class DiskService {
	
	//save directory or file into the disk
	public static void saveFile(File file,Disk disk) {
		disk.getDisk().add(file.getStartIndex(),file);
	}
	
	//achieve content from disk by index
	public static Object getDiskContent(int index,Disk disk) {
		return disk.getDisk().get(index);
	}
	
	public static void saveContent(Object content,Disk disk,int index) {
		//FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		disk.getDisk().set(index, content);
	}
	
}
