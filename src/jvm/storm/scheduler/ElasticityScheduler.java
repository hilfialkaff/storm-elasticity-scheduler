package storm.scheduler;

import java.io.BufferedReader;
import java.io.File;
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
    // private static final String NIMBUS_HOST =
    // "node-1.stormcluster.cs525.emulab.net";
    private static final String NIMBUS_HOST = "127.0.0.1";
    private static final String INTERVAL = "600";
    private static final int METRICS_RECV_PORT = 9005;
    private static final String ELASTICITY_SCHEDULER_DIR = "/proj/CS525/exp/stormCluster/project/storm-elasticity-scheduler";

    private static HashMap<String, Topology> _topologies = new HashMap<String, Topology>();
    private static int _numMachines = 0;
    private server metrics_server;
    private Process theProcess;

    @Override
    public void prepare(Map conf) {
        this.metrics_server = new server(METRICS_RECV_PORT);
        this.metrics_server.start();
        System.out.println("Server Successfully started!!");

        Process theProcess = null;
        BufferedReader inStream = null;

        /*
         * try { String command = "java -cp "+ELASTICITY_SCHEDULER_DIR+
         * "/target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies.jar storm.scheduler.ThriftMetrics"
         * ; System.out.println("Starting Metrics client routine...");
         * this.theProcess =
         * Runtime.getRuntime().exec("java -cp "+ELASTICITY_SCHEDULER_DIR+
         * "/target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies.jar storm.scheduler.ThriftMetrics"
         * ); System.out.println("Metrics client routine started..."); //java
         * -cp
         * target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies
         * .jar storm.scheduler.ThriftMetrics } catch(IOException e) {
         * System.err.println("Error on exec() method"); e.printStackTrace(); }
         * 
         * try { inStream = new BufferedReader(new InputStreamReader(
         * this.theProcess.getInputStream() ));
         * System.out.println(inStream.readLine()); } catch(IOException e) {
         * System.err.println("Error on inStream.readLine()");
         * e.printStackTrace(); }
         */

    }

    public void printOurTopology() {
        for (Topology topology : _topologies.values()) {
            HashMap<String, Node> nodes = topology.getNodes();

            LOG.info("Topology: " + topology.getName());
            for (Node node : nodes.values()) {
                LOG.info("Node " + node.getName() + " has throughput"
                    + node.getTasks());
            }
        }
    }

    public void printCurTopology(Cluster cluster, TopologyDetails topology) {
        Map<String, List<ExecutorDetails>> componentToExecutors = cluster
            .getNeedsSchedulingComponentToExecutors(topology);

        Log.info("needs scheduling(component->executor): "
            + componentToExecutors);
        Log.info("needs scheduling(executor->components): "
            + cluster.getNeedsSchedulingExecutorToComponents(topology));
        SchedulerAssignment currentAssignment = cluster
            .getAssignmentById(topology.getId());
        if (currentAssignment != null) {
            Log.info("current assignments: "
                + currentAssignment.getExecutorToSlot());
        } else {
            Log.info("current assignments: {}");
        }

        Collection<SupervisorDetails> supervisors = cluster.getSupervisors()
            .values();

        for (SupervisorDetails supervisor : supervisors) {
            List<WorkerSlot> availableSlots = cluster
                .getAvailableSlots(supervisor);
            for (WorkerSlot slot : availableSlots) {
                LOG.info("Slot: " + slot.getPort());
            }
        }
    }

    /* TODO: Currently only assume one topology */
    public void _schedule(Topologies topologies, Cluster cluster) {
        for (Topology topology : _topologies.values()) {
            TopologyDetails topologyDetails = topologies.getByName(topology
                .getName());

            if (topologyDetails == null) {
                continue;
            }

            HashMap<String, Supervisor> supervisors = new HashMap<String, Supervisor>();
            SchedulerAssignment currentAssignment = cluster
                .getAssignmentById(topologyDetails.getId());
            Collection<SupervisorDetails> clusterSupervisors = cluster
                .getSupervisors().values();
            double avgThroughput = 0.0;

            if (currentAssignment == null) {
                /* Leaving this for EvenScheduler */
                Log.info("current assignments: {}");
                return;
            }

            Map<ExecutorDetails, WorkerSlot> executorMap = currentAssignment
                .getExecutorToSlot();

            /* TODO: Assume there is only one task */
            for (Entry<ExecutorDetails, WorkerSlot> executorEntry : executorMap
                .entrySet()) {
                ExecutorDetails executorDetails = executorEntry.getKey();
                String startTask = Integer.toString(executorDetails
                    .getStartTask());
                String supervisorName = executorEntry.getValue().getNodeId();
                Supervisor supervisor;
                Node node = null;
                double throughput;

                if (!supervisors.containsKey(supervisorName)) {
                    supervisor = new Supervisor(supervisorName);
                    supervisors.put(supervisorName, supervisor);
                } else {
                    supervisor = supervisors.get(supervisorName);
                }

                node = topology.getNodeByTask(startTask);

                if (node == null) {
                    continue;
                }

                throughput = node.getTaskThroughput(startTask);
                supervisor.addTask(executorDetails, throughput);

            }

            for (Node node1 : topology.getNodes().values()) {
                LOG.info("node id: " + node1.getName() + "tasks: "
                    + node1.getTasks());
            }

            ArrayList<ExecutorDetails> tasksToMigrate = new ArrayList<ExecutorDetails>();
            avgThroughput = computeAvgThroughput(supervisors);
            double throughputToMigrate = avgThroughput * supervisors.size()
                / clusterSupervisors.size();
            double migratedThroughput = 0.0;

            /* Find which tasks to migrate */
            for (Supervisor supervisor : supervisors.values()) {
                HashMap<ExecutorDetails, Double> curTasks = supervisor
                    .getTasks();
                double bestThroughput = 0.0;
                ExecutorDetails bestTask = new ExecutorDetails(0, 0);

                LOG.info("Supervisor:" + supervisor.getName() + " migrated: "
                    + migratedThroughput + " needed: " + throughputToMigrate);

                for (Entry<ExecutorDetails, Double> taskDetails : curTasks
                    .entrySet()) {
                    double throughput = taskDetails.getValue();
                    ExecutorDetails task = taskDetails.getKey();
                    LOG.info("throughput:" + throughput + " best:"
                        + bestThroughput);

                    if (throughput > bestThroughput
                        && (migratedThroughput + throughput) < throughputToMigrate) {
                        bestThroughput = throughput;
                        bestTask = task;
                    }
                }

                if (bestThroughput == 0.0) {
                    continue;
                }

                migratedThroughput += bestThroughput;
                tasksToMigrate.add(bestTask);
            }

            /* Nothing to migrate */
            if (tasksToMigrate.size() == 0) {
                return;
            }

            /* Migrate to new supervisors */
            for (SupervisorDetails clusterSupervisor : clusterSupervisors) {
                String hostname = clusterSupervisor.getId();
                Supervisor supervisor = supervisors.get(hostname);

                /* It has been scheduled previously */
                if (supervisor != null) {
                    continue;
                }

                List<WorkerSlot> availableSlots = cluster
                    .getAvailableSlots(clusterSupervisor);
                int numSlots = availableSlots.size();

                LOG.info("tasksToMigrate" + tasksToMigrate);

                for (int i = 0; i < numSlots; i++) {
                    WorkerSlot slot = availableSlots.get(i);
                    List<ExecutorDetails> tasks = tasksToMigrate.subList(i
                        / numSlots * tasksToMigrate.size(), (i + 1) / numSlots
                        * tasksToMigrate.size());

                    migrateTasks(topologyDetails, cluster, tasks, slot);
                }
            }
        }
    }

    public double computeAvgThroughput(HashMap<String, Supervisor> supervisors) {
        double throughput = 0.0;

        for (Supervisor supervisor : supervisors.values()) {
            throughput += supervisor.computeThroughput();
        }

        return throughput / supervisors.size();
    }

    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
        ArrayList<String> topologyToDelete = new ArrayList<String>();
        int numSupervisor = cluster.getSupervisors().size();

        readTopology();
        updateMetrics();

        // if (_numMachines != numSupervisor) {
        // System.out.println("Old supervisor: " + _numMachines + " new: "
        // + numSupervisor);
        // _numMachines = numSupervisor;
        //
        // _schedule(topologies, cluster);
        // }
        //
        _schedule(topologies, cluster);

        // for (Entry<String, Topology> topoEntry : _topologies.entrySet()) {
        // TopologyDetails topology = topologies.getByName(topoEntry.getKey());
        //
        // // Topology doesn't exist anymore -> remove from database, else
        // // schedule
        // if (topology == null) {
        // topologyToDelete.add(topoEntry.getKey());
        // } else {
        // boolean needsScheduling = cluster.needsScheduling(topology);
        //
        // if (!needsScheduling) {
        // Log.info("Our topology DOES NOT NEED scheduling.");
        // } else {
        // /* Default scheduling for now */
        // printCurTopology(cluster, topology);
        // }
        // }
        // }

        /* Delete non-existant topologies */
        for (String s : topologyToDelete) {
            _topologies.remove(s);
        }

        // Use EvenScheduler for the rest
        new EvenScheduler().schedule(topologies, cluster);
        // testSchedule(topologies, cluster);
        // testMigrate(topologies, cluster);

    }

    public void updateMetrics() {
        while (this.metrics_server.MsgQueue.size() > 0) {
            String data = this.metrics_server.getMsg();
            String[] metrics = data.split(",");

            String[] id_tokens = metrics[0].split(":");

            String host = id_tokens[0];
            String port = id_tokens[1];
            String component_id = id_tokens[2];
            String topology_id_full = id_tokens[3];
            String topology_id = id_tokens[3].split("-")[0];
            String task_id = id_tokens[4];

            if (_topologies.get(topology_id) == null) {
                System.out.println("ERROR: this topology does not exist!!!!");
                return;

            } else {
                Node node = _topologies.get(topology_id).getNode(component_id);
                if (node == null) {
                    System.out.println("ERROR: this component " + component_id
                        + " does not exist!!!!");
                    return;

                } else {
                    if (node.containsTask(task_id) == true) {
                        double prevThroughput = node
                            .getTaskAccumThroughput(task_id);
                        node.setTaskThroughput(task_id,
                            Double.parseDouble(metrics[1]) - prevThroughput);
                        node.setTaskAccumThroughput(task_id,
                            Double.parseDouble(metrics[1]));
                    } else {
                        node.addTask(task_id, Double.parseDouble(metrics[1]));

                    }

                    node.CalculateOverallThroughput();

                    LOG.info("Updated metric: " + host + ',' + port + ','
                        + component_id + ',' + task_id + ','
                        + node.getTaskThroughput(task_id) + ','
                        + node.getTaskAccumThroughput(task_id));
                }
            }
        }
    }

    public void testMigrate(Topologies topologies, Cluster cluster) {
        System.out.println("/******start testMigrate********/");
        System.out.println("num of topologies: "
            + Integer.toString(_topologies.size()));
        if (_topologies.size() > 0) {
            for (Entry<String, Topology> topoEntry : _topologies.entrySet()) {
                TopologyDetails topology = topologies.getByName(topoEntry
                    .getKey());
                System.out.println("topology: " + topoEntry.getKey());

                Map<String, List<ExecutorDetails>> componentToExecutors = cluster
                    .getNeedsSchedulingComponentToExecutors(topology);
                List<SupervisorDetails> visors = new ArrayList<SupervisorDetails>();
                Collection<SupervisorDetails> supervisors = cluster
                    .getSupervisors().values();
                SupervisorDetails specialSupervisor = null;
                // System.out.println("Number of supervisors: "
                // + Integer.toString(supervisors.size()));
                for (SupervisorDetails supervisor : supervisors) {
                    Map meta = (Map) supervisor.getSchedulerMeta();
                    visors.add(supervisor);
                }
                List<ExecutorDetails> executors = componentToExecutors
                    .get("special-spout");

                List<WorkerSlot> availableSlots = cluster
                    .getAvailableSlots(visors.get(0));
                if (availableSlots.isEmpty() && !executors.isEmpty()) {
                    for (Integer port : cluster.getUsedPorts(visors.get(0))) {
                        cluster.freeSlot(new WorkerSlot(specialSupervisor
                            .getId(), port));
                    }
                }
                SchedulerAssignment currentAssignment = cluster
                    .getAssignmentById(topologies.getByName(topoEntry.getKey())
                        .getId());
                System.out.println("executors for spout_0"
                    + currentAssignment.getExecutors());
                Iterator iter = currentAssignment.getExecutors().iterator();
                ExecutorDetails e = (ExecutorDetails) iter.next();

                removeTask(topology, cluster, e);

                List<ExecutorDetails> task = new ArrayList<ExecutorDetails>();
                task.add(e);

                if (visors.size() > 1) {
                    List<WorkerSlot> slots1 = cluster.getAvailableSlots(visors
                        .get(0));
                    List<WorkerSlot> slots2 = cluster.getAvailableSlots(visors
                        .get(1));
                    if (e.getStartTask() % 2 == 0) {
                        cluster.assign(slots2.get(0), topology.getId(), task);
                        System.out.println("We assigned executors:" + task
                            + " to slot: [" + slots2.get(0).getNodeId() + ", "
                            + slots2.get(0).getPort() + "]");
                    } else {
                        cluster.assign(slots1.get(0), topology.getId(), task);
                        System.out.println("We assigned executors:" + task
                            + " to slot: [" + slots1.get(0).getNodeId() + ", "
                            + slots1.get(0).getPort() + "]");
                    }

                }
            }

        }
        System.out.println("/******end testMigrate********/");
    }

    public void migrateTasks(TopologyDetails topology, Cluster cluster,
        List<ExecutorDetails> executors, WorkerSlot slot) {
        String topologyId = topology.getId();
        SchedulerAssignment currentAssignment = cluster
            .getAssignmentById(topologyId);
        Map<ExecutorDetails, WorkerSlot> c = currentAssignment
            .getExecutorToSlot();

        for (ExecutorDetails executor : executors) {
            c.remove(executor);
        }

        cluster.assign(slot, topologyId, executors);
        LOG.info("Migrating executors: " + executors + " to slot: " + slot);
    }

    public void removeTask(TopologyDetails topology, Cluster cluster,
        ExecutorDetails executor) {
        System.out.println("/******start removeTask********/");
        System.out.println("task to remove: " + executor);
        SchedulerAssignment currentAssignment = cluster
            .getAssignmentById(topology.getId());
        // this.executorToSlot.remove(executor);
        Map<ExecutorDetails, WorkerSlot> c = currentAssignment
            .getExecutorToSlot();
        c.remove(executor);
        System.out.println("/******end removeTask********/");

    }

    public void testSchedule(Topologies topologies, Cluster cluster) {
        for (Entry<String, Topology> topoEntry : _topologies.entrySet()) {
            TopologyDetails topology = topologies.getByName(topoEntry.getKey());
            if (topology != null) {
                List<ExecutorDetails> oddTasks = new ArrayList<ExecutorDetails>();
                List<ExecutorDetails> evenTasks = new ArrayList<ExecutorDetails>();

                Map<String, List<ExecutorDetails>> componentToExecutors = cluster
                    .getNeedsSchedulingComponentToExecutors(topology);

                Iterator it = componentToExecutors.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    // System.out.println("Key: "+pairs.getKey()+"\nValues:");
                    List<ExecutorDetails> execList = (List<ExecutorDetails>) pairs
                        .getValue();
                    for (int i = 0; i < execList.size(); i++) {
                        // System.out.println(execList.get(i));
                        // System.out.println("start Task: "+Integer.toString(execList.get(i).getStartTask())+" end Task: "+Integer.toString(execList.get(i).getEndTask())+" String: "+execList.get(i).toString());

                        if (execList.get(i).getStartTask() % 2 == 0) {
                            evenTasks.add(execList.get(i));
                        } else {
                            oddTasks.add(execList.get(i));
                        }
                    }

                }

                // Log.info("needs scheduling(component->executor): "+
                // componentToExecutors);
                // Log.info("needs scheduling(executor->components): "+
                // cluster.getNeedsSchedulingExecutorToComponents(topology));
                SchedulerAssignment currentAssignment = cluster
                    .getAssignmentById(topologies.getByName(topoEntry.getKey())
                        .getId());
                if (currentAssignment != null) {
                    Log.info("current assignments: "
                        + currentAssignment.getExecutorToSlot());
                } else {
                    Log.info("current assignments: {}");
                }

                List<SupervisorDetails> visors = new ArrayList<SupervisorDetails>();
                Collection<SupervisorDetails> supervisors = cluster
                    .getSupervisors().values();
                SupervisorDetails specialSupervisor = null;
                // System.out.println("Number of supervisors: "
                // + Integer.toString(supervisors.size()));
                for (SupervisorDetails supervisor : supervisors) {
                    Map meta = (Map) supervisor.getSchedulerMeta();
                    // System.out.println("Supervisor name: " + supervisor +
                    // "\n");
                    // System.out.println("id: " + supervisor.getId() +
                    // " host: "
                    // + supervisor.getHost());
                    visors.add(supervisor);
                }
                if (visors.size() > 1) {
                    List<WorkerSlot> slots1 = cluster.getAvailableSlots(visors
                        .get(0));
                    List<WorkerSlot> slots2 = cluster.getAvailableSlots(visors
                        .get(1));
                    cluster.assign(slots1.get(0), topology.getId(), evenTasks);
                    System.out.println("We assigned executors:" + evenTasks
                        + " to slot: [" + slots1.get(0).getNodeId() + ", "
                        + slots1.get(0).getPort() + "]");
                    cluster.assign(slots2.get(0), topology.getId(), oddTasks);
                    System.out.println("We assigned executors:" + oddTasks
                        + " to slot: [" + slots2.get(0).getNodeId() + ", "
                        + slots2.get(0).getPort() + "]");
                }

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
                HashMap<String, Node> nodes = new HashMap<String, Node>();
                String topologyName = topologyFiles[i].getName();

                if (_topologies.containsKey(topologyName)
                    || topologyName.startsWith(".")) {
                    continue;
                }

                try {
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        String _line[] = line.split(",");
                        Node parent;
                        Node child;

                        if (!nodes.containsKey(_line[0])) {
                            parent = new Node(_line[0]);
                            nodes.put(_line[0], parent);
                        } else {
                            parent = nodes.get(_line[0]);
                        }

                        if (!nodes.containsKey(_line[1])) {
                            child = new Node(_line[1]);
                            nodes.put(_line[1], child);
                        } else {
                            child = nodes.get(_line[1]);
                        }

                        LOG.info("Adding parent:" + _line[0] + " child: "
                            + _line[1]);
                        parent.addChildren(child);
                    }

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Topology newTopology = new Topology(topologyName, nodes);

                LOG.info("Added new topology: " + topologyName);
                _topologies.put(topologyName, newTopology);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
