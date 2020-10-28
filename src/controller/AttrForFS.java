package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

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
	
	private static List<FileModel> exeFiles;
	
	private static List<FileModel> currentOpenFile = new ArrayList<>();

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

	public static List<FileModel> getCurrentOpenFile() {
		return currentOpenFile;
	}

	public static void setCurrentOpenFile(List<FileModel> currentOpenFile) {
		AttrForFS.currentOpenFile = currentOpenFile;
	}
	
	@SuppressWarnings("unchecked")
	public static void init() {
		DiskModel d = (DiskModel) DiskService.achieve2Disk(main.Main.DISK);
		d = DiskService.checkDisk(d);
		AttrForFS.disk = d;
		AttrForFS.fat = d.getFat();
		HashMap<String, Object> hash = DiskService.getDirsAndFiles(disk);
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
		AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));
		AttrForFS.setExeFiles((List<FileModel>)hash.get("exeFiles"));
	}

	@SuppressWarnings("unchecked")
	public static void format() {
		DiskModel d = new DiskModel();
		AttrForFS.disk = d;
		AttrForFS.fat = d.getFat();
		HashMap<String, Object> hash = DiskService.getDirsAndFiles(disk);
		AttrForFS.setCurrentFiles((List<Object>)hash.get("files"));
		AttrForFS.setCurrentDirs((List<Object>)hash.get("dirs"));
		AttrForFS.setCurrentFilesAndDirs((List<Object>)hash.get("allFiles"));
	}

	public static List<FileModel> getExeFiles() {
		return exeFiles;
	}

	public static void setExeFiles(List<FileModel> exeFiles) {
		AttrForFS.exeFiles = exeFiles;
	}
}
