package oop.ex6.scope;

// Creating a new if-while scope

/**
 * An implementation of an if scope or a while scope object (in this exercise the if and the while has the same behaviour)
 */
public class IfWhileScope extends Scope {

    /**
     * The object's constructor.
     * @param father this scope's father
     */
    public IfWhileScope(Scope father) {
        this.fatherScope = father;
    }

}
