package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.service.DiskService;
import filesystem.service.FileService;
import util.ExecutionFileGenerator;

public class AttrForFS {
	private static DiskModel disk;
	private static List<Object> currentFiles;
	private static List<Object> currentDirs;
	private static List<Object> currentFilesAndDirs;
	public static FATModel fat;
	
	public static List<FileModel> exeFiles = new ArrayList<>();
	
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

		System.out.println(d);
		System.out.println(getRoot().getSubFiles());
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

		try {
			FileService.createDirectory(AttrForFS.getRoot(), "exe");
			FileModel exe = FileService.getFileTraversal("exe");
			FileService.createDirectory(exe, "cat");
			FileModel cat = FileService.getFileTraversal(exe, "cat");
			FileService.createDirectory(exe, "dog");
			FileModel dog = FileService.getFileTraversal(exe, "dog");
			// FileService.createFile(AttrForFS.getRoot(), "ff");
			// FileService.editFileContent(FileService.getFileTraversal(AttrForFS.getRoot(), "ff"), ExecutionFileGenerator.generateInstructions());

			FileService.createFile(cat, "c1.e");
			FileModel c1 = FileService.getFileTraversal(cat, "c1.e");
			FileService.editFileContent(c1, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			c1.setReadOnly(true);
			AttrForFS.exeFiles.add(c1);

			FileService.createFile(cat, "c2.e");
			FileModel c2 = FileService.getFileTraversal(cat, "c2.e");
			FileService.editFileContent(c2, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			c2.setReadOnly(true);
			AttrForFS.exeFiles.add(c2);

			FileService.createFile(cat, "c3.e");
			FileModel c3 = FileService.getFileTraversal(cat, "c3.e");
			FileService.editFileContent(c3, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			c3.setReadOnly(true);
			AttrForFS.exeFiles.add(c3);

			FileService.createFile(cat, "c4.e");
			FileModel c4 = FileService.getFileTraversal(cat, "c4.e");
			FileService.editFileContent(c4, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			c4.setReadOnly(true);
			AttrForFS.exeFiles.add(c4);

			FileService.createFile(cat, "c5.e");
			FileModel c5 = FileService.getFileTraversal(cat, "c5.e");
			FileService.editFileContent(c5, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			c5.setReadOnly(true);
			AttrForFS.exeFiles.add(c5);
			
			FileService.createFile(dog, "d1.e");
			FileModel d1 = FileService.getFileTraversal(dog, "d1.e");
			FileService.editFileContent(d1, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			d1.setReadOnly(true);
			AttrForFS.exeFiles.add(d1);
			
			FileService.createFile(dog, "d2.e");
			FileModel d2 = FileService.getFileTraversal(dog, "d2.e");
			FileService.editFileContent(d2, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			d2.setReadOnly(true);
			AttrForFS.exeFiles.add(d2);
			
			FileService.createFile(dog, "d3.e");
			FileModel d3 = FileService.getFileTraversal(dog, "d3.e");
			FileService.editFileContent(d3, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			d3.setReadOnly(true);
			AttrForFS.exeFiles.add(d3);
			
			FileService.createFile(dog, "d4.e");
			FileModel d4 = FileService.getFileTraversal(dog, "d4.e");
			FileService.editFileContent(d4, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			d4.setReadOnly(true);
			AttrForFS.exeFiles.add(d4);
			
			FileService.createFile(dog, "d5.e");
			FileModel d5 = FileService.getFileTraversal(dog, "d5.e");
			FileService.editFileContent(d5, new String(Compiler.compile(ExecutionFileGenerator.generateInstructions())));
			d5.setReadOnly(true);
			AttrForFS.exeFiles.add(d5);
			System.out.println(d);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CompilerException e) {
			e.printStackTrace();
		}
	}

	public static String getExeFiles() {
		try {
			Random r = new Random();
			return controller.Compiler.decompile(FileService.getFileContent(AttrForFS.exeFiles.get(r.nextInt(AttrForFS.exeFiles.size()))).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return ExecutionFileGenerator.generateInstructions();
		}
	}
}
