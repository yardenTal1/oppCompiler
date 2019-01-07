package oop.ex6.references;

import oop.ex6.variable.Variable;
import oop.ex6.exception.WarningInFile;
import oop.ex6.scope.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class of variables that have been defined as a varReferences
 */
public class VariableRef extends Reference {
    private String name, givenArgs;
    private String typeVar;
    private String typeGivenArg;
    boolean isDeclare = false;

    private static final String PATTERN_STRING = "(\\s*\".+\"s*$)";
    private static final String PATTERN_CHAR = "(^\\s*'.'\\s*$)";
    private static final String LETTER_NAME = "^\\s*([a-zA-Z][a-zA-Z_0-9]*)\\s*";
    private static final String UNDERSCORE_NAME = "^\\s*([_][a-zA-Z_0-9]+)\\s*";

    /**
     * constructor that create a new variableRef
     * @param name - the name of variableRef
     * @param givenArgs - the name of given argument (the validator after the "=")
     * @param scope - the validators scope
     * @param lineNum - line number
     * @param isDeclare - true if the variable is declared and false if not
     */
    public VariableRef(String name, String givenArgs, Scope scope, int lineNum, boolean isDeclare) {
        super(scope, lineNum);
        this.givenArgs = givenArgs;
        this.name = name;
        this.isDeclare = isDeclare;
    }

    /**
     * A method that checks whether a variable is defined legally
     * @param referenceVarList - reference variable list
     * @return true if the variable ref is valid and false if its not
     * @throws WarningInFile
     */
    public boolean handleVarRef(List<VariableRef> referenceVarList) throws WarningInFile {
        if (name.replaceAll(" ", "").equals(givenArgs.replaceAll(" ", "")))
            throw new WarningInFile("Invalid assignment");
        for (VariableRef var : referenceVarList) { //global scope
            if (var.scope.getFatherScope() == null) {
                Variable foundVar = varInScope(name, scope, lineNum, isDeclare, false);
                if(foundVar == null){
                    throw  new WarningInFile("invalid var reference");
                }
                typeVar = foundVar.getType();
                Variable refFromFile = null;
                if (checkVarName(givenArgs)) { //check if the variable name is legal
                    refFromFile = varInScope(givenArgs, scope, lineNum, false, true);
                    if (refFromFile == null) {
                        throw new WarningInFile("error in reference");
                    }
                }
                else {
                        checkArgs(typeVar, givenArgs);
                    }
                if(refFromFile!=null){
                    if (!refFromFile.getType().equals(typeVar))
                        throw new WarningInFile("Invalid assignment");
                    return true;
                }
            }
        }
        for (VariableRef var : referenceVarList) { //the other scopes
            if (var.scope.getFatherScope() != null) {
                Variable foundVar = varInScope(name, scope, lineNum, isDeclare, false);
                if(foundVar == null){
                    throw new WarningInFile("invalid var reference");
                }
                typeVar = foundVar.getType();
                Variable refFromFile = null;
                if (checkVarName(givenArgs)){
                    refFromFile = varInScope(givenArgs, scope, lineNum, false, true);
                    if(refFromFile!=null){
                        if (!refFromFile.getType().equals(typeVar))
                            throw new WarningInFile("Invalid assignment");
                        return true;
                    }else{
                        throw new WarningInFile("Error in ref");
                    }
                }
                return checkArgs(typeVar, givenArgs);

            }
        }
        return false; // not supposed to get here
    }

    /**
     * the method checks if the arguments assignment is valid by checking the type
     * @param type - the type that request
     * @param arg - an argument that is checked
     * @return true if a valid assignment has been made and false of not
     * @throws WarningInFile
     */
    public static boolean checkArgs(String type, String arg) throws WarningInFile {
        switch (type) {
            case ("int"):
                try {
                    Integer.parseInt(arg.replaceAll(" ", ""));
                    return true;
                } catch (NumberFormatException e) {
                    throw new WarningInFile("invalid assignment: request int, found another type");
                }
            case ("boolean"):
                try {
                    // long if

                    if ((arg.replaceAll("\\s", "").equals("false")) || (arg.replaceAll("\\s", "").equals("true")))
                        return true;
                    Double.parseDouble(arg.replaceAll(" ", ""));
                    return true;
                } catch (NumberFormatException e) {
                    throw new WarningInFile("invalid assignment: request boolean, found another type");
                }
            case ("double"):
                try {
                    Double.parseDouble(arg.replaceAll(" ", ""));
                    return true;
                } catch (NumberFormatException e) {
                    throw new WarningInFile("invalid assignment: request double, found another type");
                }
            case ("String"):
                try {
                    Pattern patternVariable = Pattern.compile(PATTERN_STRING);
                    if (patternVariable.matcher(arg).matches())
                        return true;
                } catch (NumberFormatException e) {
                    throw new WarningInFile("invalid assignment: request String, found another type");
                }
            case ("char"):
                try {
                    Pattern patternVariable = Pattern.compile(PATTERN_CHAR);
                    if (patternVariable.matcher(arg).matches())
                        return true;
                }catch (NumberFormatException e){
                    throw new WarningInFile("\"invalid assignment: request String, found another type");
                }
        }
        return true;
    }

    /**
     * A method that checked the variable name
     * @param var
     * @return true if its legal name and false if its not
     */
    private boolean checkVarName(String var) {
        Pattern patternValidName = Pattern.compile(LETTER_NAME);
        Matcher m = patternValidName.matcher(var);
        if (m.matches()) {
            return true;
        }
        patternValidName = Pattern.compile(UNDERSCORE_NAME);
        m = patternValidName.matcher(var);
        if (m.matches()) {
            return true;
        }
        return false;
    }
}

