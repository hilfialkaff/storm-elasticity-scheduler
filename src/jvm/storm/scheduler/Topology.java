package storm.scheduler;

public class Topology {
    private final String _name;
    private static Node _root;

    public Topology(String name, Node root) {
        _name = name;
        _root = root;
    }
}
