import GameObject.State;

public class Main {
    public static void main(String[] args) {
        // Create a new State object
        State state = new State("Initial State");
        State parent = new State("Parent State");
        State grandparent = new State("Grandparent State");
        state.parent = parent;
        parent.parent = grandparent;

        // Print the parent of the state
        state.printParent();
    }
}
