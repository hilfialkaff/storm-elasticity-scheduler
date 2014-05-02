package storm.scheduler;

import java.util.HashMap;

public class Topology {
    private final String _name;
    private static HashMap<String, Node> _nodes;

    public Topology(String name, HashMap<String, Node> nodes) {
        _name = name;
        _nodes = nodes;

        for (Node node : _nodes.values()) {
            node.countChildren();
        }
    }

    public Node getNode(String name) {
        Node ret;

        System.out.println("getNodes(): " + _nodes);

        if (!_nodes.containsKey(name)) {
            ret = null;
        } else {
            ret = _nodes.get(name);
        }

        return ret;
    }

    public HashMap<String, Node> getNodes() {
        return _nodes;
    }

    public Node getNodeByTask(String taskName) {
        Node ret = null;

        for (Node node : _nodes.values()) {
            if (node.getTaskThroughput(taskName) != -1) {
                ret = node;
                break;
            }
        }

        return ret;
    }

    public String getName() {
        return _name;
    }
}
