package filesystem.model;

public class FATModel {
	public static final int RESERVED_BLOCK_COUNT = DiskModel.BLOCK_COUNT/DiskModel.BLOCK_SIZE + 1;	// directory items && root
	private int[] table;
	private int freeCount = DiskModel.BLOCK_COUNT-FATModel.RESERVED_BLOCK_COUNT;
	
	public FATModel() {
		this.table = new int[DiskModel.BLOCK_COUNT];
		for (int i = 0; i < FATModel.RESERVED_BLOCK_COUNT; ++i) {
			this.table[i] = -1;
		}
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
