package oop.ex6.main;

import oop.ex6.exception.*;

import java.io.IOException;

/**
 * The main class
 */
public class Sjavac {
    public static void main(String[] args) {
        // start reading the file.
        SJavaValidator reader = new SJavaValidator();
        try {
            reader.parsingFile(args[0]);
            reader.cleaningRef();
            System.out.println("0");
        } catch (IOException e)  {
            System.out.println("2");
            System.err.print("Invalid path or number of arguments");
        }
        catch(WarningInFile w){
            System.out.println("1");
        }

    }
}
