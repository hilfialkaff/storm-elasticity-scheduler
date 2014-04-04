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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Integer;
import java.util.*;
import java.util.ArrayList;
import java.io.*;
import java.awt.Point;

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
	/**
	String[] cmd = { "/users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/getMetrics.sh peng 10 /var/storm/storm-0.9.1/logs/metrics.log /users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/metrics 2 /" };
	try{
        	Process p = Runtime.getRuntime().exec(cmd);
	}
	catch (IOException e){ 
		System.err.println("Caught IOException: " + e.getMessage());
	}

	**/
	try {
            Process proc = Runtime.getRuntime().exec("/users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/getMetrics.sh peng 10 /var/storm/storm-0.9.1/logs/metrics.log /users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/metrics 2 /"); //Whatever you want to execute
          
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
        
	//get metrics
	parse_data("/users/peng/storm-elasticity-scheduler/src/jvm/storm/scheduler/metrics/metrics.log");
	
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
                    //LOG.info("Supervisor name: " + meta.get("name").toString());

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
    
    //Parse metric data	
    public void parse_data(String metric_file){
	FileInputStream fis=null;
	try{	
        	fis = new FileInputStream(metric_file);
	}
	catch(FileNotFoundException e){
		System.err.println("Caught FileNotFoundException: " + e.getMessage());
		return;
	}
        Scanner scanner = new Scanner(fis);
	Map<String, Double> occurrence = new HashMap<String, Double>();
	Map<String, Double> numExecute = new HashMap<String, Double>();
        while(scanner.hasNextLine()){
            //System.out.println(scanner.nextLine());
				String[] splited = scanner.nextLine().split("\\s+");
				for (int i = 0; i < splited.length; i++){
					//System.out.println(Integer.toString(i)+"---"+splited[i]);
				}
				if(occurrence.containsKey(splited[5])==false)
				{
					occurrence.put(splited[5], 0.0);
					numExecute.put(splited[5], 0.0);
				}
				numExecute.put(splited[5], numExecute.get(splited[5])+Integer.parseInt(splited[7]));
				occurrence.put(splited[5], occurrence.get(splited[5])+1);
				//System.out.println(map.get(splited[5]));
        }
	//System.out.println("/*****occurrence*******/");
	//printMap(occurrence);
	//System.out.println("/*****numExecute*******/");
	//printMap(numExecute);
	System.out.println("/*****avg*******/");
	getAverage(occurrence, numExecute);

      
        scanner.close();
   }

	public static void printMap(Map mp) {
		Iterator it = mp.entrySet().iterator();
    		while (it.hasNext()) {
        			Map.Entry pairs = (Map.Entry)it.next();
        			System.out.println(pairs.getKey() + " = " + pairs.getValue());

    		}
	}

	public static void getAverage(Map occurrence, Map numExecute) {
		Iterator it = numExecute.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String)entry.getKey();
			double val = (Double)entry.getValue();

			double avg = (val) / (Double)occurrence.get(key);
			System.out.println(key + " avg: " + avg);
		}
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
