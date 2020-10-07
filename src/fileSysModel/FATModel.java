package fileSysModel;

public class FATModel {
	private byte[] fat = new byte[128];
	private int freeCnt = 125;
	
	public FATModel() {
		fat[0] = -1;
		fat[1] = -1;
		fat[2] = -1;	//directory items && root
	}
	
	public byte[] getFat() {
		return fat;
	}
	public void setFat(byte[] fat) {
		this.fat = fat;
	}
	public int getFreeCnt() {
		return freeCnt;
	}
	public void setFreeCnt(int freeCnt) {
		this.freeCnt = freeCnt;
	}
	
}
