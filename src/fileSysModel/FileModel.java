package fileSysModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileModel {
	//**********************
	//members
	private String name;	//filenames or directory names
	private String type;	//file type
	private int attr;	//define file or directory	1 is file,2 is directory
	private int startIndex;	//the start index of FAT
	private int size;	//file size
	private boolean IsReadOnly = false;
	private boolean IsHide = false;
	private FileModel father = null;	//the upper directory of this object
	private String FileContent;
	private List<FileModel> subFiles = new ArrayList<>(); //sub files list
	private int subDirNums;
	private FileModel ParentFile;
	private byte[] dir_items = new byte[8];
	//[0]~[2] filenames
	//[3]~[4] file type
	//[5] file attr
	//[6] start disk index
	//[7] length of file
	
	public Map<String,FileModel> subMap = new HashMap<String,FileModel>();
	//**********************
	
	//**********************
	//Construct methods
	public FileModel(String name,String type,int startIndex,int size) {
		this.setName(name);
		this.setType(type);
		this.setAttr(1);
		this.setStartIndex(startIndex);
		this.setSize(size);
	}
	
	public FileModel(String name,int startIndex) {
		this.setName(name);
		this.setAttr(2);
		this.setStartIndex(startIndex);
		this.setType("dir");
		this.setSize(1);
	}
	//**********************
	
	//**********************
	//setters && getters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		for(int i=0;i<3;i++) {
			this.dir_items[i] = (byte)name.charAt(i);
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		for(int i=0;i<2;i++) {
			this.dir_items[i+3] = (byte) type.charAt(i);
		}
	}
	public int getAttr() {
		return attr;
	}
	public void setAttr(int attr) {
		this.attr = attr;
		this.dir_items[5] = (byte) attr;
	}
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
		this.dir_items[6] = (byte) startIndex;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
		this.dir_items[7] = (byte) size;
	}
	public FileModel getFather() {
		return father;
	}
	public void setFather(FileModel father) {
		this.father = father;
	}
	public String getFileContent() {
		return FileContent;
	}
	public void setFileContent(String fileContent) {
		FileContent = fileContent;
	}
	
	public List<FileModel> getSubFiles() {
		return subFiles;
	}

	public void setSubFiles(List<FileModel> subFiles) {
		this.subFiles = subFiles;
	}

	public int getSubDirNums() {
		return subDirNums;
	}

	public void setSubDirNums(int subDirNums) {
		this.subDirNums = subDirNums;
	}

	public FileModel getParentFile() {
		return ParentFile;
	}

	public void setParentFile(FileModel parentFile) {
		ParentFile = parentFile;
	}

	public byte[] getDir_items() {
		return dir_items;
	}

	public void setDir_items(byte[] dir_items) {
		this.dir_items = dir_items;
	}

	public boolean isIsReadOnly() {
		return IsReadOnly;
	}
	public void setIsReadOnly(boolean isReadOnly) {
		IsReadOnly = isReadOnly;
	}
	public boolean isIsHide() {
		return IsHide;
	}
	public void setIsHide(boolean isHide) {
		IsHide = isHide;
	}
	public Map<String, FileModel> getSubMap() {
		return subMap;
	}

	public void setSubMap(Map<String, FileModel> subMap) {
		this.subMap = subMap;
	}
	//**********************

	//**********************
	//methods
	@Override
	protected Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	
	//**********************
}
