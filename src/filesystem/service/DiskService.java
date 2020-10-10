package filesystem.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;

public class DiskService {
	
	//save directory or file into the disk
	public static void saveFile(FileModel file,DiskModel disk) {
		disk.getDisk_table().add(file.getStartIndex(),file);
	}
	
	//achieve content from disk by index
	public static Object getDiskContent(int index,DiskModel disk) {
		return disk.getDisk_table().get(index);
	}
	
	//save content into the disk
	public static void saveContent(Object content,DiskModel disk,int index) {
		FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		disk.getDisk_table().set(index, content);
	}
	
	//apply free block, return the vacant index of disk, -1 stand for disk full
	public static int applyFreeBlock(FATModel fat) {
		for(int i=3;i<fat.getFat_table().length;i++) {
			if(fat.getFat_table()[i]==0) {
				return i;
			}
		}
		return -1;
	}
	
	//delete object from disk
	public static void deleteObject(DiskModel disk,int index) {
		FATService.freeBlock(index, AttrForFS.getFat());
		disk.getDisk_table().set(index, null);
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
	
	public static HashMap getDirsAndFiles(DiskModel disk) {
		HashMap hashmap = new HashMap();
		List<Object> files = new ArrayList<>();
		List<Object> dirs = new ArrayList<>();
		List<Object> allFiles = new ArrayList<>();
		List<Object> contents = disk.getDisk_table();
		
		for(int i=0;i<128;i++) {
			if(contents.get(i) instanceof FileModel) {
				FileModel file = (FileModel) contents.get(i);
				allFiles.add(file);
				if(file.getAttribute()==1) //file
					files.add(file);
				else {
					dirs.add(file);	//directory
				}
			}
		}
		
		hashmap.put("files", files);
		hashmap.put("dirs", dirs);
		hashmap.put("allFiles", allFiles);
		return hashmap;
	}
	
}
