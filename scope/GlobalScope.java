package oop.ex6.scope;

import java.util.LinkedList;
import java.util.List;

// Creates a new global scope in case a new valid file is given

/**
 * An implementation of a global scope object (which is similar to a tree "root")
 */
public class GlobalScope extends Scope {
    private List<Method> mList;

    /**
     * A constructor (doesn't gets a father field because the global is the "root" of the scope's tree)
     */
    public GlobalScope() {
        super();
        // A global scope doesn't have a "}"
        closedBracket = true;
        // A list of all the methods in the file
        mList = new LinkedList<>();
    }

    /**
     * adding a new method to the mList
     * @param m - the method needs to be added
     */
    public void setmList(Method m) {
        this.mList.add(m);
    }

    /**
     * setting method mList
     * @return mList
     */
    public List<Method> getmList() {
        return mList;
    }
}
