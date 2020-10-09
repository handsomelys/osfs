package filesystem.service;

import filesystem.model.FAT;

public class FATService {
	//query the free address of fat
	//return the index
	public static int addressOfFreeBlock(FAT fat) {
		for(int i=3;i<fat.getFat().length;i++) {
			if(fat.getFat()[i]==0) return i;
		}
		return -1;
	}
	//while the file end, set the fat value = -1
	public static void fileEnd(int index,FAT fat) {
		fat.getFat()[index] = -1;
	}
	//free the FAT
	public static void freeBlock(int index,FAT fat) {
		fat.getFat()[index] = 0;
		fat.setFreeCnt(fat.getFreeCnt()+1);
	}
	//apply the space of block,set the FAT
	public static void applyForBlock(int pre,int index,FAT fat) {
		fat.getFat()[pre] = (byte)index;
		fat.setFreeCnt(fat.getFreeCnt()-1);
	}
	//set the value of block
	public static void SetBlockValue(int value,FAT fat,int index) {
		fat.getFat()[index] = (byte)value;
	}
}
