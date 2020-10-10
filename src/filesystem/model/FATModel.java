package filesystem.model;

public class FATModel {
	private int[] table = new int[128];
	private int freeCount = 125;
	
	public FATModel() {
		table[0] = -1;
		table[1] = -1;
		table[2] = -1;	//directory items && root
	}

	public int getFreeCount() {
		return freeCount;
	}

	public void setFreeCnt(int freeCount) {
		this.freeCount = freeCount;
	}

	public int[] getFat_table() {
		return table;
	}

	public void setFat_table(int[] table) {
		this.table = table;
	}
}
