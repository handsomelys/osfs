package main;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		
		DiskModel disk = new DiskModel();
		FATModel fat = new FATModel();
		AttrForFS.setDisk(DiskService.checkDisk(disk));
		AttrForFS.setFat(AttrForFS.getDisk().getFat());
		
		HashMap hash = DiskService.getDirsAndFiles(disk);
		// this have unchecked warning
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
		AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));
		FileModel parentFile = (FileModel) AttrForFS.getDisk().getDiskTable().get(2);
		for(int i=0;i<3;i++)
		if(FileService.createFile(parentFile, FileModel.FILE)) {
			System.out.println("yes");
		}
		//String content = sc.nextLine();
		
	}

}
