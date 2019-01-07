package oop.ex6.scope;

import oop.ex6.variable.Variable;
import oop.ex6.exception.WarningInFile;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A method class which describe a method object which has a name and a list of arguments

/**
 *  An implementation of a method scope object (in this exercise describes only a void method)
 */
public class Method {
    // long regex to find this pattern : (final) type var_name
    private final Pattern patternValidType = Pattern.compile("^\\s*(final\\s+)?(int|double|boolean|String|char)\\s+(.*)");
    private String name;
    private String[] optionalVals;
    private List<Variable> methodArgs = new LinkedList<Variable>();
    /**
     * A constructor that create new Method object
     * @param mName   the method name
     * @param args    the method given args
     * @param lineNum where the method appeared in the file
     * @throws WarningInFile in case one of the given args isn;t valid
     */
    public Method(String mName, String args, int lineNum) throws WarningInFile {
        name = mName;
        optionalVals = args.split(",", -1);
        for (String var : optionalVals) {
            //handle var gets the var with ";" at the end.
            Matcher m = patternValidType.matcher(var);
            if (m.matches()) {
                //long line
                List<Variable> tempL = Variable.handleNewVar(m.group(2), m.group(3), m.group(1),
                        lineNum);
                for(Variable tempVar:tempL){
                    methodArgs.add(tempVar);
                }
            }
            else if (!var.equals("")){
                throw  new WarningInFile("bad arg");
            }

        }
    }

    /**
     * @return the method name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the method arguments
     */
    public List<Variable> getMethodArgs() {
        return methodArgs;
    }
}


