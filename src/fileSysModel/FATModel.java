package fileSysModel;

public class FATModel {
	private int[] fat_table = new int[128];
	private int freeCnt = 125;
	
	public FATModel() {
		fat_table[0] = -1;
		fat_table[1] = -1;
		fat_table[2] = -1;	//directory items && root
	}

	public int getFreeCnt() {
		return freeCnt;
	}

	public void setFreeCnt(int freeCnt) {
		this.freeCnt = freeCnt;
	}

	public int[] getFat_table() {
		return fat_table;
	}

	public void setFat_table(int[] fat_table) {
		this.fat_table = fat_table;
	}
	

	
}
