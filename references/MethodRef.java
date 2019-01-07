package oop.ex6.references;

import oop.ex6.variable.Variable;
import oop.ex6.exception.WarningInFile;
import oop.ex6.scope.*;


import java.util.LinkedList;
import java.util.List;

/**
 * Confirm that a method calling line is valid
 */
public class MethodRef extends Reference{

    private String[] methodArgs;
    private String methodName;

    /**
     * a constructor that create methodRef object
     * @param methodName - the method name
     * @param methodArgs - thw name of argument method
     * @param scope - the appropriate scope
     * @param lineNum - line number
     */
    public MethodRef(String methodName, String[] methodArgs, Scope scope, int lineNum) {
        super(scope,lineNum);
        this.methodName = methodName;
        this.methodArgs = methodArgs;
    }

    /**
     * A method that check if the methodRef ic valid
     * @param globalScope
     * @return true tif its legal anf false if not
     * @throws WarningInFile
     */
    public boolean isMethodValid(GlobalScope globalScope) throws WarningInFile{
        // check if a method with the same name was declare before
//        if(globalScope.getmList().contains(methodName)){
          for(Method m :globalScope.getmList()){
            if(m.getName().equals(methodName)) {
                List<String> typeList = new LinkedList<>();
                for (String arg : methodArgs) {
                    if(!arg.equals("")){
                        Variable foundVar = varInScope(arg, scope, lineNum,false,false);
                        if(foundVar!=null){
                            typeList.add(foundVar.getType());
                        }
                    }
                }
                // return true if the method given types are correct
                if(typeList.isEmpty() && m.getMethodArgs().isEmpty()){
                    return true;
                }
                if(typeList.isEmpty() && !m.getMethodArgs().isEmpty()) {
                    if(methodArgs.length < m.getMethodArgs().toArray().length){
                        throw new WarningInFile("bad args to method");
                    }
                    for (int i = 0; i < m.getMethodArgs().toArray().length; i++) {
                        if (!VariableRef.checkArgs(m.getMethodArgs().get(i).getType(), methodArgs[i])) {
                            throw new WarningInFile("bad args in method");
                        }
                    }
                    return true;
                }

                if(typeList.toArray().length == m.getMethodArgs().toArray().length){
                    for (int i=0; i<typeList.toArray().length; i++){
                        if(!typeList.get(i).equals(m.getMethodArgs().get(i).getType())){
                            throw new WarningInFile("a given arg's type is incorrect");
                        }
                    }
                    return true;
                }
                throw new WarningInFile("the given args are not incorrect");
            }
        }
        return false;
    }
}
