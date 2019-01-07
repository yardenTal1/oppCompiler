package oop.ex6.exception;

/**
 *  In case a line in the file is invalid a warning exception will be thrown.
 */
public class WarningInFile extends Exception {
    public WarningInFile(String msg){
        super(msg);
    }
}
