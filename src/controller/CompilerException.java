package controller                                                             ;

public class CompilerException extends Exception                               {

    private static final long serialVersionUID = 1L                            ;

    public int lineNumber                                                      ;

    public CompilerException()                                                 {
        super()                                                               ;}

    public CompilerException(String message)                                   {
        super(message)                                                        ;}
        
    public CompilerException(int lineNumber, String message)                   {
        super(message)                                                         ;
        this.lineNumber = lineNumber                                          ;}

    public int getLineNumber()                                                 {
        return this.lineNumber                                                ;}

    @Override
    public String getMessage()                                                 {
        return "line "+this.lineNumber+" : "+super.getMessage()              ;}}