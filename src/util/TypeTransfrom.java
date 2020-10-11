package util;

public class TypeTransfrom {
    public static byte[] intToByteArray(int n) {
        byte[] result = new byte[4];
        result[0] = (byte) (n & 0xff);
        result[1] = (byte) (n >> 8 & 0xff);
        result[2] = (byte) (n >> 16 & 0xff);
        result[3] = (byte) (n >> 24 & 0xff);
        return result;
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
}
