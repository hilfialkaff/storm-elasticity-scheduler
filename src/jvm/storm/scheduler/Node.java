package storm.scheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Node {
    private final String _name;
    private final HashMap<String, Node> _children;
    private final HashMap<String, Double> _tasks;
    private final HashMap<String, Double> _tasksAccum;
    private double _throughput;
    private int _totalChildren;

    public Node(String name) {
        _name = name;
        _children = new HashMap<String, Node>();
        _tasks = new HashMap<String, Double>();
        _tasksAccum = new HashMap<String, Double>();
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

    public HashMap<String, Double> getTasks() {
        return _tasks;
    }

    public void addTask(String task_id, Double throughput) {
        _tasks.put(task_id, throughput);
    }

    public boolean containsTask(String task_id) {
        return _tasks.containsKey(task_id);
    }

    public void setTaskThroughput(String task_id, Double Throughput) {
        _tasks.put(task_id, Throughput);
    }

    public double getTaskThroughput(String name) {
        double ret;

        if (!_tasks.containsKey(name)) {
            ret = -1;
        } else {
            ret = _tasks.get(name).doubleValue();
        }

        return ret;
    }

    public void setTaskAccumThroughput(String task_id, Double Throughput) {
        _tasksAccum.put(task_id, Throughput);
    }

    public double getTaskAccumThroughput(String name) {
        double ret;

        if (!_tasksAccum.containsKey(name)) {
            ret = -1;
        } else {
            ret = _tasksAccum.get(name).doubleValue();
        }

        return ret;
    }

    public double CalculateOverallThroughput() {

        Iterator<Map.Entry<String, Double>> it = _tasks.entrySet().iterator();
        Double throughput = 0.0;

        while (it.hasNext()) {
            Map.Entry<String, Double> entry = it.next();
            String key = entry.getKey();
            Double val = entry.getValue();
            throughput += val;
        }

        return throughput;
    }

}
