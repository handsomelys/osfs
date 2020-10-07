package fileSysService;

import fileSysModel.DiskModel;
import fileSysModel.FileModel;

public class DiskService {
	
	public static void saveFile(FileModel file,DiskModel disk) {
		disk.getDisk().add(file.getStartIndex(),file);
	}
	
}
