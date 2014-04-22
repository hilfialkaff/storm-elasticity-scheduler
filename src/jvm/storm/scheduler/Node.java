package storm.scheduler;

import java.util.HashMap;

// TODO: 1 task per bolt
public class Node {
    private final String _name;
    private final HashMap<String, Node> _children;
    private final HashMap<String, Double> _tasks;
    private double _throughput;

    public Node(String name) {
        _name = name;
        _children = new HashMap<String, Node>();
        _tasks = new HashMap<String, Double>();
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
