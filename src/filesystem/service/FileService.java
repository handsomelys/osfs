package filesystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

import controller.AttrForFS;
import filesystem.model.*;

public class FileService {

	/**
	 * this is the universal method to create something on disk.
	 * <p>
	 * assure all your parameters is correct if you want to use this method
	 * </p>
	 * 
	 * @param parentFile    the parent file to store the newly created thing
	 * @param fileAttribute file or directory
	 * @param fileName      name
	 * @param fileType      type (this is needed only if you are creating a file)
	 * @throws IOException if create failed (see the throw out message)
	 * @see #createFile(FileModel, String) use example
	 */
	public static void create(FileModel parentFile, int fileAttribute, String fileName, char fileType)
			throws IOException {
		if (parentFile.getSubFiles().size() >= 8) {
			throw new IOException(parentFile + ": at most 8 files in this directory");
		}
		// configure the file attributes
		FileModel file = new FileModel();
		file.setAttribute(fileAttribute);
		file.setName(fileName);
		if (fileAttribute == FileModel.FILE) {
			file.setType(fileType);
		} else if (fileAttribute == FileModel.DIRECTORY) {
			file.setType(' ');
		}
		file.setParentFile(parentFile);
		file.setReadOnly(false);

		// configure the disk and fat
		int start_index = FATService.addressOfFreeBlock(AttrForFS.getFat());
		if (start_index == -1) {
			throw new IOException("Disk is full.");
		} else {

			if (checkDuplicationOfName(file)) {
				throw new IOException("Duplication of name.");
			} else {
				file.setStartIndex(start_index);
				FATService.applyForBlock(start_index, 255, AttrForFS.getFat());
				file.setSize(1);
				DiskService.saveFile(file, AttrForFS.getDisk());
			}
		}

		// update current files
		updateDirectorySub(parentFile, file);
		AttrForFS.getCurrentFilesAndDirs().add(file);
		if (file.getAttribute() == FileModel.FILE) {
			AttrForFS.getCurrentFiles().add(file);
			if (file.getType() == FileModel.EXE) {
				AttrForFS.getExeFiles().add(file);
			}
		} else {
			AttrForFS.getCurrentDirs().add(file);
		}
	}

	/**
	 * create a new thing with an auto-generated name.
	 * 
	 * @param parentFile    the parent file to store the newly created file
	 * @param fileAttribute file or directory
	 * @throws IOException if create failed (see the throw out message)
	 */
	public static void createNew(FileModel parentFile, int fileAttribute) throws IOException {
		FileService.create(parentFile, fileAttribute, FileService.getNewName(parentFile, fileAttribute), ' ');
	}

	/**
	 * create a new file with specific name and type (which contain in rawFileName).
	 * 
	 * @param parentFile  the parent file to store the newly created file
	 * @param rawFileName name and type in one string, for example: {@code abc.d}
	 * @throws IOException if create failed (see the throw out message)
	 */
	public static void createFile(FileModel parentFile, String rawFileName) throws IOException {
		int dot = rawFileName.lastIndexOf(".");
		if (dot == -1) {
			if (rawFileName.length() > 3) {
				throw new IOException(rawFileName + ": invalid file name (too long)");
			} else {
				FileService.create(parentFile, FileModel.FILE, rawFileName, ' ');
			}
		} else {
			if (dot == rawFileName.length() - 2) {
				String realFileName = rawFileName.substring(0, dot);
				char extension = rawFileName.charAt(rawFileName.length() - 1);
				if (realFileName.length() > 3) {
					throw new IOException(rawFileName + ": invalid file name (too long)");
				} else {
					FileService.create(parentFile, FileModel.FILE, realFileName, extension);
				}
			} else {
				throw new IOException(rawFileName + ": invalid file name (extension too long)");
			}
		}
	}

	/**
	 * create a new directory with specific name.
	 * 
	 * @param parentFile the parent file to store the newly created directory
	 * @param name       directory name
	 * @throws IOException if create failed (see the throw out message)
	 */
	public static void createDirectory(FileModel parentFile, String name) throws IOException {
		if (name.length() > 3) {
			throw new IOException(name + ": invalid directory name (too long)");
		} else {
			FileService.create(parentFile, FileModel.DIRECTORY, name, ' ');
		}
	}

	// public static void createFile(FileModel file, FileModel parentFile,String
	// filename) throws IOException {
	// System.out.println("parents sub nums:
	// "+file.getParentFile().getSubFiles().size());
	// if (!checkSubFileValid(file.getParentFile())) {
	// throw new IOException(parentFile+": at most 8 files in this directory");
	// }
	// System.out.println("input the file name");

	// if(filename.length()>3) {
	// System.out.println("the file name should not over 3 chars");
	// }
	// file.setParentFile(parentFile);
	// file.setName(filename);
	// if(addFile(file,AttrForFS.getDisk(),AttrForFS.getFat())) {
	// updateDirectorySub(file.getParentFile(),file);
	// AttrForFS.getCurrentFilesAndDirs().add(file);
	// if(file.getAttribute()==FileModel.FILE) {
	// AttrForFS.getCurrentFiles().add(file);
	// }
	// else {
	// AttrForFS.getCurrentDirs().add(file);
	// }
	// }
	// }

	// public static boolean addFile(FileModel file,DiskModel disk,FATModel fat) {
	// int start_index = filesystem.service.FATService.addressOfFreeBlock(fat);
	// if(start_index == -1) {
	// System.out.println("Disk is full!!");
	// return false;
	// }
	// else {
	// file.setStartIndex(start_index);
	// filesystem.service.FATService.applyForBlock(start_index, 255, fat);
	// file.setSize(1);
	// if(file==null||file.getName().trim().equals("")) {
	// System.out.println("The names'length can not be blank");
	// return false;
	// }

	// else if(checkDuplicationOfName(file)) {

	// System.out.println("Duplication of name!!");
	// return false;
	// }
	// else {
	// DiskService.saveFile(file, disk);
	// }
	// }
	// return true;
	// }

	/**
	 * get a new name for new file create according to the file existed.
	 * 
	 * @param parentFile    the directory where you like to store the new file
	 * @param fileAttribute new file or new directory?
	 * @return a unique name
	 */
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
		for (; index < 10 && flag == false; ++index) { // there is only 8 files for maximum in a directory, i think this
														// 10 is ok
			flag = true;
			for (Object f : subFiles) {
				if (f instanceof FileModel) {
					FileModel ff = (FileModel) f;
					if (ff.getName().equals(prefix + index)) {
						flag = false;
						break;
					}
				}
			}
		}
		return prefix + (index - 1);
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
			for (Object o : FileService.getSubFiles(result)) {
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
				error = error + way[i] + "/";
			}
			throw new IOException(error + ": path not exist");
		}
	}

	public static FileModel getFile(FileModel parentFile, String name) {
		for (Object o : FileService.getSubFiles(parentFile)) {
			FileModel result = (FileModel) o;
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	// remove the file from disk
	public static void removeFile(FileModel file) {
		removeFileContent(file);
		AttrForFS.getCurrentFiles().remove(file);
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		if (file.getAttribute() == FileModel.DIRECTORY) {
			AttrForFS.getCurrentDirs().remove(file);
		}
		if (file.getType() == FileModel.EXE) {
			AttrForFS.getExeFiles().remove(file);
		}
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());
	}

	public static void removeDir(FileModel directory) throws IOException {
		if (FileService.getSubFiles(directory).size() != 0) {
			throw new IOException(directory.getName() + ": directory is not empty");
		} else {
			removeFile(directory);
		}
	}

	public static void removeFileContent(FileModel file) {
		try {
			int[] fat = AttrForFS.getFat().getTable();
			int start_index = file.getStartIndex();
			while (fat[start_index] != 255 && fat[start_index] != -1) {
				AttrForFS.getDisk().getDiskTable().set(fat[start_index], null); // remove the file block
				int tmp = fat[start_index];
				filesystem.service.FATService.freeBlock(start_index, AttrForFS.getFat());
				start_index = tmp;
				int size = file.getSize() - 1; // file length -1
				file.setSize(size);
			}
			if (file.getStartIndex() != start_index) {
				filesystem.service.FATService.SetBlockValue(0, AttrForFS.getFat(), start_index); // set the former block
																									// value to 0,
																									// stands for vacant
			}
			filesystem.service.FATService.SetBlockValue(255, AttrForFS.getFat(), file.getStartIndex()); // set the
																										// current block
																										// value to 255,
																										// stands for
																										// the file end

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFileContent(FileModel file) {
		String result = "";
		int start_index = file.getStartIndex();
		int[] fatTable = AttrForFS.getFat().getTable();
		while (fatTable[start_index] != 255 && fatTable[start_index] != -1) { // until the end of file
			result = result.concat(AttrForFS.getDisk().getDiskTable().get(fatTable[start_index]).toString());
			start_index = fatTable[start_index]; // point to the next index
		}
		return result;
	}

	// edit the file's content
	public static void editFileContent(FileModel file, String content) throws IOException {
		if (file.getAttribute() == FileModel.DIRECTORY) {
			throw new IOException(file.getName() + ": Directory can not be edited");
		}
		if (file.isReadOnly()) {
			System.out.println("read only");
			throw new IOException(file.getName() + ": File is read only");

		} else {
			int requireBlocks = DiskService.calculateNeedBlock(content);
			int remainBlocks = DiskService.getDiskFreeCnt();
			removeFileContent(file);
			if (requireBlocks > remainBlocks) {
				System.out.println("Error!Do not have the enough blocks");
				return;
			}

			char[] buffer = new char[64];
			char[] txt = content.toCharArray();
			int index = file.getStartIndex();
			int cur = 0;
			for (int i = requireBlocks; i > 0; i--) {
				int pre = index;
				index = DiskService.applyFreeBlock(AttrForFS.getFat());
				for (int j = 0; j < 64 && cur < txt.length; cur++, j++) {
					buffer[j] = txt[cur];
				}
				DiskService.saveContent(String.valueOf(buffer), AttrForFS.getDisk(), index);
				FATService.applyForBlock(pre, index, AttrForFS.getFat());
				buffer = new char[64];
			}
			file.setSize(requireBlocks);
			FATService.SetBlockValue(255, AttrForFS.getFat(), index);
		}
	}

	// check if there has the duplicated name in the same directory,if true, reject
	// to create
	private static boolean checkDuplicationOfName(FileModel file) {
		boolean ifDuplicated = false;
		FileModel parentFile = file.getParentFile();
		List<Object> subFiles = FileService.getSubFiles(parentFile);
		for (int i = 0; i < subFiles.size(); i++) {
			FileModel tmpFile = (FileModel) subFiles.get(i);
			if (file.getName().equals(tmpFile.getName()) && file.getAttribute() == tmpFile.getAttribute()) {
				ifDuplicated = true;
			}
		}
		return ifDuplicated;
	}

	// get the specific directory's sub files
	public static List<Object> getSubFiles(FileModel parentFile) {
		List<Object> subFiles = new ArrayList<>();
		List<Object> allFilesSet = AttrForFS.getCurrentFilesAndDirs();
		for (int i = 0; i < allFilesSet.size(); i++) {
			FileModel tmpFile = (FileModel) allFilesSet.get(i);
			if (parentFile.equals(tmpFile.getParentFile())) {
				subFiles.add(tmpFile);
			}
		}
		return subFiles;
	}

	// update the directory's sub files
	public static boolean updateDirectorySub(FileModel directory, FileModel sub) {
		if (checkSubFileValid(directory)) {
			directory.getSubFiles().add(sub);
			directory.setSubDirNums(directory.getSubDirNums() + 1);
			return true;
		}
		System.out.println("the directory's subfiles number is over 8!!");
		return false;
	}

	// delete the sub-directory and files in the input directory, not include the
	// directory itself
	public static boolean deleteDirectoryContent(FileModel directory) {
		List<Object> sub = (List<Object>) getSubFiles(directory);
		for (int i = 0; i < sub.size(); i++) {
			FileModel currentFile = (FileModel) sub.get(i);
			List<Object> currentSub = (List<Object>) getSubFiles(currentFile);
			if (currentSub.size() > 0) {
				deleteDirectoryContent(currentFile);
			} else {
				removeFile(currentFile);
			}
		}
		return true;
	}

	// delete the directory itself and the sub content
	public static boolean deleteDirectory(FileModel directory) {
		deleteDirectoryContent(directory);
		removeFile(directory);
		return true;
	}

	public static String getAbsolutePath(FileModel file) {
		if (file.getParentFile() == null) {
			return file.getName();
		} else {
			String tmp = getAbsolutePath(file.getParentFile()) + "/" + file.getName();
			return tmp;
		}
	}

	public static void copyFile(FileModel file, FileModel destination, String filename) throws IOException {
		try {
			FileModel colonedFile = (FileModel) file.clone();
			// createFile(coloneFile, destination, filename);
			if (!checkSubFileValid(file.getParentFile())) {
				throw new IOException(destination + ": at most 8 files in this directory");
			}
			if (filename.length() > 3) {
				throw new IOException(filename + ": invalid file name (too long)");
			}
			colonedFile.setParentFile(destination);
			colonedFile.setName(filename);

			// configure the disk and fat
			int start_index = FATService.addressOfFreeBlock(AttrForFS.getFat());
			if (start_index == -1) {
				throw new IOException("Disk is full.");
			} else {

				if (checkDuplicationOfName(colonedFile)) {
					throw new IOException("Duplication of name.");
				} else {
					colonedFile.setStartIndex(start_index);
					FATService.applyForBlock(start_index, 255, AttrForFS.getFat());
					DiskService.saveFile(colonedFile, AttrForFS.getDisk());
				}
			}
			// update current files
			updateDirectorySub(destination, colonedFile);
			AttrForFS.getCurrentFilesAndDirs().add(colonedFile);
			if (file.getAttribute() == FileModel.FILE) {
				AttrForFS.getCurrentFiles().add(colonedFile);
			} else {
				AttrForFS.getCurrentDirs().add(colonedFile);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public static boolean copydir(FileModel directory) throws
	 * CloneNotSupportedException { FileModel coloneDir =
	 * (FileModel)directory.clone(); if(createFile(coloneDir)) { for(FileModel
	 * f:coloneDir.getSubFiles()) { if(createFile((FileModel)f.clone())) {
	 * System.out.println("clone sucess!"); } for(FileModel
	 * f:coloneDir.getSubFiles()) { if(f.getAttribute()==FileModel.DIRECTORY) {
	 * if(!copydir((FileModel)f)) return false; } } } catch
	 * (CloneNotSupportedException e) { e.printStackTrace(); } return true; }
	 */

	public static void changeTheAttribute(FileModel file, boolean readOnly) {
		file.setReadOnly(readOnly);
	}

	public static boolean checkSubFileValid(FileModel parentFile) {
		return getSubFiles(parentFile).size() < FileModel.MAX_SUB_NUMS;
	}

	public static boolean validInputName(String fileName) {
		if (fileName == null || fileName.equals("")) {
			System.out.println("null error");
			return false;
		} else if (fileName.toCharArray().length > 3) {
			System.out.println("name over size error");
			return false;
		} else if (fileName.contains("$") || fileName.contains("/") || fileName.contains(".")) {
			System.out.println("invalid chars error");
			return false;
		}
		return true;
	}

	public static FileModel rename(FileModel file, String newName) throws IOException {

		FileModel tmp = null;
		try {
			tmp = (FileModel) file.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if (file.getType() != FileModel.EXE || file.getAttribute()==FileModel.DIRECTORY) {
			tmp.setName(newName);
			if (validInputName(newName)) {
				if (checkDuplicationOfName(tmp)) {
					throw new IOException("Duplication name");
				} else {
					file.setName(newName);
					return file;
				}
			} else {
				throw new IOException("invalid name");
			}
		} else if(file.getType() == FileModel.EXE){
			tmp.setName(newName);
			if(validInputName(newName)) {
				if(checkDuplicationOfName(tmp)) {
					throw new IOException("Duplication name");
				} else {
					file.setName(newName);
					return file;
				}
			} else {
				throw new IOException("invalid name");
			}
		}
		return null;

	}

	public static String getRandomExeFiles() {
		int size = AttrForFS.getExeFiles().size();
		int randNumber = ((new Random()).nextInt(size));
		return AttrForFS.getExeFiles().get(randNumber).getFileContent();
	}

	public static void main(String[] args) throws CloneNotSupportedException, IOException {

		AttrForFS.init();
		create((FileModel) AttrForFS.getDisk().getDiskTable().get(2), FileModel.DIRECTORY, "abc", ' ');
// 		for(int i=0;i<AttrForFS.getFat().getTable().length;i++){
// 			System.out.println(AttrForFS.getFat().getTable()[i]);
// 		}
		for (Object f : AttrForFS.getCurrentFilesAndDirs()) {
			System.out.println((FileModel) f);
		}

		create((FileModel) AttrForFS.getDisk().getDiskTable().get(3), FileModel.FILE, "a", ' ');
		for (Object f : AttrForFS.getCurrentFilesAndDirs()) {
			System.out.println((FileModel) f);
		}
		System.out.println(getAbsolutePath((FileModel) AttrForFS.getDisk().getDiskTable().get(4)));
	}

}
