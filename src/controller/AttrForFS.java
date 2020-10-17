package controller;

import java.util.List;

import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;

public class AttrForFS {
	private static DiskModel disk;
	private static List<Object> currentFiles;
	private static List<Object> currentDirs;
	private static List<Object> currentFilesAndDirs;
	public static FATModel fat;

	public static FileModel getRoot() {
		return (FileModel) DiskService.getDiskContent(FATModel.RESERVED_BLOCK_COUNT-1, AttrForFS.getDisk());
	}

	public static DiskModel getDisk() {
		return disk;
	}

	public static void setDisk(DiskModel disk) {
		AttrForFS.disk = disk;
		setFat(disk.getFat());
	}

	public static List<Object> getCurrentFiles() {
		return currentFiles;
	}

	public static void setCurrentFiles(List<Object> currentFiles) {
		AttrForFS.currentFiles = currentFiles;
	}

	public static List<Object> getCurrentDirs() {
		return currentDirs;
	}

	public static void setCurrentDirs(List<Object> currentDirs) {
		AttrForFS.currentDirs = currentDirs;
	}

	public static FATModel getFat() {
		return fat;
	}

	public static void setFat(FATModel fat) {
		AttrForFS.fat = fat;
	}

	public static List<Object> getCurrentFilesAndDirs() {
		return currentFilesAndDirs;
	}

	public static void setCurrentFilesAndDirs(List<Object> currentFilesAndDirs) {
		AttrForFS.currentFilesAndDirs = currentFilesAndDirs;
	}
	
	
}
