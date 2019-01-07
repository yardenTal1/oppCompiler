package oop.ex6.variable;

import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oop.ex6.exception.WarningInFile;


public abstract class Variable {
    private String type, varName, refValue;
    private boolean isFinal, isGlobal;
    private int isInitialize;
    private int lineNum;



    public Variable(String type, String varName, boolean isFinal, int lineNum) {
        this.type = type;
        this.varName = varName;
        this.isFinal = isFinal;
        this.lineNum = lineNum;
        this.isInitialize = -1;
    }


    private static final String PATTERN_CASE1 = "^\\s*(\\w+)\\s*";
    private static final String PATTERN_CHECK_ARG = "^\\s*\\S+\\s*$";
    private static final String LETTER_NAME = "^\\s*([a-zA-Z][a-zA-Z_0-9]*)\\s*";
    private static final String UNDERSCORE_NAME = "^\\s*([_][a-zA-Z_0-9]+)\\s*";

    /**
     * The method that is initially checked for declaration variables and calls for a FactoryVariable that will try
     * to create a new variable
     * @param type - the legal type of variable
     * @param var - the name of variable (You may have an assignment, and not necessarily a valid one)
     * @param isFinal - "final" if its a final variable
     * @param lineNum - The declaration line number
     * @return - list of new variable
     * @throws WarningInFile - If the method can not create new variables
     */
    public static List<Variable> handleNewVar (String type, String var, String isFinal, int lineNum) throws WarningInFile{
        boolean finalVar = false;
        if (isFinal!=null && isFinal.replaceAll(" ","").equals("final")) //checking if its a final variable
            finalVar = true;
        List<Variable> newVariableList = new LinkedList<>();
        String[] splitVar = var.split(",", -1);

        for (String arg : splitVar) {
            //case 1 - only one var declared
            Pattern patternVariable = Pattern.compile(PATTERN_CASE1);
            Matcher matcherVar = patternVariable.matcher(arg);
            if (matcherVar.matches()) {
                if (checkVarName(arg)) {
                    newVariableList.add(VariableFactory.getInstance().makeVariable(type, arg, finalVar, lineNum));
                }
                else throw new WarningInFile("Invalid variable name");

            } else if (arg.contains("=")){ //case 2 - if Initialized
                    String[] chars = arg.split("=", -1);
                    if (chars.length != 2) {
                        throw new WarningInFile("Invalid variables");
                    }
                    if (checkVarName(chars[0] )) {
                        patternVariable = Pattern.compile(PATTERN_CHECK_ARG); //Check whether the argument names are valid
                        matcherVar = patternVariable.matcher(chars[1]);
                        if (!matcherVar.matches()) {
                            throw new WarningInFile("trying to assign invalid arg");
                        }
                        newVariableList.add(VariableFactory.getInstance().makeVariable(type, arg, finalVar, lineNum));
                    }
                    else throw new WarningInFile("Invalid variable name");
                }
                else throw new WarningInFile("no case was matched");
        }
            return newVariableList;

        }

    /**
     * A method that checks if the names of the variables are correct
     * @param var - the names var that is checked
     * @return true if the name if valid and false if its not
     */
    static boolean checkVarName(String var){
        Pattern patternValidName = Pattern.compile(LETTER_NAME);
        Matcher m = patternValidName.matcher(var);
        if(m.matches()){
            return true;
        }
        patternValidName = Pattern.compile(UNDERSCORE_NAME);
        m = patternValidName.matcher(var);
        if(m.matches()){
            return true;
        }
        return false;
    }

    /**
     * edit the variable initialization
     * @param lineNum - the initialize line number
     */
    public void setIsInitialize(int lineNum){
        this.isInitialize = lineNum;
    }

    /**
     * Checks whether the variable is initialized or not
     * @return true if the variable is initialized and false if not
     */
    public boolean ifInitialize(){
        if(isInitialize != -1)
            return true;
        return false;
    }

    /**
     * edit the variables global status
     * @param global - true if its global and false if its not
     */
    public void setIsGlobal(boolean global){
        this.isGlobal = global;
    }

    /**
     * Checks whether the variable is a global variable or not
     * @return true if its global and false if its not
     */
    public boolean getIsGlobal(){
        return this.isGlobal;
    }

    /**
     * Checks whether the variable is a final variable or not
     * @return true if its final and false if its not
     */
    public boolean getIsFinal() {
        return isFinal;
    }

    /**
     * Checks the variable type
     * @return the type of the variable
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return the refValue
     */
    public String getRefValue() {
        return refValue;
    }

    /**
     * edit the references value status
     * @param refValue - return the refValue
     */
    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }

    /**
     * @return the variables name
     */
    public String getVarName() {
        return varName;
    }

    /**
     * @return the line number
     */
    public int getLineNum() {
        return lineNum;
    }




}


