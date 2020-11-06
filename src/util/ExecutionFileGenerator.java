package util;

import java.util.Random;

public class ExecutionFileGenerator {
    public static String generateInstructions() {
        String result = "";
        Random r = new Random();
        int count = r.nextInt(5) + 5;
        for (int i = 0; i < count; ++i) {
            int choose = r.nextInt(4);
            String ii = "";
            if (choose == 0) {
                ii = "x=" + (r.nextInt(9) + 1);
            } else if (choose == 1) {
                ii = "x++";
            } else if (choose == 2) {
                ii = "x--";
            } else if (choose == 3) {
                ii = "!";
                choose = r.nextInt(3);
                if (choose == 0) {
                    ii = ii + "a";
                } else if (choose == 1) {
                    ii = ii + "b";
                } else if (choose == 2) {
                    ii = ii + "c";
                }
                ii = ii + (r.nextInt(9) + 1);
            }
            result = result + ii;
        }
        return result + "end";
    }

    public static void main(String[] args) throws controller.CompilerException {
        String i = ExecutionFileGenerator.generateInstructions();
        System.out.println(i);
        System.out.println(controller.Compiler.decompile(controller.Compiler.compile(i)));
		for (String ss: util.TypeTransfrom.bytesToBinaryStrings(controller.Compiler.compile(i))) {
			// if (!ss.equals("00000000"))
				System.out.println(ss);
        }
    }
}
