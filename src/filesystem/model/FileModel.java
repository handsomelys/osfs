package filesystem.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TypeTransfrom;

public class FileModel {
	//**********************
	//members
	public static final int FILE = 1;
	public static final int DIRECTORY = 2;
	public static final int ROOT = 3;
	private String name;	//filenames or directory names
	private char type;	//file type
	private int attribute;	//define file or directory	1 is file,2 is directory,3 is root
	private int startIndex;	//the start index of FAT
	private int size;	//file size
	private boolean isReadOnly = false;
	private boolean isHide = false;
	private boolean isOpen = false;
	private FileModel father = null;	//the upper directory of this object
	private String fileContent;
	private List<FileModel> subFiles = new ArrayList<>(); //sub files list
	private int subDirNums;
	private FileModel parentFile;
	//[0]~[2] filenames
	//[3] file type
	//[4] file attribute
	//[5] start disk index
	//[6]~[7] length of file
	
	public Map<String,FileModel> subMap = new HashMap<String,FileModel>();
	//**********************
	
	//**********************
	//Construct methods
	public FileModel() {
		;
	}
	public FileModel(String name,char type,int startIndex,int size) {
		this.setName(name);
		this.setType(type);
		this.setAttribute(1);
		this.setStartIndex(startIndex);
		this.setSize(size);
	}
	
	public FileModel(String name,int startIndex) {
		this.setName(name);
		this.setAttribute(2);
		this.setStartIndex(startIndex);
		this.setType(' ');
		this.setSize(1);
	}
	//**********************

	//**********************
	//methods
	
	public byte[] itemToBytes() {
		byte[] item = new byte[8];
		System.arraycopy(this.name.getBytes(), 0, item, 0, 3);	// [0]~[2] file name
		item[3] = (byte) this.type;	// [3] file type
		item[4] = (byte) this.attribute;	// [4] file attribute
		item[5] = (byte) this.startIndex;	// [5] start disk index
		System.arraycopy(TypeTransfrom.intToByteArray(this.size), 0, item, 6, 2);	// [6]~[7] length of file
		return item;
	}
	public byte[] contentToByte() {
		byte[] content = new byte[DiskModel.BLOCK_SIZE];
		if (this.fileContent != null) {
			byte[] b = this.fileContent.getBytes();
			System.arraycopy(b, 0, content, 0, b.length);
		}
		return content;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		
		FileModel file = (FileModel) obj;
		
		if (attribute != file.attribute) return false;
        if (startIndex != file.startIndex) return false;
        if (size != file.size) return false;
        if (subDirNums != file.subDirNums) return false;
        if (isOpen != file.isOpen) return false;
        if (isReadOnly != file.isReadOnly) return false;
        if (name != null ? !name.equals(file.name) : file.name != null) return false;
        if (subMap != null ? !subMap.equals(file.subMap) : file.subMap != null) return false;
        return file != null ? file.equals(file.parentFile) : file.parentFile == null;
	}
	
	 @Override
	    public int hashCode() {
	        int result = name != null ? name.hashCode() : 0;
	        result = 31 * result + attribute;
	        result = 31 * result + startIndex;
	        result = 31 * result + size;
	        result = 31 * result + subDirNums;
	        result = 31 * result + (isOpen ? 1 : 0);
	        result = 31 * result + (subMap != null ? subMap.hashCode() : 0);
	        result = 31 * result + (parentFile != null ? parentFile.hashCode() : 0);
	        result = 31 * result + (isReadOnly ? 1 : 0);
	        return result;
	    }
	 
	 @Override
	    public String toString() {
	        return "FileModel{" +
	                "name='" + this.name + '\'' +
	                ", attribute=" + this.attribute +
	                ", startIndex=" + this.startIndex +
	                ", size=" + this.size +
	                ", subDirNums=" + this.subDirNums +
	                ", open=" + this.isOpen +
	                ", subFiles=" + this.subFiles +
	                ", parentFile=" + this.parentFile +
	                ", ReadOnly=" + this.isReadOnly +
	                '}';
	    }
	
	//**********************

	//**********************
	//setters && getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
		return parentFile;
	}

	public void setParentFile(FileModel parentFile) {
		this.parentFile = parentFile;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean isHide() {
		return isHide;
	}

	public void setHide(boolean isHide) {
		this.isHide = isHide;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Map<String, FileModel> getSubMap() {
		return subMap;
	}

	public void setSubMap(Map<String, FileModel> subMap) {
		this.subMap = subMap;
	}
	//**********************
}
