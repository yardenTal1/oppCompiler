package oop.ex6.scope;

// Creates a new method scope in case a method declaration appeared in the file
public class MethodScope extends Scope {

    /**
     * A constructor
     * @param father this scope's father
     */
    public MethodScope(Scope father) {
        this.fatherScope = father;
    }

}
