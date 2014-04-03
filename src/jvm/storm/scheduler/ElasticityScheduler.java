package storm.scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private final String TOPOLOGY_NAME = "TestTopology";
    private static final Logger LOG = LoggerFactory
        .getLogger(ElasticityScheduler.class);
    private static final String TOPOLOGY_FILE = "./topology.txt";

    @Override
    public void prepare(Map conf) {
    }

    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
        // Gets the topology which we want to schedule
        TopologyDetails topology = topologies.getByName(TOPOLOGY_NAME);

        // make sure the special topology is submitted,
        if (topology != null) {
            boolean needsScheduling = cluster.needsScheduling(topology);

            if (!needsScheduling) {
                Log.info("Our special topology DOES NOT NEED scheduling.");
            } else {
                System.out.println("Our special topology needs scheduling.");
                // find out all the needs-scheduling components of this topology
                Map<String, List<ExecutorDetails>> componentToExecutors = cluster
                    .getNeedsSchedulingComponentToExecutors(topology);

                Log.info("needs scheduling(component->executor): "
                    + componentToExecutors);
                Log.info("needs scheduling(executor->components): "
                    + cluster.getNeedsSchedulingExecutorToComponents(topology));
                SchedulerAssignment currentAssignment = cluster
                    .getAssignmentById(topologies.getByName(TOPOLOGY_NAME)
                        .getId());
                if (currentAssignment != null) {
                    Log.info("current assignments: "
                        + currentAssignment.getExecutorToSlot());
                } else {
                    Log.info("current assignments: {}");
                }

                Collection<SupervisorDetails> supervisors = cluster
                    .getSupervisors().values();
                for (SupervisorDetails supervisor : supervisors) {
                    Map meta = (Map) supervisor.getSchedulerMeta();
                    LOG.info("Supervisor name: " + meta.get("name").toString());

                    List<WorkerSlot> availableSlots = cluster
                        .getAvailableSlots(supervisor);
                    for (WorkerSlot slot : availableSlots) {
                        LOG.info("Slot: " + slot.getPort());
                    }
                }
            }
        }

        // Use EvenScheduler for the rest
        new EvenScheduler().schedule(topologies, cluster);
    }

    public void read_topology() throws Exception {
        Node root = new Node("root");

        FileReader file = new FileReader(TOPOLOGY_FILE);
        BufferedReader reader = new BufferedReader(file);
        String line = null;

        while ((line = reader.readLine()) != null) {
            String _line[] = line.split(",");
            Node parent = new Node(_line[0]);
            Node child = new Node(_line[1]);

            parent.addChildren(child);
            root.addChildren(parent);
            root.addChildren(child);
        }

        reader.close();
    }

    public void main(String[] args) throws Exception {
        read_topology();
    }
}
