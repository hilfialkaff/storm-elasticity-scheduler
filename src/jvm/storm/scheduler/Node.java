package storm.scheduler;

import java.util.HashMap;

// TODO: 1 task per bolt
public class Node {
    private String _name;
    private HashMap<String, Node> _children;
    private double _throughput;

    public Node(String name) {
        _name = name;
        _children = new HashMap<String, Node>();
        _throughput = 0.0;
    }

    public String getName() {
        return _name;
    }

    public void addChildren(Node node) {
        _children.put(node.getName(), node);
    }

    public Node getChildren(String name) {
        return _children.get(name);
    }

    public void setThroughput(double throughput) {
        _throughput = throughput;
    }

    public double getThroughput() {
        return _throughput;
    }
}
