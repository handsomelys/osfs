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
	
	public static void removeFile(FileModel file) {
		removeFileContent(file);
		AttrForFS.getCurrentFiles().remove(file);
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());
	}
	
	public static void removeFileContent(FileModel file) {
		try {
			int[] fat = AttrForFS.getFat().getFat_table();
			int start_index = file.getStartIndex();
			while(fat[start_index]!=-1) {
				AttrForFS.getDisk().getDisk_table().set(start_index, null);
				int tmp = fat[start_index];
				FATService.freeBlock(start_index, AttrForFS.getFat());
				start_index = tmp;
				int size = file.getSize() - 1;
				file.setSize(size);
			}if(file.getStartIndex()!=start_index) {
				FATService.SetBlockValue(0, AttrForFS.getFat(), start_index);
			}
			else {
				FATService.SetBlockValue(-1, AttrForFS.getFat(), start_index);
			}
		}	catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean checkDuplicationOfName(FileModel file) {
		// TODO Auto-generated method stub
		return false;
	}
}
