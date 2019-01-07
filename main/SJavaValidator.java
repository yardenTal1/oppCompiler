package oop.ex6.main;

import oop.ex6.variable.*;
import oop.ex6.exception.WarningInFile;
import oop.ex6.scope.*;
import oop.ex6.references.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validte the files lines.
 */

public class SJavaValidator {
    private GlobalScope globalScope = new GlobalScope();
    private Scope curScope = globalScope;
    private int lineNumber ;
    private List<IfWhileRef> ifWhileRefList = new LinkedList<>();
    private List<MethodRef> methodRefList = new LinkedList<>();
    // regex check if the line ends with ";"
    private final Pattern PATTERN_SEMICOLON = Pattern.compile(";\\s*$");
    // regex check if the line ends with "{"
    private final Pattern PATTERN_CURLY_BRACKETS_OPEN = Pattern.compile("\\{\\s*$");
    // regex check if the line has "}"
    private final Pattern PATTERN_CURLY_BRACKETS_CLOSE = Pattern.compile("^\\s*}\\s*$");
    // regex check for a "declaring a new method" pattern
    // this is a long regex so the regex in two lines.
    private final Pattern PATTERN_VOID = Pattern.compile("^\\s*void\\s+([a-zA-Z][a-zA-Z_0-9]*)\\s*\\((.*)\\)\\s*\\{\\s*$");
    // regex check if the line starts with "if" or "while"
    private final Pattern PATTERN_IF_WHILE = Pattern.compile("^\\s*(if|while)\\s*\\((.*)\\)\\s*\\{\\s*$");
    // regex check if the whole line is spaces
    private final Pattern PATTERN_ALL_SPACES = Pattern.compile("\\s*");
    // regex check if the whole line includes a string and spaces
    private final Pattern PATTERN_ONLY_STRING = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z_0-9]*)\\s*$");
    // regex check if the whole line includes a string with a ";" and spaces
    private final Pattern PATTERN_ONLY_STRING_AND_SEMICOLON = Pattern.compile("^\\s*([a-zA-Z_0-9]+)\\s*;\\s*$");
    // regex check for a "calling method" pattern
    private final Pattern PATTERN_CALLING_A_METHOD = Pattern.compile("^\\s*([a-zA-Z][a-zA-Z_0-9]*)\\s*\\((.*)\\)\\s*;\\s*$");
    // regex check for args in a declaration of an if or a while
    private final Pattern PATTERN_IF_WHILE_ARG = Pattern.compile("^\\s*([a-zA-Z_0-9]+)\\s*");
    // regex check for type - long regex
    private final Pattern PATTERN_VALID_TYPE = Pattern.compile("^\\s*(final\\s+)?(int|double|boolean|String|char)\\s+(.+)");
    // long regex
    private final Pattern FINAL_PROBLEM = Pattern.compile("^\\s*(final\\s+)(int|double|boolean|String|char)\\s+" +
            "([a-zA-Z_][a-zA-Z_0-9]*)\\s*;\\s*");

    /**
     * Reading the file line by line and confirm each line syntax validation, and not compiling validation.
     * @param path for the file.
     * @throws IOException in case the path is invalid.
     * @throws WarningInFile in case the line doesn't have a valid Sjavac pattern.
     */
    public void parsingFile (String path) throws IOException, WarningInFile {
        File file = new File(path);
        java.io.FileReader fileReader = new java.io.FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        // Reading the first line in the file
        String textLine = bufferedReader.readLine();
        Matcher m;
        lineNumber += 1;
        /**
         * handle these 5 line options:
         * 1. the line starts with "\\"
         * 2. the whole line is spaces
         * 3. the line ends with ";" (maybe spaces after it)
         * 4. the line ends with "{" (maybe spaces after it)
         * 5. the line has only "}" (maybe spaces before and after)
         * all the other options are syntax error and an error should be thrown.
         */
        while(textLine != null){

            // handle the first case
            if(textLine.startsWith("//")) {}

            // handle the second case - the whole line is spaces
            else if (PATTERN_ALL_SPACES.matcher(textLine).matches()){}

            // handle the third case - the line ends with ";"
            else if(PATTERN_SEMICOLON.matcher(textLine).find()){
                handleSemicolonLine(textLine);
            }

            // ends with "{" so two cases are available: void or if/while
            else if(PATTERN_CURLY_BRACKETS_OPEN.matcher(textLine).find()){
                // the line has "void"
                m = PATTERN_VOID.matcher(textLine);
                if(m.matches()){
                    handleVoid(m);
                }
                // the line has "if or while"
                m = PATTERN_IF_WHILE.matcher(textLine);
                if(m.matches()) {
                    handleIfWhile(m);
                    Scope tempScope = new IfWhileScope(curScope);
                    curScope.setInnerScopeList(tempScope);
                    curScope = tempScope;
                }
            }

            // the fifth case - the line has "}"
            else if(PATTERN_CURLY_BRACKETS_CLOSE.matcher(textLine).matches()){
                // If this closed bracket isn't the first for the current scope, a syntax error will bw thrown.
                if(curScope.isClosedBracket()){
                    throw new WarningInFile("Two closed brackets for the same scope");
                }
                if (isAMethodScope(curScope)){
                    // in case the scope is a method scope check for a return statement before closing the method.
                    if (curScope.getLestReturnLine()+1 != lineNumber){
                        throw new WarningInFile("There isn't a return statement before closing the method");
                    }
                }
                curScope.setClosedBracket(true);
                curScope = curScope.getFatherScope();
            }

            // none of the option above occurred
            else throw new WarningInFile("none of the five options");

            // continue reading the file
            textLine = bufferedReader.readLine();
            lineNumber += 1;
        }
    }

    /**
     * After reading the file confirm the file compiling validation.
     * @throws WarningInFile in case a line in the isn't compile valid.
     */
    public void cleaningRef() throws WarningInFile{

        // Handle the scope's varRefList in case it's not empty.
        refRecursion(globalScope);

        // Check if all the variable in the if/while ref list are valid
        for(IfWhileRef varToCheck : ifWhileRefList){
            varToCheck.handleIfWhileRef();
        }

        // Check if all the methods in the method ref list are valid
        for(MethodRef methodToCheck : methodRefList){
            methodToCheck.isMethodValid(globalScope);
        }
    }

    /**
     * Cleaning each scope varRefList.
     * @param scope the current scope. In the first call gets the global scope.
     * @throws WarningInFile In case some reference is invalid.
     */
    public void refRecursion(Scope scope) throws WarningInFile{

        // clear a scope ref list
        if(!scope.getVarRefList().isEmpty()) {
            scope.getVarRefList().get(0).handleVarRef(scope.getVarRefList());
        }
        if(scope.getInnerScopeList().isEmpty()){
           return;
        }
        for (Scope child:scope.getInnerScopeList()){
            for(Variable var : scope.getVariableList() ){
                // insert a variable if it's not exist in the scope variable list
                if(avoidDuplicate(var,child)){
                    child.setVariableList(var);
                }
            }
            for(Variable var : scope.getArgsList() ){
                // insert a variable if it's not exist in the scope variable list
                if(avoidDuplicate(var,child)){
                    child.setVariableList(var);
                }
            }
            refRecursion(child);
        }
        return;
    }

    /**
     * Verify a variable isn't already in the scope variable list.
     * @param varToCheck the var needs to be added
     * @param scope the current scope
     * @return true if the variable is not in the scope's variable list and false otherwise.
     */
    private boolean avoidDuplicate(Variable varToCheck,Scope scope){
            for(Variable var: scope.getVariableList()){
                if(var.getVarName().equals(varToCheck.getVarName())){
                    if(var.getType().equals((varToCheck.getType())))
                        return false;
                }
            }
            return true;
        }

    /**
     * Handle a line which starts with "void"
     * @param m the line divided to groups
     * @throws WarningInFile in case a bad pattern was catch.
     */
    private void handleVoid(Matcher m) throws WarningInFile{
        String methodName = m.group(1);
        String args = m.group(2);
        Method curMethod;
        curMethod = new Method(methodName, args, lineNumber);
        globalScope.setmList(curMethod);
        Scope tempScope = new MethodScope(curScope);
        // adding the new scope to the innerScope list and update the current scope to the new one
        curScope.setInnerScopeList(tempScope);
        curScope = tempScope;
        curScope.setArgsList(curMethod.getMethodArgs());

    }

    /**
     * Check if the if-while pattern is valid
     * @param m the relevant matcher
     * @throws WarningInFile in case
     */
    private void handleIfWhile(Matcher m) throws WarningInFile{
        String args = m.group(2);
        // Added a non-positive limit in order to the pattern will be applied as many times as it occurred,
        // also empty places will be occurred.
        String[] optionalVars = args.split("\\|\\||&&",-1);
        for (String var : optionalVars){
            // The args could be an initialized var/ boolean / int/ double
            m = PATTERN_IF_WHILE_ARG.matcher(var);
            if(m.matches()) {
                if(!checkIfWhileArg(var.replaceAll(" ",""))){
                    // the given args isn;t a default int, double, or boolean expression
                    ifWhileRefList.add(new IfWhileRef(var,curScope,lineNumber));
                }
            }
            else{
                throw new WarningInFile("The if-while args are invalid");
            }
        }
    }

    /**
     * A help method to "handle if while method"
     * @param var a variable name
     * @return true if the var is valid as an if while argument.
     */
    private boolean checkIfWhileArg(String var){
        if(var.equals("true") || var.equals("false")){
            return true;
        }
        else if(var.matches("(-)?[0-9]+")){
            return true;
        }
        else if(var.matches("(-)?[0-9]+\\.[0-9]+")){
            return true;
        }
        return false;
    }
    /**
     * Checking if a scope is a "MethodScope"
     * @param curScope the current scope
     * @return true if the scope is a methodScope and false otherwise
     */
    private boolean isAMethodScope(Scope curScope){
        // checking if the cur scope is a method scope
        Class aClass = new MethodScope(globalScope).getClass();
        return (aClass.isInstance(curScope));
    }

    /**
     * Handle an assinment case
     * @param params should contain two variables
     * @throws WarningInFile in case params doesn't hold two valid strings
     */
    private void checkAssignmentCase(String[] params) throws WarningInFile{
        // in case the text line has more then two parameters
        if(params.length != 2) {
            // a valid line has the follow pattern: "  string = string ;"
            throw new WarningInFile("The line isn't valid");
        }
        String firstChar, secondChar;
        Matcher m = PATTERN_ONLY_STRING.matcher(params[0]);
        if(m.matches()){
            firstChar = m.group(1);
            m = PATTERN_ONLY_STRING_AND_SEMICOLON.matcher(params[1]);
            if(m.matches()){
                secondChar = m.group(1);
            }
            else{
                throw new WarningInFile("The second parameter isn't valid");
            }
        }
        else{
            throw new WarningInFile("The first parameter isn't valid");
        }
        curScope.setVarRefList(new VariableRef(firstChar,secondChar,curScope,lineNumber,false));
    }

    /**
     * Handle a ";" case
     * @param textLine a file text line need to be checked
     * @return true if the text line is valid
     * @throws WarningInFile in case the line is invalid
     */
    private boolean handleSemicolonLine(String textLine) throws WarningInFile {
        /** 4 cases need to be handle:
         * 1. return statement
         * 2. calling a method
         * 3. declare on a new var;
         * 4. assignment operation ("a=b")
         */


        // case 1 - handling a return statement
        if (textLine.replaceAll("\\s+", "").equals("return;")) {
            // a return statement is valid only inside an inner scope
            if (curScope.getFatherScope() == null){
                throw new WarningInFile("A return statement in the global scope");
            }
            if(isAMethodScope(curScope)){
                // kipping the line number in order to handle a closed bracket case.
                curScope.setLestReturnLine(lineNumber);
            }
            return true;
        }

        //case 2 - handling a method call line. In case the line fits to a "calling method" pattern
        // the method will be added to methodRefList.
        Matcher m = PATTERN_CALLING_A_METHOD.matcher(textLine);
        if(m.matches()){
            String methodName = m.group(1);
            // Added a non-positive limit in order to the pattern will be applied as many times as it occurred,
            // also empty places will be occurred.
            String[] args = m.group(2).split(",",-1);
            methodRefList.add(new MethodRef(methodName,args,curScope,lineNumber));
            return true;
        }

        //case 3 - check if one of the correct types are there.
        m = PATTERN_VALID_TYPE.matcher(textLine);
        if(m.matches()){
            String isFinal = m.group(1);
            String varType = m.group(2);
            String args = m.group(3).trim();
            args = args.substring(0, args.length() - 1);
            Matcher matcherVar = FINAL_PROBLEM.matcher(textLine);
            if(matcherVar.matches()){
                throw  new WarningInFile("bad pattern of final obj");
            }
            List<Variable> varList = Variable.handleNewVar(varType,args,isFinal,lineNumber);
            for(Variable var:varList){
                if(curScope.getFatherScope() == null){
                    var.setIsGlobal(true);
                }
                // in case the var reference is another variable adding to var ref
                if(var.getRefValue()!=null){
                    curScope.setVarRefList(new VariableRef(var.getVarName(),var.getRefValue(),curScope,lineNumber,true));
                }
            }
            curScope.setVariableList(varList);
            return true;
        }

        //case 4 - is there a valid assignment.
        if(textLine.contains("=")){
            // Added a non-positive limit in order to the pattern will be applied as many times as it occurred,
            // also empty places will be occurred.
            String[] params = textLine.split("=",-1);
            checkAssignmentCase(params);
            return true;
        }
        // none of the ";" 4 options occurred
        throw new WarningInFile("line is invalid");
    }


}

