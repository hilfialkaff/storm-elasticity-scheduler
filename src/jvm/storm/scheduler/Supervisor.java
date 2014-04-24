package storm.scheduler;

import java.util.HashMap;

import backtype.storm.scheduler.ExecutorDetails;

public class Supervisor {
    private final String _name;
    private final HashMap<ExecutorDetails, Double> _tasks;

    public Supervisor(String name) {
        _name = name;
        _tasks = new HashMap<ExecutorDetails, Double>();
    }

    public String getName() {
        return _name;
    }

    public boolean addTask(ExecutorDetails task, double throughput) {
        boolean ret = false;

        if (!_tasks.containsKey(task)) {
            _tasks.put(task, throughput);
            ret = true;
        }

        return ret;
    }

    public HashMap<ExecutorDetails, Double> getTasks() {
        return _tasks;
    }

    public double computeThroughput() {
        double total = 0;

        for (double throughput : _tasks.values()) {
            total += throughput;
        }

        return total;
    }
}
