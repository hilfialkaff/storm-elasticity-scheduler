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
import java.io.*;
/*
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
*/


import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
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
    private static final Logger LOG = LoggerFactory.getLogger(ElasticityScheduler.class);
    private static final String ROOT_NODE = "root";
    private static final String TOPOLOGY_DIR = "/proj/CS525/exp/stormCluster/project/storm-experiments/topologies/";
    private static final String NIMBUS_HOST = "node-1.stormcluster.cs525.emulab.net";
    private static final String INTERVAL = "600";
    private static final int METRICS_RECV_PORT = 9005;
    private static final String ELASTICITY_SCHEDULER_DIR = "/proj/CS525/exp/stormCluster/project/storm-elasticity-scheduler";

  //  private TSocket _tsocket;
   // private TFramedTransport _tTransport;
  //  private TBinaryProtocol _tBinaryProtocol;
    private Nimbus.Client _client;
    private static HashMap<String, Topology> _topologies = new HashMap<String, Topology>();
    private static int _numMachines = 0;
    private server metrics_server;
    private Process theProcess;

    @Override
    public void prepare(Map conf) {
        /*
         * _tsocket = new TSocket("localhost", 6627); _tTransport = new
         * TFramedTransport(_tsocket); _tBinaryProtocol = new
         * TBinaryProtocol(_tTransport); _client = new
         * Nimbus.Client(_tBinaryProtocol);
         * 
         * try { _tTransport.open(); } catch (TException e) {
         * e.printStackTrace(); }
         */
    	
    	this.metrics_server = new server(METRICS_RECV_PORT);
		this.metrics_server.start();
		System.out.println("Server Successfully started!!");
		
		Process theProcess = null;
	      BufferedReader inStream = null;
	     
	      /*
		 try
	      {
			 String command = "java -cp "+ELASTICITY_SCHEDULER_DIR+"/target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies.jar storm.scheduler.ThriftMetrics";
			System.out.println("Starting Metrics client routine..."); 
			this.theProcess = Runtime.getRuntime().exec("java -cp "+ELASTICITY_SCHEDULER_DIR+"/target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies.jar storm.scheduler.ThriftMetrics");
			System.out.println("Metrics client routine started...");
	          //java -cp target/storm_elasticity_thrift-1.0.0-SNAPSHOT-jar-with-dependencies.jar storm.scheduler.ThriftMetrics
	      }
	      catch(IOException e)
	      {
	         System.err.println("Error on exec() method");
	         e.printStackTrace();  
	      }
		 
		 try
	      {
	         inStream = new BufferedReader(new InputStreamReader( this.theProcess.getInputStream() ));  
	         System.out.println(inStream.readLine());
	      }
	      catch(IOException e)
	      {
	         System.err.println("Error on inStream.readLine()");
	         e.printStackTrace();  
	      }
		*/
		
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

    public void _schedule(Topologies topologies, Cluster cluster) {
    }
    
    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
    	//System.out.println("IN schedule!!!");
    	
        ArrayList<String> topologyToDelete = new ArrayList<String>();
        int numSupervisor = cluster.getSupervisors().size();
        
         updateMetrics();
         readTopology();

        if (_numMachines != numSupervisor) {
            System.out.println("Old supervisor: " + _numMachines + " new: "
                + numSupervisor);
            _numMachines = numSupervisor;

            _schedule(topologies, cluster);
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
                    /* Default scheduling for now */
                    printCurTopology(cluster, topology);
                }
            }
        }

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
    	
    	
    	while(this.metrics_server.MsgQueue.size()>0)
    	{
    		String data = this.metrics_server.getMsg();
    		String[] metrics = data.split(",");
    		System.out.println("ID: "+metrics[0] + " Metric: "+metrics[1]);
    		//Node node = topology.getNode(transferPair.getKey());
            //node.setThroughput(transferPair.getValue()
                //.get(INTERVAL));
    	}
/*
        try {
            ClusterSummary clusterSummary = _client.getClusterInfo();
            List<TopologySummary> topologies = clusterSummary.getTopologies();

            for (TopologySummary topo : topologies) {
                TopologyInfo topologyInfo = _client.getTopologyInfo(topo
                    .getId());
                List<ExecutorSummary> executorSummaries = topologyInfo
                    .getExecutors();
                Topology topology = _topologies.get(topo.getName());

                for (ExecutorSummary executorSummary : executorSummaries) {
                    ExecutorStats executorStats = executorSummary.getStats();
                    String componentId = executorSummary.getComponent_id();
                    Map<String, Map<String, Long>> transfer = executorStats
                        .getTransferred();

                    if (componentId.startsWith("__")) {
                        continue;
                    }

                    for (Entry<String, Map<String, Long>> transferPair : transfer
                        .entrySet()) {
                        Node node = topology.getNode(transferPair.getKey());
                        node.setThroughput(transferPair.getValue()
                            .get(INTERVAL));

                        LOG.info("Setting node: " + node.getName()
                            + " throughput to :" + node.getThroughput());
                    }
                }
            }
        } catch (TException e) {
            e.printStackTrace();
        }
*/
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
                    // System.out.println("Supervisor name: " + supervisor +
                    // "\n");
                    // System.out.println("id: " + supervisor.getId() +
                    // " host: "
                    // + supervisor.getHost());
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
                LOG.info("Exception: " + e.getMessage());
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
                LOG.info("Added new topology: " + topologyName);
                _topologies.put(topologyName, newTopology);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
