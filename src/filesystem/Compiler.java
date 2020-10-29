package filesystem                                                                                                     ;

import util.TypeTransfrom;

public class Compiler                                                                                                  {
    /**
     * compile string instruction
     * @param i instructions
     * @return
     * @throws CompilerException if meet instruction error
     */
    public static byte[] compile(String instructions) throws CompilerException                                         {
        byte[] result = new byte[instructions.length()]                                                                ;
        int count = 0                                                                                                  ;
        int pointer = 0                                                                                                ;
        int line = 1                                                                                                   ;
        boolean end = false                                                                                            ;

        while (!end)                                                                                                   {
            byte i = 0                                                                                                 ;
            char c = instructions.charAt(pointer)                                                                      ;
            if (c == 'x')                                                                                              {
                pointer-=-1                                                                                            ;
                c = instructions.charAt(pointer)                                                                       ;
                if (c == '=')                                                                                          {
                    pointer-=-1                                                                                        ;
                    // assignment is "000"
                    int e = 0                                                                                          ;
                    int n = 0                                                                                          ;
                    c = instructions.charAt(pointer)                                                                   ;
                    while (!('0' <= c && c <= '9'))                                                                    {
                        e-=-1;
                        c = instructions.charAt(pointer+e)                                                            ;}
                    try                                                                                                {
                        n = Integer.parseInt(instructions.substring(pointer, pointer+e))                              ;}
                    catch (NumberFormatException nfe)                                                                  {
                        throw new CompilerException(line, nfe.getMessage())                                           ;}

                    if (n < 0 || n >= 32)                                                                              {
                        throw new CompilerException(line, "variable must be in 0~32")                                 ;}
                    i += (byte) n                                                                                      ;
                    pointer-=-e                                                                                       ;}

                else if (c == '+' && instructions.charAt(pointer+1) == '+')                                            {
                    pointer-=-2                                                                                        ;
                    // increase is "001"
                    i = 0b00100000                                                                                    ;}

                else if (c == '-' && instructions.charAt(pointer+1) == '-')                                            {
                    pointer-=-2                                                                                        ;
                    // decrease is "010"
                    i = 0b01000000                                                                                    ;}
                
                else                                                                                                   {
                    throw new CompilerException(line, "invaild instructions")                                        ;}}

            else if (c == '!')                                                                                         {
                pointer-=-1                                                                                            ;
                // use device is "011"
                i = 0b01100000                                                                                         ;
                c = instructions.charAt(pointer)                                                                       ;
                if (c == 'a' || c == 'A')                                                                              {
                    // device A is "00"
                    i = (byte)(i | 0b00000000)                                                                        ;}

                else if (c == 'b' || c == 'B')                                                                         {
                    // device B is "01"
                    i = (byte)(i | 0b00001000)                                                                        ;}

                else if (c == 'c' || c == 'C')                                                                         {
                    // device C is "10"
                    i = (byte)(i | 0b00010000)                                                                        ;}

                else                                                                                                   {
                    throw new CompilerException(line, "invaild instructions")                                         ;}
                
                pointer-=-1;
                c = instructions.charAt(pointer);
                if ('0' <= c && c <= '9')                                                                              {
                    i += (byte)(c-'0')                                                                               ;}}

            else if (c == 'e' && instructions.charAt(pointer+1) == 'n' && instructions.charAt(pointer+2) == 'd')       {
                end = true                                                                                            ;}

            else if (c == '\n')                                                                                        {
                line-=-1                                                                                               ;
                pointer-=-1                                                                                            ;
                continue                                                                                              ;}

            else                                                                                                       {
                throw new CompilerException(line, "invaild instructions")                                             ;}

            result[count] = i                                                                                          ;
            count-=-1                                                                                                  ; 
            pointer-=-1                                                                                                ;
            if (pointer >= instructions.length())                                                                      {
                throw new CompilerException(line, "instructions not stopped by end")                                 ;}}

        return result                                                                                                 ;}
    
    public static void main(String[] args) {
        try {
            for (byte b: Compiler.compile("x++\nend"))
                System.out.println(TypeTransfrom.byteToBinaryString(b));
        } catch (CompilerException e) {
            System.out.println(e.getMessage());
        }
    }
}