package storm.scheduler;

import java.util.LinkedList;

public class Task {
    private final int NUM_BINS = 5;
    private final int NUM_ELEMENT_PER_BINS = 3;

    private final String _name;
    private LinkedList<Double>[] _throughputs;

    public Task(String name) {
        _name = name;

        for (int i = 0; i <= NUM_BINS; i++) {
            _throughputs[i] = new LinkedList<Double>();
        }
    }

    public String getName() {
        return _name;
    }

    public void addThroughput(double throughput, int curTime) {
        for (int i = NUM_BINS; i > 0; i--) {
            if (curTime % Math.pow(2, i) == 0) {
                _throughputs[i].add(throughput);

                if (_throughputs[i].size() > NUM_ELEMENT_PER_BINS) {
                    _throughputs[i].remove();
                }
            }
        }
    }

    public double getThroughput() {
        double throughput = 0;
        int numElements = 0;

        for (int i = 0; i <= NUM_BINS; i++) {
            for (Double d : _throughputs[i]) {
                throughput += d;
                numElements++;
            }
        }

        return throughput / numElements;
    }
}