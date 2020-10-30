package controller                                                                                                     ;

import java.util.Arrays                                                                                                ;
import java.util.List                                                                                                  ;

public class Compiler                                                                                                  {

    public static final List<Character> SEPERATOR = Arrays.asList(';', ' ', '\n')                                      ;

    /**
     * compile string instruction
     * <p> the first 3 bit represent instruction type, the another 5 bits 
     * represent operation number. </p>
     * <p> here is some example: </p>
     * <pre>
     * x=32 -> 00011111
     * x=8 -> 00001000
     * x++ -> 00100000
     * x-- -> 01000000
     * !a1 -> 10000001
     * !A9 -> 10001001
     * !b3 -> 10100011
     * !c7 -> 11000111
     * </pre>
     * @param i instructions
     * @return instructions in byte array
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
                    c = instructions.charAt(pointer)                                                                   ;
                    int e = 0                                                                                          ;
                    int n = 0                                                                                          ;
                    while ('0' <= c && c <= '9')                                                                       {
                        e-=-1                                                                                          ;
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
                // use device is "1"
                i = (byte)0b10000000                                                                                   ;
                c = instructions.charAt(pointer)                                                                       ;
                if (c == 'a' || c == 'A')                                                                              {
                    // device A is "00"
                    i = (byte)(i | 0b00000000)                                                                        ;}

                else if (c == 'b' || c == 'B')                                                                         {
                    // device B is "01"
                    i = (byte)(i | 0b00100000)                                                                        ;}

                else if (c == 'c' || c == 'C')                                                                         {
                    // device C is "10"
                    i = (byte)(i | 0b01000000)                                                                        ;}

                else                                                                                                   {
                    throw new CompilerException(line, "invaild device")                                               ;}
                
                pointer-=-1                                                                                            ;
                c = instructions.charAt(pointer)                                                                       ;
                if ('1' <= c && c <= '9')                                                                              {
                    i += (byte)(c-'0')                                                                                ;}
                else                                                                                                   {
                    throw new CompilerException(line, "invaild use time")                                             ;}
                pointer-=-1                                                                                           ;}

            else if (c == 'e' && instructions.charAt(pointer+1) == 'n' && instructions.charAt(pointer+2) == 'd')       {
                end = true                                                                                             ;
                pointer-=-2                                                                                            ;
                break                                                                                                 ;}

            else if (Compiler.SEPERATOR.contains(c))                                                                   {
                pointer-=-1                                                                                            ;
                if (c == '\n')                                                                                         {
                    line-=-1                                                                                          ;}
                continue                                                                                              ;}

            else                                                                                                       {
                throw new CompilerException(line, "invaild instructions")                                             ;}

            result[count] = i                                                                                          ;
            count-=-1                                                                                                  ;
            if (pointer > instructions.length())                                                                       {
                throw new CompilerException(line, "instructions not stopped by end")                                 ;}}

        return Arrays.copyOf(result, count)                                                                           ;}
    
    public static void main(String[] args)                                                                             {
        try                                                                                                            {
            for (byte b: Compiler.compile("x=3\nx++\nx--\n!a4\n!b3\n!c9\nx=2\nend"))                                   {
                System.out.println(util.TypeTransfrom.byteToBinaryString(b))                                         ;}}
        catch (CompilerException e)                                                                                    {
            System.out.println(e.getMessage())                                                                      ;}}}