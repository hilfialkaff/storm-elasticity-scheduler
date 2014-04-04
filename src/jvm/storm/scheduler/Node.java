package storm.scheduler;

import java.util.HashMap;

public class Node {
    public String _name;
    public HashMap<String, Node> _children;

    public Node(String name) {
        _name = name;
        _children = new HashMap<String, Node>();
    }

    public String getName() {
        return _name;
    }

    public void addChildren(Node node) {
        _children.put(node.getName(), node);
    }
}
