package filesystem.model;

import java.util.ArrayList;
import java.util.List;

public class DiskModel {
	private FATModel fat;
	private List<Object> diskTable;
	
	public DiskModel() {
		diskTable = new ArrayList<>(128);	//initiate capacity of 128
		fat = new FATModel();
		for(int i=1;i<128;i++) {
			diskTable.add(null);
		}
		FileModel root = new FileModel("root",2);	// root's attr is directory,start index is 2
		diskTable.add(2,root);	//start begin is 2
	}

	public FATModel getFat_table() {
		return fat;
	}

	public void setFat_table(FATModel fat_table) {
		this.fat = fat_table;
	}

	public List<Object> getDisk_table() {
		return diskTable;
	}

	public void setDisk_table(List<Object> disk_table) {
		this.diskTable = disk_table;
	}

	
	
}
