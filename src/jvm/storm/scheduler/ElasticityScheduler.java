package storm.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.scheduler.Cluster;
import backtype.storm.scheduler.EvenScheduler;
import backtype.storm.scheduler.ExecutorDetails;
import backtype.storm.scheduler.IScheduler;
import backtype.storm.scheduler.SchedulerAssignment;
import backtype.storm.scheduler.SupervisorDetails;
import backtype.storm.scheduler.Topologies;
import backtype.storm.scheduler.TopologyDetails;
import backtype.storm.scheduler.WorkerSlot;

/**
 * Put the following config in <code>nimbus</code>'s <code>storm.yaml</code>:
 *
 * <pre>
 *     # tell nimbus to use this custom scheduler
 *     storm.scheduler: "storm.scheduler.ElasticityScheduler"
 * </pre>
 */
public class ElasticityScheduler implements IScheduler {
    private static final Logger LOG = LoggerFactory
        .getLogger(ElasticityScheduler.class);
    private static final String ROOT_NODE = "root";
    private static final String TOPOLOGY_DIR = "/proj/CS525/exp/stormCluster/project/storm-experiments/topologies/";
    private static final String PROJECT_DIR = "/proj/CS525/exp/stormCluster/project/storm-elasticity-scheduler/";
    private static final String SCRIPT_DIR = "scripts/";
    private static final String METRIC_FILE = "metrics/metrics.log";
    private static final String TOPOLOGY_ONE = "TestTopology";

    private static HashMap<String, Topology> _topologies = new HashMap<String, Topology>();
    private static int _numMachines = 0;

    @Override
    public void prepare(Map conf) {
        /**
         * String[] cmd = {
         * "/users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/getMetrics.sh peng 10 /var/storm/storm-0.9.1/logs/metrics.log /users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/metrics 2 /"
         * }; try{ Process p = Runtime.getRuntime().exec(cmd); } catch
         * (IOException e){ System.err.println("Caught IOException: " +
         * e.getMessage()); }
         **/
        try {
            Process proc = Runtime.getRuntime().exec(
                PROJECT_DIR + SCRIPT_DIR + "get_metrics.sh /");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
        ArrayList<String> topologyToDelete = new ArrayList<String>();

        parseData(PROJECT_DIR + METRIC_FILE);
        readTopology();

        if (cluster.getSupervisors().size() == _numMachines) {
            return;
        }

        for (Entry<String, Topology> topoEntry : _topologies.entrySet()) {
            TopologyDetails topology = topologies.getByName(topoEntry.getKey());

            // Topology doesn't exist anymore -> remove from database, else
            // schedule
            if (topology == null) {
                topologyToDelete.add(topoEntry.getKey());
            } else {
                boolean needsScheduling = cluster.needsScheduling(topology);

                if (!needsScheduling) {
                    Log.info("Our topology DOES NOT NEED scheduling.");
                } else {
                    System.out.println("Our topology needs scheduling.");
                    // find out all the needs-scheduling components of this
                    // topology
                    Map<String, List<ExecutorDetails>> componentToExecutors = cluster
                        .getNeedsSchedulingComponentToExecutors(topology);

                    Log.info("needs scheduling(component->executor): "
                        + componentToExecutors);
                    Log.info("needs scheduling(executor->components): "
                        + cluster
                            .getNeedsSchedulingExecutorToComponents(topology));
                    SchedulerAssignment currentAssignment = cluster
                        .getAssignmentById(topology.getId());
                    if (currentAssignment != null) {
                        Log.info("current assignments: "
                            + currentAssignment.getExecutorToSlot());
                    } else {
                        Log.info("current assignments: {}");
                    }

                    Collection<SupervisorDetails> supervisors = cluster
                        .getSupervisors().values();
                    _numMachines = supervisors.size();

                    for (SupervisorDetails supervisor : supervisors) {
                        Map meta = (Map) supervisor.getSchedulerMeta();

                        List<WorkerSlot> availableSlots = cluster
                            .getAvailableSlots(supervisor);
                        for (WorkerSlot slot : availableSlots) {
                            LOG.info("Slot: " + slot.getPort());
                        }
                    }
                }
            }
        }

        /* Delete non-existant topologies */
        for (String s : topologyToDelete) {
            _topologies.remove(s);
        }

        // Use EvenScheduler for the rest
        new EvenScheduler().schedule(topologies, cluster);
    }

    // Parse metric data
    public void parseData(String metricFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(metricFile);
        } catch (FileNotFoundException e) {
            System.err.println("Caught FileNotFoundException: "
                + e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(fis);
        Map<String, Double> occurrence = new HashMap<String, Double>();
        Map<String, Double> numExecute = new HashMap<String, Double>();
        while (scanner.hasNextLine()) {
            // System.out.println(scanner.nextLine());
            String line = scanner.nextLine();
            String[] splited = line.split("\\s+");
            int numCount = 0;

            for (int i = 0; i < splited.length; i++) {
                // System.out.println(Integer.toString(i)+"---"+splited[i]);
            }
            if (occurrence.containsKey(splited[5]) == false) {
                occurrence.put(splited[5], 0.0);
                numExecute.put(splited[5], 0.0);
            }

            try {
                numCount = Integer.parseInt(splited[7]);
            } catch (Exception e) {
                LOG.info(e.getMessage());
                continue;
            }

            numExecute.put(splited[5], numExecute.get(splited[5]) + numCount);
            occurrence.put(splited[5], occurrence.get(splited[5]) + 1);
            // System.out.println(map.get(splited[5]));
        }
        // System.out.println("/*****occurrence*******/");
        // printMap(occurrence);
        // System.out.println("/*****numExecute*******/");
        // printMap(numExecute);
        System.out.println("/*****avg*******/");
        for (Entry<String, Topology> topoEntry : _topologies.entrySet()) {
            getAverage(occurrence, numExecute, topoEntry.getKey());
        }

        scanner.close();
    }

    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());

        }
    }

    public static void getAverage(Map occurrence, Map numExecute,
        String topoName) {
        Iterator it = numExecute.entrySet().iterator();
        Topology topo = _topologies.get(topoName);
        Node root = topo.getRoot();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            double val = (Double) entry.getValue();
            String nodeName = (key.split(":"))[1];
            LOG.info("Getting average for: " + nodeName);
            Node node = root.getChildren(nodeName);

            double avg = (val) / (Double) occurrence.get(key);
            System.out.println(key + " avg: " + avg);

            if (node == null) {
                LOG.info("TOPOLOGY INFORMATION IS WRONG");
            } else {
                node.setThroughput(avg);
            }
        }
    }

    public void readTopology() {
        try {
            File folder = new File(TOPOLOGY_DIR);
            File[] topologyFiles = folder.listFiles();

            if (topologyFiles == null) {
                return;
            }

            for (int i = 0; i < topologyFiles.length; i++) {
                FileReader file = new FileReader(topologyFiles[i]);
                BufferedReader reader = new BufferedReader(file);
                String line = null;
                Node root = new Node(ROOT_NODE);
                String topologyName = topologyFiles[i].getName();

                if (_topologies.containsKey(topologyName)
                    || topologyName.startsWith(".")) {
                    continue;
                }

                try {
                    while ((line = reader.readLine()) != null) {
                        String _line[] = line.split(",");
                        Node parent = new Node(_line[0]);
                        Node child = new Node(_line[1]);

                        parent.addChildren(child);
                        root.addChildren(parent);
                        root.addChildren(child);
                    }

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Topology newTopology = new Topology(topologyName, root);
                System.out.println("Added new topology: " + topologyName);
                _topologies.put(topologyName, newTopology);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
