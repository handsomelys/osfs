package fileSysService;

import controller.AttrForFS;
import fileSysModel.DiskModel;
import fileSysModel.FATModel;
import fileSysModel.FileModel;

public class FileService {

	public static void creatFile(FileModel file,DiskModel disk,FATModel fat) {
		int start_index = FATService.addressOfFreeBlock(fat);
		if(start_index == -1) {
			System.out.println("Disk is full!!");
			return ;
		}
		else {
			file.setStartIndex(start_index);
			FATService.applyForBlock(start_index, 255, fat);
			file.setSize(1);
			if(file==null||file.getName().trim().equals("")) {
				System.out.println("The names'length can not be blank");
			}
			else if(file.getName().length()>3) {
				System.out.println("The names length must smaller than 3");
			}
			else if(checkDuplicationOfName(file)) {
				System.out.println("Duplication of name!!");
			}
			else {
				DiskService.saveFile(file, disk);
				AttrForFS.getCurrentFiles().add(file);
				AttrForFS.getCurrentFilesAndDirs().add(file);
				System.out.println("Create File successed");
			}
		}
		
	}

	private static boolean checkDuplicationOfName(FileModel file) {
		// TODO Auto-generated method stub
		return false;
	}
}
