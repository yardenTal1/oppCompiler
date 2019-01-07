package oop.ex6.variable;

import java.util.regex.Pattern;
import oop.ex6.exception.WarningInFile;

/**
 A factory implementation which create a new variable as required.
 */
public class VariableFactory {

    private static final String PATTERN_STRING = "(\\s*\".+\"s*$)";
    private static final String PATTERN_CHAR = "(^\\s*'.'\\s*$)";
    private static final VariableFactory instance = new VariableFactory();

    /**
     * A private constructor for "Singelton" design for factory
     */
    private VariableFactory() {
    }

    /**
     * create factory instance
     * @return instance
     */
    public static VariableFactory getInstance() {
        return instance;
    }

    /**
     * A method that create new variable according to the type requested, if all the details legal
     * @param type - the variables type
     * @param varName - tha variable name
     * @param isFinal - true of ots a final variable
     * @param lineNum - line number
     * @return new variable
     * @throws WarningInFile
     */
    public static Variable makeVariable(String type, String varName, boolean isFinal, int lineNum) throws WarningInFile {
            switch (type) {
                case "int":
                    //Checks whether the new variable has been set and initialized together
                    if (varName.contains("=")) {
                        String[] valuesVariable = varName.split("=");
                        varName = valuesVariable[0];
                        IntVariable newIntVar;
                        try {
                            Integer.parseInt(valuesVariable[1].replaceAll(" ",""));
                            newIntVar = new IntVariable(type, varName, isFinal, lineNum);
                            newIntVar.setIsInitialize(lineNum);
                            return newIntVar;
                        } catch (NumberFormatException e) {
                            if (Variable.checkVarName(valuesVariable[1])){
                                newIntVar = new IntVariable(type, varName, isFinal, lineNum);
                                newIntVar.setIsInitialize(lineNum);
                                newIntVar.setRefValue(valuesVariable[1]);
                                return newIntVar;}
                            throw new WarningInFile("invalid assignment: reqaired int, found another type");
                        }
                    }return new IntVariable(type, varName, isFinal, lineNum);


                case "boolean":
                    //Checks whether the new variable has been set and initialized together
                    if (varName.contains("=")) {
                        String[] valuesVariable = varName.split("=");
                        varName = valuesVariable[0];
                        BooleanVariable newBoolVar;
                        try {
                            // long if
                            if (valuesVariable[1].replaceAll(" ","").equals("false") ||
                                    (valuesVariable[1].replaceAll(" ","").equals("true"))) {
                                newBoolVar = new BooleanVariable(type, varName, isFinal, lineNum);
                                newBoolVar.setIsInitialize(lineNum);
                                return newBoolVar;
                            } else {
                                Double.parseDouble(valuesVariable[1].replaceAll(" ",""));
                                newBoolVar = new BooleanVariable(type, varName, isFinal, lineNum);
                                newBoolVar.setIsInitialize(lineNum);
                                return newBoolVar;
                            }
                        } catch (NumberFormatException e) {
                            if (Variable.checkVarName(valuesVariable[1])){
                                newBoolVar = new BooleanVariable(type, varName, isFinal, lineNum);
                                newBoolVar.setRefValue(valuesVariable[1]);
                                newBoolVar.setIsInitialize(lineNum);
                                return newBoolVar;}
                            throw new WarningInFile("invalid assignment: reqaired int, found another type");
                        }
                    }return new BooleanVariable(type, varName, isFinal, lineNum);


                case "String":
                    //Checks whether the new variable has been set and initialized together
                    if (varName.contains("=")) {
                        String[] valuesVariable = varName.split("=");
                        varName = valuesVariable[0];
                        StringVariable newStringVar;
                        Pattern patternVariable = Pattern.compile(PATTERN_STRING);
                        if (patternVariable.matcher(valuesVariable[1]).matches()) {
                            newStringVar = new StringVariable(type, varName, isFinal, lineNum);
                            newStringVar.setIsInitialize(lineNum);
                        } else{
                            if (Variable.checkVarName(valuesVariable[1])){
                                newStringVar = new StringVariable(type, varName, isFinal, lineNum);
                                newStringVar.setRefValue(valuesVariable[1]);
                                newStringVar.setIsInitialize(lineNum);
                                return newStringVar;}
                            throw new WarningInFile("Invalid value");
                        }
                    }return new StringVariable(type, varName, isFinal,lineNum);


                case "char":
                    //Checks whether the new variable has been set and initialized together
                    if (varName.contains("=")) {
                        String[] valuesVariable = varName.split("=");
                        varName = valuesVariable[0];
                        CharVariable newCharVar;
                        Pattern patternVariable = Pattern.compile(PATTERN_CHAR);
                        if (patternVariable.matcher(valuesVariable[1]).matches()) {
                            newCharVar = new CharVariable(type, varName, isFinal, lineNum);
                            newCharVar.setIsInitialize(lineNum);}
                        else {
                            if (Variable.checkVarName(valuesVariable[1])){
                                newCharVar = new CharVariable(type, varName, isFinal, lineNum);
                                newCharVar.setRefValue(valuesVariable[1]);
                                newCharVar.setIsInitialize(lineNum);
                                return newCharVar;}
                            throw new WarningInFile("Invalid value");
                        }
                    }return new CharVariable(type, varName, isFinal, lineNum);

                case "double":
                    //Checks whether the new variable has been set and initialized together
                    if (varName.contains("=")) {
                        String[] valuesVariable = varName.split("=");
                        varName = valuesVariable[0];
                        DoubleVariable newDoubleVar;
                        try {
                            Double.parseDouble(valuesVariable[1].replaceAll(" ",""));
                            newDoubleVar = new DoubleVariable(type, varName, isFinal, lineNum);
                            newDoubleVar.setIsInitialize(lineNum);
                            return newDoubleVar;
                        } catch (NumberFormatException e) {
                            if (Variable.checkVarName(valuesVariable[1])){
                                newDoubleVar = new DoubleVariable(type, varName, isFinal,lineNum);
                                newDoubleVar.setRefValue(valuesVariable[1]);
                                newDoubleVar.setIsInitialize(lineNum);
                                return newDoubleVar;}
                            throw new WarningInFile("invalid assignment: reqaired int or double, found another type");
                        }
                    } return new DoubleVariable(type, varName, isFinal,lineNum);
            }
        return new DoubleVariable("double","a",true, 1); //not supposed to get here
    }
}




