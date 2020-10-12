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

	/**
	 * query the free address of fat, return the index
	 * @return
	 */
	public int addressOfFreeBlock() {
		for(int i = FATModel.RESERVED_BLOCK_COUNT; i < this.table.length; i++) {
			if (this.table[i] == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * while the file end, set the fat value = -1
	 * @param index
	 */
	public void fileEnd(int index) {
		this.table[index] = -1;
	}

	/**
	 * free the FAT
	 * @param index
	 */
	public void freeBlock(int index) {
		this.table[index] = 0;
		++this.freeCount;
	}

	/**
	 * take the space of block,set the FAT
	 * @param pre
	 * @param index
	 */
	public void occupyBlock(int pre, int index) {
		this.table[pre] = (byte) index;
		--this.freeCount;
	}

	/**
	 * set the value of block
	 * @param value
	 * @param index
	 */
	public void SetBlockValue(int value, int index) {
		this.table[index] = (byte) value;
	}

	/**
	 * for save to file
	 * @return
	 */
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
