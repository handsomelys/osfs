package filesystem.model;

import java.util.ArrayList;
import java.util.List;

public class Disk {
	private FAT fat;
	private List<Object> disk;
	
	public Disk() {
		disk = new ArrayList<>(128);	//initiate capacity of 128
		fat = new FAT();
		for(int i=1;i<128;i++) {
			disk.add(null);
		}
		File root = new File("root",2);	// root's attr is directory,start index is 2
		disk.add(2,root);	//start begin is 2
	}

	public FAT getFat() {
		return fat;
	}

	public void setFat(FAT fat) {
		this.fat = fat;
	}

	public List<Object> getDisk() {
		return disk;
	}

	public void setDisk(List<Object> disk) {
		this.disk = disk;
	}
	
}
