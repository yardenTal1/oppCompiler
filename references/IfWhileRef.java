package oop.ex6.references;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oop.ex6.variable.Variable;
import oop.ex6.exception.WarningInFile;
import oop.ex6.scope.*;

/**
 * Valid that an if\ while decleration is valid.
 */
public class IfWhileRef extends Reference {

    private String name;

    /**
     * a constructor that create if\whileRef object
     * @param name - the name of argument in if\while pattern
     * @param scope - the appropriate scope
     * @param lineNum - line number
     */
    public IfWhileRef(String name, Scope scope, int lineNum) {
        super(scope, lineNum);
        this.name = name;
    }


    private final String PATTERN_CHECK_ARG2 = "^\\s*\\S+\\s*$";

    /**
     * A method that checks whether the if\whileRef argument has been set up legally
     * @return true if its legal and false if not
     * @throws WarningInFile
     */
    public boolean handleIfWhileRef() throws WarningInFile {
        int badStrike = 0;
        Pattern patternVariable = Pattern.compile(PATTERN_CHECK_ARG2);
        Matcher matcherVar = patternVariable.matcher(name);
        if (!matcherVar.matches()) {
            throw new WarningInFile("bad arg");
        }
        try {
            VariableRef.checkArgs("int", name);
        } catch (WarningInFile w) {
            badStrike++;
        }
        try {
            VariableRef.checkArgs("double", name);
        } catch (WarningInFile w) {
            badStrike++;
        }
        try {
            VariableRef.checkArgs("boolean", name);
        } catch (WarningInFile w) {
            badStrike++;
        }
        if (badStrike < 3) {
            return true;
        }
        Variable foundVar = varInScope(name, scope, lineNum, false, false);
        if (foundVar == null) {
            throw new WarningInFile("the if while arg isn;t valid");
        }
        if (!foundVar.ifInitialize()) {
            throw new WarningInFile("an if arg wasn't initialized");
        }
        if (foundVar.getType().equals("int") || foundVar.getType().equals("double") || foundVar.getType().equals("boolean")) {
            return true;
        }
        throw new WarningInFile("the if-while argument isn't valid");
    }
}