package main;

import java.util.HashMap;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileModel file = new FileModel("abc",2);
		DiskModel disk = new DiskModel();
		FATModel fat = new FATModel();
		AttrForFS.setDisk(DiskService.checkDisk(disk));
		AttrForFS.setFat(AttrForFS.getDisk().getFat_table());
		HashMap hash = DiskService.getDirsAndFiles(disk);
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
		AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));
		FileService.creatFile(file, disk, fat);
	}

}
