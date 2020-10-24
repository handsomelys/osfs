# OSFile实现接口文档

## 实现的包介绍

### 1、 controller
#### AttrForFS:
+ 本操作系统的各类属性，如Disk、File、Directory都放在该类中调用，内含各个静态的getter&setter

+ ```java
  private static DiskModel disk;
  	private static List<Object> currentFiles;
  	private static List<Object> currentDirs;
  	private static List<Object> currentFilesAndDirs;
  	public static FATModel fat;
  ```
+ 在系统的初始化环节，必须为AttrForFS的成员初始化对象
  

### 2、model
+ 该包中含有三个Model类，分别是DiskModel，FATModel，FileModel，分别是Disk、FAT、File的各个抽象属性集
##### DiskModel：
+ ```java
  public static final int BLOCK_COUNT = 128;
      public static final int BLOCK_SIZE = 64; // bytes
  
      private FATModel fat;
      private List<Object> diskTable;
  ```
  
+ 成员变量 diskTable，表示各个磁盘块。fat，表示文件分配表
  
+ ```java
  public DiskModel() {
          this.diskTable = new ArrayList<>(BLOCK_COUNT); // initiate capacity of 128
          this.fat = new FATModel();
          initDiskModel();
      }
  ```
  
  构造方法如上，初始化时，将2号磁盘块设置为根目录，0、1号磁盘块用于存放FAT表
##### FATModel：
+ 文件分配表的各个抽象属性
  
  ```java
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
  ```
##### FileModel：
+ 存放目录项的各个抽象属性，如下代码所示即为其成员变量

  ```java
  public class FileModel {
  	//**********************
  	//members
  	public static final int FILE = 1;
  	public static final int DIRECTORY = 2;
  	public static final int ROOT = 3;
  	private String name;	//filenames or directory names
  	private char type;	//file type
  	private int attribute;	//define file or directory	1 is file,2 is directory,3 is root
  	private int startIndex;	//the start index of FAT
  	private int size;	//file size
  	private boolean isReadOnly = false;
  	private boolean isHide = false;
  	private boolean isOpen = false;
  	private FileModel father = null;	//the upper directory of this object
  	private String fileContent;
  	private List<FileModel> subFiles = new ArrayList<>(); //sub files list
  	private int subDirNums;
  	private FileModel parentFile;
  	//[0]~[2] filenames
  	//[3] file type
  	//[4] file attribute
  	//[5] start disk index
  	//[6]~[7] length of file
  	
  	public Map<String,FileModel> subMap = new HashMap<String,FileModel>();
  	//**********************
  ```

  




### 3、service
+ service包内放置了对Disk、FAT、File的各个逻辑操作

+ 其中Disk、FAT的逻辑操作对用户是透明的，这里介绍FileService的函数接口

  ```java
  public static boolean createFile(FileModel parentFile,int fileAttribute)
  ```
+ 函数作用：创建文件&文件夹
+ 输入参数：父目录，文件属性
+ 返回值：boolean类型 若创建文件成功，则返回true
  
1. 该方法先判断parentfile是否已经有超过8个子文件，如果是，直接返回false;

2. 再判断需要新建的文件 or 目录的名字是否合法， 不合法返回false

3. 设置文件的起始块、大小等各类属性，更新AttrForFS的属性
4. 更新parentFile的子文件List

	
	
	```java
	public static void editFileContent(FileModel file,String content)
	```
+ 函数作用：编辑文件内容
+ 输入参数：文件对象file，文本对象content（该文本对象可以是ui中输入的textField的内容）

1. 该方法调用DiskService的静态方法，判断需要编辑的内容大小是否超过当前空闲磁盘大小，若当前空闲容量满足需要编辑内容大小，则将该file原本的文本内容用textfield保存下来，再将原本的content删掉（用后文的removeFileContent方法），用新文本代替。

   
   
   ```java
   public static void removeFileContent(FileModel file)
   ```
+ 函数作用：删除指定文件的文本内容，但在磁盘中仍保留有该文件

+ 输入参数：FileModel类型对象 file

  
  
  ```java
  public static void removeFile(FileModel file)
  ```
  
+ 函数作用：从磁盘中删除指定file对象 （调用removeFileContent方法）

+ 输入参数：FileModel类型对象 file

  

  ```java
  public static String getFileContent(FileModel file)
  ```

+ 函数作用：从磁盘中获得指定file的文本内容

+ 输入参数：FileModel类型对象 file

  
  
  ```java
  private static List<Object> getSubFiles(FileModel parentFile)
  ```
  
+ 函数作用: 返回指定目录的子文件List

+ 输入参数: FileModel类型对象 parentFile (目录)

+ 返回值: List<Object> 类型对象的子文件集

  

  ```java
  public static boolean deleteDirectoryContent(FileModel directory)
  ```

+ 函数作用：删除指定目录在磁盘的所有子文件，但不包括该目录本身

+ 输入参数：FileModel类型对象 directory

+ 返回值： 若删除成功，返回true

  ​	

  ```java
  public static boolean deleteDirectory(FileModel directory)
  ```

+ 函数作用：删除指定目录在磁盘的所有子文件，包括该目录本身
+ 输入参数：FileModel类型对象 directory
+ 返回值： 若删除成功，返回true