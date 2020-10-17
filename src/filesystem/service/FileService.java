package filesystem.service;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;

public class FileService {
	static Scanner sc = new Scanner(System.in);
	public static boolean createFile(FileModel parentFile,int fileAttribute) {
		System.out.println(parentFile);
		System.out.println("parents sub nums: "+parentFile.getSubFiles().size());
		if(parentFile.getSubFiles().size()>=8) {
			
			System.out.println("at most 8 files in one directory!!");
			return false;
		}
		FileModel file = new FileModel();
		file.setAttribute(fileAttribute);
		System.out.println("input the file name");
		String filename = sc.nextLine();
		
		if(filename.length()>3) {
			System.out.println("the file name should not over 3 chars");
			return false;
		}
		file.setName(filename);
		file.setParentFile(parentFile);
		file.setReadOnly(false);
		if(addFile(file,AttrForFS.getDisk(),AttrForFS.getFat())) {
			updateDirectorySub(parentFile,file);
			return true;
		}
		
		return false;
	}
	
	public static boolean addFile(FileModel file,DiskModel disk,FATModel fat) {
		int start_index = FATService.addressOfFreeBlock(fat);
		if(start_index == -1) {
			System.out.println("Disk is full!!");
			return false;
		}
		else {
			file.setStartIndex(start_index);
			FATService.applyForBlock(start_index, 255, fat);
			file.setSize(1);
			if(file==null||file.getName().trim().equals("")) {
				System.out.println("The names'length can not be blank");
				return false;
			}
			//else if(file.getName().length()>3) {	duplicated method
			//	System.out.println("The names length must smaller than 3");
			//}
			else if(checkDuplicationOfName(file)) {
				
				System.out.println("Duplication of name!!");
				return false;
			}
			else {
				DiskService.saveFile(file, disk);
				AttrForFS.getCurrentFiles().add(file);
				AttrForFS.getCurrentFilesAndDirs().add(file);
				//System.out.println("Create File successed");
			}
		}
		return true;
	}
	
	public static void removeFile(FileModel file) {
		removeFileContent(file);
		AttrForFS.getCurrentFiles().remove(file);
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());
	}
	
	public static void removeFileContent(FileModel file) {
		
			int[] fat = AttrForFS.getFat().getTable();
			int start_index = file.getStartIndex();
			while(fat[start_index]!=255) {
				AttrForFS.getDisk().getDiskTable().set(start_index, null);	//remove the file block
				int tmp = fat[start_index];
				FATService.freeBlock(start_index, AttrForFS.getFat());
				start_index = tmp;
				int size = file.getSize() - 1;	//file length -1
				file.setSize(size);
			}if(file.getStartIndex()!=start_index) {
				FATService.SetBlockValue(0, AttrForFS.getFat(), start_index);	//set the former block value to 0, stands for vacant
			}
			else {
				FATService.SetBlockValue(255, AttrForFS.getFat(), start_index);	//set the current block value to 255, stands for the file end
			}
		}
	
	
	public static String getFileContent(FileModel file) {
		String result = "";
		int start_index = file.getStartIndex();
		int[] fatTable = AttrForFS.getFat().getTable();
		while(fatTable[start_index]!=255){	//until the end of file
			result = result.concat(AttrForFS.getDisk().getDiskTable().get(fatTable[start_index]).toString());
			start_index = fatTable[start_index];	//point to the next index
		}
		return result;
	}
	
	public static void editFileContent(FileModel file,String content) {
		int requireBlocks = DiskService.calculateNeedBlock(content);
		int remainBlocks = DiskService.getDiskFreeCnt();
		if(requireBlocks>remainBlocks) {
			System.out.println("Error!Do not have the enough blocks");
			return ;
		}
		removeFileContent(file);
		char[] buffer = new char[110];
		char[] txt = content.toCharArray();
		int index = file.getStartIndex();
		int cur = 0;
		for(int i=requireBlocks;i>0;i--) {
			int pre = index;
			index = DiskService.applyFreeBlock(AttrForFS.getFat());
			for(int j=0;j<64&&cur<txt.length;cur++,j++) {
				buffer[j] = txt[cur];
			}
			DiskService.saveContent(String.valueOf(buffer), AttrForFS.getDisk(), index);
			FATService.applyForBlock(pre, index, AttrForFS.getFat());
			buffer = new char[110];
			file.setSize(file.getSize()+1);
		}
		FATService.SetBlockValue(255, AttrForFS.getFat(), index);
	}
	
	private static boolean checkDuplicationOfName(FileModel file) {
		boolean ifDuplicated = false;
		FileModel parentFile = file.getParentFile();
		List<Object> subFiles = FileService.getSubFiles(parentFile);
		for(int i=0;i<subFiles.size();i++) {
			FileModel tmpFile = (FileModel) subFiles.get(i);
			if(file.getName().equals(tmpFile.getName())&&file.getAttribute()==tmpFile.getAttribute()) {
				ifDuplicated = true;
			}
		}
		return ifDuplicated;
	}

	public static List<Object> getSubFiles(FileModel parentFile) {
		// TODO Auto-generated method stub
		List<Object> subFiles = new ArrayList<>();
		List<Object> allFilesSet = AttrForFS.getCurrentFilesAndDirs();
		for(int i=0;i<allFilesSet.size();i++) {
			FileModel tmpFile = (FileModel) allFilesSet.get(i);
			if(parentFile.equals(tmpFile.getParentFile())) {
				subFiles.add(tmpFile);
			}
		}
		return subFiles;
	}
	
	public static boolean updateDirectorySub(FileModel directory,FileModel sub) {
		if(directory.getSubFiles().size()<8) {
			directory.getSubFiles().add(sub);
			directory.setSubDirNums(directory.getSubDirNums()+1);
			return true;
		}
		System.out.println("the directory's subfiles number is over 8!!");
		return false;
	}
	
	//delete the sub-directory and files in the input directory, not include the directory itself
	public static boolean deleteDirectoryContent(FileModel directory) {
		List<Object> sub = (List<Object>)getSubFiles(directory);
		for(int i=0;i<sub.size();i++) {
			FileModel currentFile = (FileModel)sub.get(i);
			List<Object> currentSub = (List<Object>) getSubFiles(currentFile);
			if(currentSub.size()>0) {
				deleteDirectoryContent(currentFile);
			}
			else {
				removeFile(currentFile);
			}
		}
		return true;
	}
	
	//delete the directory itself and the sub content
	public static boolean deleteDirectory(FileModel directory) {
		deleteDirectoryContent(directory);
		removeFile(directory);
		return true;
	}
}
