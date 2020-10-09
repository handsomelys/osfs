package filesystem.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileModel {
	//**********************
	//members
	private String name;	//filenames or directory names
	private String type;	//file type
	private int attribute;	//define file or directory	1 is file,2 is directory
	private int startIndex;	//the start index of FAT
	private int size;	//file size
	private boolean isReadOnly = false;
	private boolean isHide = false;
	private FileModel father = null;	//the upper directory of this object
	private String fileContent;
	private List<FileModel> childFiles = new ArrayList<>(); //child files list
	private int subDirNums;
	private FileModel parentFile;
	private byte[] directoryItem = new byte[8];
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
			this.directoryItem[i] = (byte)name.charAt(i);
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		for(int i=0;i<2;i++) {
			this.directoryItem[i+3] = (byte) type.charAt(i);
		}
	}
	public int getAttribute() {
		return attribute;
	}
	public void setAttr(int attr) {
		this.attribute = attr;
		this.directoryItem[5] = (byte) attr;
	}
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
		this.directoryItem[6] = (byte) startIndex;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
		this.directoryItem[7] = (byte) size;
	}
	public FileModel getFather() {
		return father;
	}
	public void setFather(FileModel father) {
		this.father = father;
	}
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	
	public List<FileModel> getSubFiles() {
		return childFiles;
	}

	public void setSubFiles(List<FileModel> childFiles) {
		this.childFiles = childFiles;
	}

	public int getSubDirNums() {
		return subDirNums;
	}

	public void setSubDirNums(int subDirNums) {
		this.subDirNums = subDirNums;
	}

	public FileModel getParentFile() {
		return parentFile;
	}

	public void setParentFile(FileModel parentFile) {
		this.parentFile = parentFile;
	}

	public byte[] getDir_items() {
		return directoryItem;
	}

	public void setDir_items(byte[] dir_items) {
		this.directoryItem = dir_items;
	}

	public boolean isIsReadOnly() {
		return isReadOnly;
	}
	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	public boolean isIsHide() {
		return isHide;
	}
	public void setIsHide(boolean isHide) {
		this.isHide = isHide;
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
