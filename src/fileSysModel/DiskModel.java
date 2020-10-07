package fileSysModel;

import java.util.ArrayList;
import java.util.List;

public class DiskModel {
	private FATModel fat;
	private List<Object> disk;
	
	public DiskModel() {
		disk = new ArrayList<>(128);	//initiate capacity of 128
		fat = new FATModel();
		for(int i=1;i<128;i++) {
			disk.add(null);
		}
		FileModel root = new FileModel("root",2);	// root's attr is directory,start index is 2
		disk.add(2,root);	//start begin is 2
	}

	public FATModel getFat() {
		return fat;
	}

	public void setFat(FATModel fat) {
		this.fat = fat;
	}

	public List<Object> getDisk() {
		return disk;
	}

	public void setDisk(List<Object> disk) {
		this.disk = disk;
	}
	
}
