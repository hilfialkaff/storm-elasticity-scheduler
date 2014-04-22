package storm.scheduler;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;

public class ThriftMetrics extends Thread{
	private static final int METRICS_SEND_PORT = 9005;
	private static final String METRICS_SEND_HOSTNAME = "localhost";
	
	private TSocket tsocket;
	private Nimbus.Client client;
	TFramedTransport tTransport;
	TBinaryProtocol tBinaryProtocol;
	client metrics_send_client;
	public ThriftMetrics ()
	{
		  this.tsocket = new TSocket("localhost", 6627);
	        this.tTransport = new TFramedTransport(tsocket);
	        this.tBinaryProtocol = new TBinaryProtocol(tTransport);
	       this.client = new Nimbus.Client(tBinaryProtocol);
	       this.metrics_send_client = new client(METRICS_SEND_HOSTNAME, METRICS_SEND_PORT);
	       System.out.println("Starting metrics client...");
	       this.metrics_send_client.start();
		
	}
	public void run()
	{
		  try {
	            this.tTransport.open();

	            while (true) {
	                ClusterSummary clusterSummary = client.getClusterInfo();
	                List<TopologySummary> topologies = clusterSummary
	                    .getTopologies();
	                for (TopologySummary topo : topologies) {
	                    TopologyInfo topologyInfo = client.getTopologyInfo(topo
	                        .getId());
	                    List<ExecutorSummary> executorSummaries = topologyInfo
	                        .getExecutors();

	                    for (ExecutorSummary executorSummary : executorSummaries) {
	                        ExecutorStats executorStats = executorSummary
	                            .getStats();
	                        if (executorStats == null) {
	                            System.out.println("NULL");
	                            continue;
	                        }
	                        String host = executorSummary.getHost();
	                        int port = executorSummary.getPort();
	                        String componentId = executorSummary.getComponent_id();
	                        Map<String, Map<String, Long>> transfer = executorStats
	                            .getTransferred();
	                        
	                        if(transfer.get("600").get("default")!=null)
	                        {
		                        this.metrics_send_client.addMsg(host + ':' + port
		                            + ':' + componentId + "," + transfer.get("600").get("default"));
	                        }
	  /*
	                        System.out.println("executor <" + host + ',' + port
	                            + ',' + componentId + "> transferred: " + transfer.get("600"));
	                            
	                            */
	                    }
	                }

	                Thread.sleep(5000);
	            }
	        } catch (TException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		
		
	}
	public static void main(String[] args) {
        ThriftMetrics m = new ThriftMetrics();
        try{
        m.start();
        m.join();
        }catch(java.lang.InterruptedException e)
        {
        	System.out.println(e);
        	
        }
        /*
        while(true)
        {
        	System.out.println("HERERERERERE");
        	try {
        	Thread.sleep(2000);
        	}
        	catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
        */
        
      

      
    }
}
