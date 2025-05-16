package GameObject;

public class State {
    private String name;
    public State parent;

    public State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void printParent() {
        if (parent != null) {
            parent.printParent();
            System.out.print("Parent: " + parent.getName() + "->");
            
        } else {
            return;
        }
    }
}

