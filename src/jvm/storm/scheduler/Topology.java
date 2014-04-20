package storm.scheduler;

public class Topology {
    private final String _name;
    private static Node _root;

    public Topology(String name, Node root) {
        _name = name;
        _root = root;
    }

    public Node getRoot() {
        return _root;
    }

    public Node getNode(String name) {
        return _root.getChildren(name);
    }
}
