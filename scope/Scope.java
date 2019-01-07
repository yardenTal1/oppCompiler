package oop.ex6.scope;

import oop.ex6.variable.*;
import oop.ex6.exception.WarningInFile;
import oop.ex6.references.VariableRef;

import java.util.LinkedList;
import java.util.List;

// An abstract class which create a new scope object in case a "{" char appear in the file

/**
 * An abstract class which define a new scope
 */
public abstract class Scope {
    protected List<Scope> innerScopeList;
    protected List<Variable> variableList, argsList;
    protected Scope fatherScope;
    protected boolean closedBracket;
    protected List<VariableRef> varRefList;
    private int lestReturnLine;

    /**
     * A constructor that create a scope object
     */
    public Scope() {
      innerScopeList = new LinkedList<>();
      variableList = new LinkedList<>();
      argsList = new LinkedList<>();
      varRefList = new LinkedList<>();
      fatherScope = null;
      closedBracket = false;
    }

    /**
     * @return the scope's father
     */
    public Scope getFatherScope() {
        return fatherScope;
    }

    /**
     * @return the inner scope list
     */
    public List<Scope> getInnerScopeList() {
        return innerScopeList;
    }

    /**
     * setting the inner scope list
     * @param innerScope - new inner scope list
     */
    public void setInnerScopeList(Scope innerScope) {
        this.innerScopeList.add(innerScope);
    }

    /**
     * @param closedBracket changes to "true"
     */
    public void setClosedBracket(boolean closedBracket) {
        this.closedBracket = closedBracket;
    }

    /**
     * @return "true" if this scope already closed and false otherwise
     */
    public boolean isClosedBracket() {
        return closedBracket;
    }

    /**
     * @return the line of the lest return statement on this scope
     * it's relevant only for a method scope
     */
    public int getLestReturnLine() {
        return lestReturnLine;
    }

    /**
     * @param lestReturnLine set this field to the new line number
     */
    public void setLestReturnLine(int lestReturnLine) {
        this.lestReturnLine = lestReturnLine;
    }

    /**
     * @return the scope variable list
     */
    public List<Variable> getVariableList() {
        return variableList;
    }

    /**
     * Adding new variables to this scope local variable list
     * @param variableList a var list need to be added
     */
    public void setVariableList(List<Variable> variableList) throws WarningInFile{
        if(variableList != null){
            for(Variable var : variableList ){
                    if(avoidDuplicate1(var) && avoidDuplicate2(var)){
                        this.variableList.add(var);
                    }
                    else throw new WarningInFile("duplicate var names");
            }
        }
    }

    /**
     * Adding new variables to this scope local variable list
     * @param varToAdd a var  need to be added
     */
    public void setVariableList(Variable varToAdd) throws WarningInFile{
        variableList.add(varToAdd);
    }

    /**
     * @return a list of arguments
     */
    public List<Variable> getArgsList() {
        return argsList;
    }

    /**
     * setting arguments list
     * @param argsList - the list to setting
     * @throws WarningInFile
     */
    public void setArgsList(List<Variable> argsList)throws WarningInFile {
            for(Variable var : argsList ){
                if(avoidDuplicate2(var)){
                    this.argsList.add(var);
                }
                else throw new WarningInFile("duplicate var names");

            }
        }

    /**
     * Confirm that a variable with the same name wasn't already define in the scope
     * @param varToCheck the variable need to be check if is name is unique
     * @return true if the isn't duplicate and false otherwise.
     */
    private boolean avoidDuplicate1(Variable varToCheck){
        for(Variable var: this.variableList){
            if(var.getVarName().equals(varToCheck.getVarName())){
                return false;
            }
        }
        return true;
    }

    /**
     * Confirm that a variable with the same name wasn't already define in the scope
     * @param varToCheck the variable need to be check if is name is unique
     * @return true if the isn't duplicate and false otherwise.
     */
    private boolean avoidDuplicate2(Variable varToCheck){
        for(Variable var: this.argsList){
            if(var.getVarName().equals(varToCheck.getVarName())){
                return false;
            }
        }
        return true;
    }

    /**
     * @return the varRef list
     */
    public List<VariableRef> getVarRefList() {
        return varRefList;
    }

    /**
     * @param varRef - setting the varRef list
     */
    public void setVarRefList(VariableRef varRef) {
        this.varRefList.add(varRef);
    }
}
