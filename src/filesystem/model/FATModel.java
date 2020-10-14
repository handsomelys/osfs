package filesystem.model;

public class FATModel {
	public static final int RESERVED_BLOCK_COUNT = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE + 1;	// directory items && root
	private int[] table;
	private int freeCount = 125;
	public static final int USED_BLOCK = 255;
	public static final int UNUSED_BLOCK = 0;
	public static final int THE_FILE_END = -1;
	
	public FATModel() {
		this.table = new int[DiskModel.BLOCK_COUNT];
		this.table[0] = USED_BLOCK;
		this.table[1] = USED_BLOCK;
		this.table[2] = USED_BLOCK;

	}

	public byte[] toBytes() {
		byte[] result = new byte[DiskModel.BLOCK_COUNT];
		for (int i = 0; i < DiskModel.BLOCK_COUNT; ++i) {
			result[i] = (byte) table[i];
		}
		return result;
	}

	public int getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(int freeCount) {
		this.freeCount = freeCount;
	}

	public int[] getTable() {
		return table;
	}

	public void setTable(int[] table) {
		this.table = table;
	}
}
