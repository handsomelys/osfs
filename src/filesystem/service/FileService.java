package filesystem.service;

import controller.AttrForFS;
import filesystem.model.FileModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		// parentFile.getSubFiles().add(file);
		// configure the disk and fat
		int start_index = FATService.addressOfFreeBlock(AttrForFS.getFat());
		if (start_index == -1) {
			throw new IOException("Disk is full.");
		} else {

			if (checkDuplicationOfName(file)) {
				throw new IOException("Duplication of name.");
			} else {
				file.setStartIndex(start_index);
				FATService.applyForBlock(start_index, -1, AttrForFS.getFat());
				// System.out.println("start_index" + start_index);
				file.setSize(1);
			}
		}

		// update current files
		updateDirectorySub(parentFile, file);
		AttrForFS.getCurrentFilesAndDirs().add(file);
		if (file.getAttribute() == FileModel.FILE) {
			AttrForFS.getCurrentFiles().add(file);
			if (file.getType() == FileModel.EXE) {
				AttrForFS.exeFiles.add(file);
			}
		} else {
			AttrForFS.getCurrentDirs().add(file);
			DiskService.saveContent(file, AttrForFS.getDisk(),start_index);
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


	/**
	 * get a new name for new file create according to the file existed.
	 * 
	 * @param parentFile    the directory where you like to store the new file
	 * @param fileAttribute new file or new directory?
	 * @return a unique name
	 */
	public static String getNewName(FileModel parentFile, int fileAttribute) {
		String prefix;
		if (fileAttribute == FileModel.FILE) {	//如果文件属性是普通文件
			prefix = "nf";
		} else if (fileAttribute == FileModel.DIRECTORY || fileAttribute == FileModel.ROOT) {//如果文件属性是目录
			prefix = "nd";
		} else {	//其他
			prefix = "??";
		}
		int index = 0;	//为每个新建的文件后缀加一个index，以防重名
		boolean flag = false;
		List<Object> subFiles = FileService.getSubFiles(parentFile);
		for (; index < 10 && flag == false; ++index) { 
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
				if (next.getNormalName().equals(way[index])) {
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
		removeFileContent(file);	//清空当前file的内容
		AttrForFS.getCurrentFiles().remove(file);	//更新AttrForFS相关数据结构的内容
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		if (file.getAttribute() == FileModel.DIRECTORY) {
			AttrForFS.getCurrentDirs().remove(file);
		}
		if (file.getType() == FileModel.EXE) {
			AttrForFS.exeFiles.remove(file);
		}
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());	//将磁盘中文件对应的位置设为null
		file.getParentFile().getSubFiles().remove(file);	//将文件从父目录的目录项中除去
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
			int start_index = file.getStartIndex();	//获取文件下标
						
			while (fat[start_index] != 255 && fat[start_index] != -1) {	//循环 直至遇到FAT[当前下标]为-1，代表文件结束
				AttrForFS.getDisk().getDiskTable().set(fat[start_index], null); 	//清空磁盘中当前下标的内容
				int tmp = fat[start_index];
				FATService.freeBlock(start_index, AttrForFS.getFat());	//将FAT表中当前下标的值设为0，表示空闲
				start_index = tmp;
				int size = file.getSize() - 1; 
				file.setSize(size);	//更新文件的大小
			}

			if (file.getStartIndex() != start_index) {
				FATService.SetBlockValue(0, AttrForFS.getFat(), start_index);
			}
			AttrForFS.getDisk().getDiskTable().set(file.getStartIndex(), null);
			FATService.SetBlockValue(-1, AttrForFS.getFat(), file.getStartIndex());	//将文件目前的起始下标的FAT表的值设为-1
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFileContent(FileModel file) throws IOException {
		if (file.getAttribute() == FileModel.DIRECTORY) {
			throw new IOException(file.getName() + ": directory is not allowed to get content");
		}
		int start_index = file.getStartIndex();	//获得文本内容
		String result = "";
		int[] fatTable = AttrForFS.getFat().getTable();

		while (fatTable[start_index] != 255 && fatTable[start_index] != -1) { // 遇到fatTable[start_index] == -1，代表文件结束

			result = result.concat(AttrForFS.getDisk().getDiskTable().get(start_index).toString());	//将磁盘中文本内容拼接起来

			start_index = fatTable[start_index]; // point to the next index
		}
		if (fatTable[start_index] == 255 || fatTable[start_index] == -1) {	//当前的fatTable[start_index] == -1 代表文件到头，判断磁盘当前下标是否为null，不为null说明还有内容，与result拼接
			if (AttrForFS.getDisk().getDiskTable().get(start_index) == null) {
				return null;
			}
			result = result.concat(AttrForFS.getDisk().getDiskTable().get(start_index).toString());
		}
		return result;
	}

	// edit the file's content
	public static void editFileContent(FileModel file, String content) throws IOException {
		if (file.getAttribute() == FileModel.DIRECTORY) {
			throw new IOException(file.getName() + ": Directory can not be edited");
		}
		if (file.isReadOnly()) {
			// System.out.println("read only");
			throw new IOException(file.getName() + ": File is read only");

		} else {

			int requireBlocks = DiskService.calculateNeedBlock(content);
			int remainBlocks = DiskService.getDiskFreeCnt();

			if (requireBlocks > remainBlocks) {
				// System.out.println("Error!Do not have the enough blocks");
				return;
			}
			removeFileContent(file);
			char[] buffer = new char[64];

			char[] txt = (content == null ? null : content.toCharArray());
			int index = file.getStartIndex();
			int cur = 0;

			int pre = index;
			// System.out.println(requireBlocks);

			for (int i = requireBlocks; i > 0; i--) {
				if (i == requireBlocks) {
					pre = file.getStartIndex();
				} else {
					pre = index;
				}
				// System.out.println("pre " + pre);
				// System.out.println("index0 " + index);
				for (int k = 0; k < 10; k++) {
					System.out.println(AttrForFS.getFat().getTable()[k]);
				}
				index = FATService.addressOfFreeBlock(AttrForFS.getFat());

				// System.out.println("index " + index);
				for (int j = 0; j < 64 && cur < txt.length; cur++, j++) {
					buffer[j] = txt[cur];
				}
				DiskService.saveContent(String.valueOf(buffer), AttrForFS.getDisk(), pre);

				FATService.applyForBlock(pre, index, AttrForFS.getFat());
				FATService.SetBlockValue(-1, AttrForFS.getFat(), index);

				if (cur == txt.length) {
					FATService.SetBlockValue(0, AttrForFS.getFat(), index);
				}
				buffer = new char[64];
				// System.out.println(AttrForFS.getDisk().getDiskTable());

			}
			if (requireBlocks == 0) {
				file.setSize(1);
			} else {
				file.setSize(requireBlocks);
			}
			// System.out.println(AttrForFS.getDisk().getDiskTable());
			// System.out.println("pre2 " + pre);
			FATService.SetBlockValue(-1, AttrForFS.getFat(), pre);
//			for (int k = 0; k < AttrForFS.getFat().getTable().length; k++) {
//				System.out.println(AttrForFS.getFat().getTable()[k]);
//			}
//			System.out.println(AttrForFS.getDisk().getDiskTable());
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
			if (file.getName().equals(tmpFile.getName()) && file.getType() == tmpFile.getType()) {
				ifDuplicated = true;
			}
		}
		return ifDuplicated;
	}

	// get the specific directory's sub files
	public static List<Object> getSubFiles(FileModel parentFile) {
		List<Object> subFiles = new ArrayList<>();
		List<Object> allFilesSet = AttrForFS.getCurrentFilesAndDirs();	//在当前AttrForFS记录的所有文件中，若找到file的父目录为输入的参数，即添加到subFiles中
		for (int i = 0; i < allFilesSet.size(); i++) {
			FileModel tmpFile = (FileModel) allFilesSet.get(i);
			if (parentFile.equals(tmpFile.getParentFile())) {
				subFiles.add(tmpFile);
			}
		}
		return subFiles;	//返回输入的目录的子文件集
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
		List<Object> sub = (List<Object>) getSubFiles(directory);	//获得指定directory下所有子文件
		for (int i = 0; i < sub.size(); i++) {
			FileModel currentFile = (FileModel) sub.get(i);
			List<Object> currentSub = (List<Object>) getSubFiles(currentFile);
			if (currentSub.size() > 0) {	//若文件夹不为空 递归调用此方法
				deleteDirectoryContent(currentFile);
			} else {
				removeFile(currentFile);	//删除文件
			}
		}
		return true;	//若子文件都删完了 返回true
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
			FileModel clonedFile = (FileModel) file.clone();	//获得输入的file的一个clone对象
			if (!checkSubFileValid(file.getParentFile())) {	//判断当前输入的file的父目录是否已经超过八个子文件
				throw new IOException(destination.getNormalName() + ": at most 8 files in this directory");
			}
			if (filename.length() > 3) {	//判断名字是否合法
				throw new IOException(filename + ": invalid file name (too long)");
			}
			clonedFile.setParentFile(destination);	//将clone的文件对象设置在和file同一个父目录下
			clonedFile.setName(filename);	//设置文件名字

			int start_index = FATService.addressOfFreeBlock(AttrForFS.getFat());	//判断是否有足够空间，申请空闲盘块
			if (start_index == -1) {
				throw new IOException("Disk is full.");
			} else {

				if (checkDuplicationOfName(clonedFile)) {
					throw new IOException("Duplication of name.");
				} else {
					clonedFile.setStartIndex(start_index);	//为clone后的文件设置各个属性值
					FATService.applyForBlock(start_index, 255, AttrForFS.getFat());	//更新FAT表和磁盘块内容
					DiskService.saveFile(clonedFile, AttrForFS.getDisk());
				}
			}

			updateDirectorySub(destination, clonedFile);	//更新当前文件父目录的子文件集
			AttrForFS.getCurrentFilesAndDirs().add(clonedFile);	//更新AttrForFS的各个数据结构内容
			if (file.getAttribute() == FileModel.FILE) {
				AttrForFS.getCurrentFiles().add(clonedFile);
			} else {
				AttrForFS.getCurrentDirs().add(clonedFile);
			}
			editFileContent(clonedFile, getFileContent(file));	//将文件的文本内容复制到clone后的文件
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	public static void changeTheAttribute(FileModel file, boolean readOnly) {
		file.setReadOnly(readOnly);
	}

	public static boolean checkSubFileValid(FileModel parentFile) {
		return getSubFiles(parentFile).size() < FileModel.MAX_SUB_NUMS;
	}

	public static boolean validInputName(String rawFileName) throws IOException {
		int dot = rawFileName.lastIndexOf(".");
		if (dot == -1) {
			if (rawFileName.length() > 3) {
				throw new IOException(rawFileName + ": invalid file name (too long)");
			}
		} else {
			if (dot == rawFileName.length() - 2) {
				String realFileName = rawFileName.substring(0, dot);
				// char extension = rawFileName.charAt(rawFileName.length() - 1);
				if (realFileName.length() > 3) {
					throw new IOException(rawFileName + ": invalid file name (too long)");
				}
			} else {
				throw new IOException(rawFileName + ": invalid file name (extension too long)");
			}
		}
//		if (fileName == null || fileName.equals("")) {
//			System.out.println("null error");
//			return false;
//		} else if (fileName.toCharArray().length > 3) {
//			System.out.println("name over size error");
//			return false;
//		} else if (fileName.contains("$") || fileName.contains("/") || fileName.contains(".")) {
//			System.out.println("invalid chars error");
//			return false;
//		}
//		return true;
		return true;
	}

	public static FileModel rename(FileModel file, String newName) throws IOException {
		//先复制一个file文件，将newName赋值过去，判断会不会与当前父目录的子文件重名，或者不合法
		FileModel tmp = null;
		try {
			tmp = (FileModel) file.clone();	
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if (file.getType() != FileModel.EXE || file.getAttribute() == FileModel.DIRECTORY) {	//没有后缀拓展名
			tmp.setName(newName);
			if (validInputName(newName)) {	//判断名字是否合法
				if (checkDuplicationOfName(tmp)) {	//判断是否重名
					throw new IOException("Duplication name");
				} else {
					file.setName(newName);	//合法就修改名字
					return file;
				}
			} else {
				throw new IOException("invalid name");
			}
		} else if (file.getType() == FileModel.EXE) {	//可执行文件 有后缀拓展名 另外判断
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
		}
		return null;

	}

	public static String[] getRandomExeFile() {
		int size = AttrForFS.exeFiles.size();
		// System.out.println(size);
		int randNumber = ((new Random()).nextInt(size));
		FileModel f = AttrForFS.exeFiles.get(randNumber);
		// String s = "";
		// try {
		// 	s = FileService.getFileContent(f);
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
		// System.out.println(s+"here");
		// for (String ss: util.TypeTransfrom.bytesToBinaryStrings(s.getBytes())) {
		// 	if (!ss.equals("00000000"))
		// 		System.out.println(ss);
		// }
		return new String[] { f.getName(), util.ExecutionFileGenerator.generateInstructions()+"end" };
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
