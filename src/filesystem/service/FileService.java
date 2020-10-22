package filesystem.service;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import controller.AttrForFS;
import filesystem.model.DiskModel;
import filesystem.model.FATModel;
import filesystem.model.FileModel;

public class FileService {
	static Scanner sc = new Scanner(System.in);

	public static String getNewName(FileModel parentFile, int fileAttribute) {
		String prefix;
		if (fileAttribute == 1) {
			prefix = "nf";
		} else if (fileAttribute == 2 || fileAttribute == 3) {
			prefix = "nd";
		} else {
			prefix = "??";
		}
		int index = 0;
		boolean flag = false;
		List<Object> subFiles = FileService.getSubFiles(parentFile);
		for (; index < 10 && flag == false; ++index) {	// there is only 8 files for maximum in a directory, i think this is ok
			flag = true;
			for (Object f: subFiles) {
				if (f instanceof FileModel) {
					FileModel ff = (FileModel) f;
					if (ff.getName().equals(prefix+index)) {
						flag = false;
						break;
					}
				}
			}
		}
		return prefix+(index-1);
	}
	
	//create file in the disk, need to point out the parent file
	public static boolean createFile(FileModel file,FileModel parentFile,String filename) {
		System.out.println("parents sub nums: "+file.getParentFile().getSubFiles().size());
		if(!addSubFileValid(file.getParentFile())) {
			
			System.out.println("at most 8 files in one directory!!");
			return false;
		}
		System.out.println("input the file name");

		
		if(filename.length()>3) {
			System.out.println("the file name should not over 3 chars");
			return false;
		}
		file.setParentFile(parentFile);
		file.setName(filename);
		if(addFile(file,AttrForFS.getDisk(),AttrForFS.getFat())) {
			updateDirectorySub(file.getParentFile(),file);
			AttrForFS.getCurrentFilesAndDirs().add(file);
			if(file.getAttribute()==FileModel.FILE) {
				AttrForFS.getCurrentFiles().add(file);
			}
			else {
				AttrForFS.getCurrentDirs().add(file);
			}
			return true;
		}
		
		return false;
	}
	public static boolean createFile(FileModel parentFile, int fileAttribute) {
		return FileService.createFile(parentFile, fileAttribute, FileService.getNewName(parentFile, fileAttribute), 'x');
	}

	public static boolean createFile(FileModel parentFile, int fileAttribute, String fileName, char fileType) {
		if(parentFile.getSubFiles().size()>=8) {
			System.out.println("at most 8 files in one directory!!");
			return false;
		}
		if (fileName.length() > 3) {
			System.out.println("wjmcdbndy3");
		}
		FileModel file = new FileModel();
		file.setAttribute(fileAttribute);
		file.setName(fileName);
		if (fileAttribute == 1 && fileType != 0) {
			file.setType(fileType);
		}
		file.setParentFile(parentFile);
		file.setReadOnly(false);
		if(addFile(file,AttrForFS.getDisk(),AttrForFS.getFat())) {
			updateDirectorySub(parentFile,file);
			AttrForFS.getCurrentFilesAndDirs().add(file);
			if(file.getAttribute()==FileModel.FILE) {
				AttrForFS.getCurrentFiles().add(file);
			}
			else {
				AttrForFS.getCurrentDirs().add(file);
			}
			return true;
		}
		return false;
	}

	public static boolean createFileWithExtension(FileModel parentFile, String rawFileName) throws IOException {
		int dot = rawFileName.lastIndexOf(".");
		if (dot == -1) {
			if (rawFileName.length()>3) {
				throw new IOException(rawFileName+": invalid file name (too long)");
			} else {
				return FileService.createFile(parentFile, 1, rawFileName, ' ');
			}
		} else {
			if (dot == rawFileName.length()-2) {
				String realFileName = rawFileName.substring(0, dot);
				char extension = rawFileName.charAt(rawFileName.length()-1);
				if (realFileName.length()>3) {
					throw new IOException(rawFileName+": invalid file name (too long)");
				} else {
					return FileService.createFile(parentFile, 1, realFileName, extension);
				}
			} else {
				throw new IOException(rawFileName+": invalid file name (extension too long)");
			}
		}
	}	
	
	public static boolean addFile(FileModel file,DiskModel disk,FATModel fat) {
		int start_index = filesystem.service.FATService.addressOfFreeBlock(fat);
		if(start_index == -1) {
			System.out.println("Disk is full!!");
			return false;
		}
		else {
			file.setStartIndex(start_index);
			filesystem.service.FATService.applyForBlock(start_index, 255, fat);
			file.setSize(1);
			if(file==null||file.getName().trim().equals("")) {
				System.out.println("The names'length can not be blank");
				return false;
			}

			else if(checkDuplicationOfName(file)) {
				
				System.out.println("Duplication of name!!");
				return false;
			}
			else {
				DiskService.saveFile(file, disk);
			}
		}
		return true;
	}
	public static FileModel getFileTraversal(String path) throws IOException {
		return FileService.getFileTraversal(AttrForFS.getRoot(), path);
	}

	public static FileModel getFileTraversal(FileModel start, String path) throws IOException {
		FileModel result = start;
		String[] way = path.split("/");
		boolean all = true;
		int index = 0;
		for (; index < way.length; ++index) {
			boolean found = false;
			FileModel next = null;
			for (Object o: FileService.getSubFiles(result)) {
				next = (FileModel) o;
				if (next.getName().equals(way[index])) {
					found = true;
					break;
				}
			}
			if (found) {
				result = next;
			} else {
				all = false;
				break;
			}
		}
		if (all) {
			return result;
		} else {
			String error = "";
			for (int i = 0; i <= index; ++i) {
				error = error+way[i]+"/";
			}
			throw new IOException(error+": path not exist");
		}
	}

	public static FileModel getFile(FileModel parentFile, String name) {
		for (Object o: FileService.getSubFiles(parentFile)) {
			FileModel result = (FileModel) o;
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}
	
	//remove the file from disk
	public static void removeFile(FileModel file) {
		removeFileContent(file);
		AttrForFS.getCurrentFiles().remove(file);
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());
	}
	
	public static void removeDir(FileModel directory) {
		if(FileService.getSubFiles(directory).size()!=0) {
			System.out.println("The directory is not empty!");
		}
		else {
			removeFile(directory);
		}
	}
	
	public static void removeFileContent(FileModel file) {
		
			int[] fat = AttrForFS.getFat().getTable();
			int start_index = file.getStartIndex();
			while(fat[start_index]!=255&&fat[start_index]!=-1) {
				AttrForFS.getDisk().getDiskTable().set(start_index, null);	//remove the file block
				int tmp = fat[start_index];
				filesystem.service.FATService.freeBlock(start_index, AttrForFS.getFat());
				start_index = tmp;
				int size = file.getSize() - 1;	//file length -1
				file.setSize(size);
			}if(file.getStartIndex()!=start_index) {
				filesystem.service.FATService.SetBlockValue(0, AttrForFS.getFat(), start_index);	//set the former block value to 0, stands for vacant
			}
				filesystem.service.FATService.SetBlockValue(255, AttrForFS.getFat(), file.getStartIndex());	//set the current block value to 255, stands for the file end

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
	
	//edit the file's content
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
			filesystem.service.FATService.applyForBlock(pre, index, AttrForFS.getFat());
			buffer = new char[110];
			file.setSize(file.getSize()+1);
		}
		filesystem.service.FATService.SetBlockValue(255, AttrForFS.getFat(), index);
	}
	
	// check if there has the duplicated name in the same directory,if true, reject to create
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
	
	//get the specific directory's sub files
	public static List<Object> getSubFiles(FileModel parentFile) {
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
	
	//update the directory's sub files
	public static boolean updateDirectorySub(FileModel directory,FileModel sub) {
		if(addSubFileValid(directory)) {
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
	
	public static boolean copyFile(FileModel file,FileModel parentFile,String filename) {
		try {
			FileModel coloneFile = (FileModel)file.clone();
			if(createFile(coloneFile,parentFile,filename)) {
				System.out.println("clone sussess!");
				return true;
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
/*	public static boolean copydir(FileModel directory) throws CloneNotSupportedException {
		FileModel coloneDir = (FileModel)directory.clone();
		if(createFile(coloneDir)) {
			for(FileModel f:coloneDir.getSubFiles()) {
			if(createFile((FileModel)f.clone())) {
				System.out.println("clone sucess!");
			}
			for(FileModel f:coloneDir.getSubFiles()) {
				if(f.getAttribute()==FileModel.DIRECTORY) {
					if(!copydir((FileModel)f))	return false;
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return true;
	}*/

	public static boolean addSubFileValid(FileModel parentFile){
		if(getSubFiles(parentFile).size()>=FileModel.MAX_SUB_NUMS){
			System.out.println("It's subs nums over 8!");
			return false;
		}
		return true;
	}

	public static boolean validInputName(String fileName){
		if(fileName==null||fileName.equals("")){
			System.out.println("null error");
			return false;
		}
		else if(fileName.toCharArray().length>3){
			System.out.println("name over size error");
			return false;
		}
		else if(fileName.contains("$")||fileName.contains("/")||fileName.contains(".")){
			System.out.println("invalid chars error");
			return false;
		}
		return true;
	}
	public static void main(String[] args) throws CloneNotSupportedException {

		AttrForFS.init();
		createFile((FileModel)AttrForFS.getDisk().getDiskTable().get(2),1,"abc",'c');
		System.out.println(AttrForFS.getCurrentFiles());
//		System.out.println(AttrForFS.getFat().getTable());
		copyFile((FileModel)AttrForFS.getDisk().getDiskTable().get(3),(FileModel)AttrForFS.getDisk().getDiskTable().get(2),"ab");
		for(int i=0;i<AttrForFS.getFat().getTable().length;i++){
			System.out.println(AttrForFS.getFat().getTable()[i]);
		}
		System.out.println(AttrForFS.getCurrentFiles());
		editFileContent((FileModel)AttrForFS.getDisk().getDiskTable().get(3),"hello,world");
		for(int i=0;i<AttrForFS.getFat().getTable().length;i++){
			System.out.println(AttrForFS.getFat().getTable()[i]);
		}
		System.out.println((String) AttrForFS.getDisk().getDiskTable().get(5));
	}
}
