package filesystem.model;

import java.io.Serializable;

public class FATModel implements Serializable {

	private static final long serialVersionUID = 6953005305964695531L;

	public static final int RESERVED_BLOCK_COUNT = DiskModel.BLOCK_COUNT / DiskModel.BLOCK_SIZE + 1; // directory items
																										// && root



	private int[] table;
	private int freeCount = 125;
	public static final int USED_BLOCK = -1;
	public static final int UNUSED_BLOCK = 0;
	public static final int THE_FILE_END = -1;


	public FATModel() {
		this.table = new int[DiskModel.BLOCK_COUNT];
		this.table[0] = USED_BLOCK;
		this.table[1] = USED_BLOCK;
		this.table[2] = USED_BLOCK;
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
		if(index==0||index==1||index==2) {
			return ;
		}
		this.table[index] = 0;
		++this.freeCount;
	}

	/**
	 * take the space of block,set the FAT
	 * @param pre
	 * @param index
	 */
	public void occupyBlock(int pre, int index) {
		this.table[pre] =  index;
		--this.freeCount;
	}

	/**
	 * set the value of block
	 * @param value
	 * @param index
	 */
	public void SetBlockValue(int value, int index) {
		this.table[index] =  value;
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
