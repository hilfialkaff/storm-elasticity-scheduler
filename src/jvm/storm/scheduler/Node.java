package storm.scheduler;

import java.util.HashMap;
import java.util.LinkedList;

public class Node {
    private final String _name;
    private final HashMap<String, Node> _children;
    private final HashMap<String, Double> _tasks;
    private double _throughput;
    private int _totalChildren;

    public Node(String name) {
        _name = name;
        _children = new HashMap<String, Node>();
        _tasks = new HashMap<String, Double>();
        _totalChildren = 0;
    }

    public int getPriority() {
        return _totalChildren;
    }

    public void countChildren() {
        int count = 0;
        HashMap<String, Node> visited = new HashMap<String, Node>();
        LinkedList<Node> nodes = new LinkedList<Node>();

        for (Node node : _children.values()) {
            nodes.push(node);
        }

        while (nodes.size() > 0) {
            Node node = nodes.removeFirst();
            if (visited.containsKey(node.getName())) {
                continue;
            }

            for (Node nodeTmp : node.getAllChildren().values()) {
                nodes.push(nodeTmp);
            }

            count += 1;
        }

        _totalChildren = count;
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

    public HashMap<String, Node> getAllChildren() {
        return _children;
    }

    public void setThroughput(double throughput) {
        _throughput = throughput;
    }

    public double getThroughput() {
        return _throughput;
    }

    public double getTaskBandwidth(String name) {
        double ret;

        if (!_tasks.containsKey(name)) {
            ret = -1;
        } else {
            ret = _tasks.get(name).doubleValue();
        }

        return ret;
    }
}
