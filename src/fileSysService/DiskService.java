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
	
	public static void saveContent(Object content,DiskModel disk,int index) {
		FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		disk.getDisk().set(index, content);
	}
	
}
