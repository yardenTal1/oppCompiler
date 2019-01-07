package oop.ex6.references;

import oop.ex6.exception.WarningInFile;
import oop.ex6.variable.Variable;
import oop.ex6.scope.*;

import java.util.List;

/**
 * An abstract class confirms that line is valid (check references)
 */
public abstract class Reference {
    protected Scope scope;
    protected int lineNum;

    /**
     * a constructor that create Reference object
     * @param scope - the appropriate scope
     * @param lineNum - line number
     */
    public Reference(Scope scope, int lineNum) {
        this.scope = scope;
        this.lineNum = lineNum;
    }

    /**
     * A method that checks if the variableRef has been set
     * @param name - the variable name
     * @param curScope -  the appropriate scope
     * @param lineNum - line number
     * @param isDeclare - if the variable is declared
     * @param isRef - if its a variableRef
     * @return the variable if its declared and null if its not founded
     * @throws WarningInFile
     */
    public Variable varInScope( String name, Scope curScope, int lineNum, boolean isDeclare, boolean isRef) throws WarningInFile {
        List<Variable> varList = scope.getVariableList();
        String type = "false";
        for (Variable var : varList) {
            type = findVar(name,curScope,lineNum,isDeclare,isRef,var);
            if(!type.equals("false")){
                return var;
            }
        }
        varList = scope.getArgsList();
        for (Variable var : varList) {
            type = findVar(name,curScope,lineNum,false,false,var);
            if(!type.equals("false")){
                return var;
            }
        }
        return null;
    }

    /**
     * A method that checks if the variableRef has been legally increased
     * @param name -  the variable name
     * @param curScope - the appropriate scope
     * @param lineNum - line number
     * @param isDeclare - if the variable is declared
     * @param isRef - if its a variableRef
     * @param var - the variable that id checked
     * @return the variable type if its legal variable
     * @throws WarningInFile
     */
    private String findVar(String name, Scope curScope, int lineNum, boolean isDeclare, boolean isRef, Variable var)
            throws WarningInFile {
        // find the var in the var list which represent the ref
        if (var.getVarName().replaceAll(" ","").equals(name.replaceAll(" ",""))) {
            // in case var is final assignment is invalid in case it's not a declaration var line
            if(!isDeclare && var.getIsFinal() &&!isRef){
                throw new WarningInFile("assignment to final object");
            }
            // in case the var in declaration line return its type
            if(isDeclare){
                return var.getType();
            }
            if(isRef){
                if(!var.ifInitialize()){
                    throw new WarningInFile("the var wasn't initialized");
                }
                else {
                    if(lineNum < var.getLineNum() && !var.getIsGlobal())
                        throw new WarningInFile("the var wasn't declare yet");
                }
            }
            // case 0 - the current case!!!
            if(var.getIsGlobal() && scope.getFatherScope() != null && var.getLineNum() < lineNum ){
                return var.getType();}

            // case 1: ref not from global and var deceleration from global
            else if(var.getIsGlobal() && scope.getFatherScope() != null && var.getLineNum() > lineNum ){
                return var.getType();}


            // case 2: The ref and var are global
            else if (var.getIsGlobal() && scope.getFatherScope() == null && var.getLineNum() < lineNum){
                return var.getType();
            }

            // case 3: The ref and var not global
            else if(!var.getIsGlobal() && scope.getFatherScope() != null && var.getLineNum() < lineNum){
                return var.getType();
            }
            else if(!var.getIsGlobal() && scope.getFatherScope() == null ){
                return var.getType();}

            else {
                throw new WarningInFile("The var wasn't define yet");
            }
        }
        return "false";
    }
}
