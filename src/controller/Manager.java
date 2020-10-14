package controller;

import filesystem.model.FATModel;
import filesystem.model.FileModel;
import filesystem.model.DiskModel;

public class Manager {
    /*
    private DiskModel disk;

    public Manager() {
        this.disk = new DiskModel();
    }

    public void createFile(FileModel file) {
		int startIndex = this.disk.getFat().addressOfFreeBlock();
		if (startIndex == -1) {
			System.out.println("Disk is full!!");
		}
		else {
            file.setStartIndex(startIndex);
            this.disk.getFat().occupyBlock(startIndex, 255);
            file.setSize(1);
            if(checkDuplicationOfName(file)) {
				System.out.println("Duplication of name!!");
			}
			else {
				DiskService.saveFile(file, disk);
				AttrForFS.getCurrentFiles().add(file);
				AttrForFS.getCurrentFilesAndDirs().add(file);
				System.out.println("Create File successed");
			}
		}
    }
    
	public static void removeFile(FileModel file) {
		removeFileContent(file);
		AttrForFS.getCurrentFiles().remove(file);
		AttrForFS.getCurrentFilesAndDirs().remove(file);
		DiskService.deleteObject(AttrForFS.getDisk(), file.getStartIndex());
    }
    
	private static boolean checkDuplicationOfName(FileModel file) {
		boolean ifDuplicated = false;
		FileModel parentFile = file.getParentFile();
		List<Object> subFiles = FileService.getSubFiles(parentFile);
		for(int i=0;i<subFiles.size();i++) {
			FileModel tmpFile = (FileModel) subFiles.get(i);
			if(file.getName().equals(tmpFile.getName())&&file.getAttribute()==tmpFile.getAttribute()) {
				ifDuplicated = true;
			}
		}
		return ifDuplicated;
	}

    public DiskModel getDisk() {
        return this.disk;
    }
    public void setDisk(DiskModel disk) {
        this.disk = disk;
    }
    */
}
