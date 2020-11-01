package filesystem.service;

import filesystem.model.FATModel;

public class FATService {
	//query the free address of fat
	//return the index
	public static int addressOfFreeBlock(FATModel fat) {
		for(int i = 3; i < fat.getTable().length; i++) {
			if(fat.getTable()[i]==0) return i;
		}
		return -1;
	}
	//while the file end, set the fat value = -1
	public static void fileEnd(int index,FATModel fat) {
		fat.getTable()[index] = FATModel.USED_BLOCK;
	}
	//free the FAT
	public static void freeBlock(int index,FATModel fat) {
		fat.getTable()[index] = 0;
		fat.setFreeCount(fat.getFreeCount()+1);
	}
	//apply the space of block,set the FAT
	public static void applyForBlock(int pre,int index,FATModel fat) {
		fat.getTable()[pre] = index;
		fat.setFreeCount(fat.getFreeCount()-1);
	}
	//set the value of block
	public static void SetBlockValue(int value,FATModel fat,int index) {
		fat.getTable()[index] = (byte)value;
	}
}
