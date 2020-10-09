package fileSysModel;

import java.util.ArrayList;
import java.util.List;

public class DiskModel {
	private FATModel fat_table;
	private List<Object> disk_table;
	
	public DiskModel() {
		disk_table = new ArrayList<>(128);	//initiate capacity of 128
		fat_table = new FATModel();
		for(int i=1;i<128;i++) {
			disk_table.add(null);
		}
		FileModel root = new FileModel("root",2);	// root's attr is directory,start index is 2
		disk_table.add(2,root);	//start begin is 2
	}

	public FATModel getFat_table() {
		return fat_table;
	}

	public void setFat_table(FATModel fat_table) {
		this.fat_table = fat_table;
	}

	public List<Object> getDisk_table() {
		return disk_table;
	}

	public void setDisk_table(List<Object> disk_table) {
		this.disk_table = disk_table;
	}

	
	
}
