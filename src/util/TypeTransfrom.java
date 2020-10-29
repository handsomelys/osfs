package util;

public class TypeTransfrom {
	//single integer to byte[]
    public static byte[] intToByteArray(int n) {
        byte[] result = new byte[4];
        result[0] = (byte) (n & 0xff);
        result[1] = (byte) (n >> 8 & 0xff);
        result[2] = (byte) (n >> 16 & 0xff);
        result[3] = (byte) (n >> 24 & 0xff);
        return result;
    }
    
    //byte[] to single integer
    public static int byteArrayToInt(byte[] bytes) {
    	int value = 0;
    	for(int i=0;i<4;i++) {
    		int shift = (3-i) * 8;
    		value += (bytes[i] & 0xFF) << shift;
    	}
    	return value;
    }
    
    public static String byteToBinaryString(byte b) {
        String result = Integer.toBinaryString(b & 0xff);
        while (result.length() < 8) {
            result = "0" + result;
        }
        return result;
    }
    
    public static String[] bytesToBinaryStrings(byte[] b) {
        String[] result = new String[b.length];
        for (int i = 0; i < b.length; ++i) {
            result[i] = TypeTransfrom.byteToBinaryString(b[i]);
        }
        return result;
    }
	
	//change string to byte[]
	public static byte[] stringToByte(String s) {
		byte[] bytes = s.getBytes();
		return bytes;
	}
	
	//decode byte[] to string
	public static String byteToString(byte[] bytes) {
		String s = new String(bytes);
		return s;
	}
	
	public static void main(String[] args) {
		String string = "hello,world";
		byte[] bytes = stringToByte(string);
		for(int i=0;i<bytes.length;i++) {
			System.out.println(bytes[i]);
		}
		String s = byteToString(bytes);
		System.out.println(s);
	}
}
