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

public class ThriftExample {
    public static void main(String[] args) {
        TSocket tsocket = new TSocket("localhost", 6627);
        TFramedTransport tTransport = new TFramedTransport(tsocket);
        TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
        Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);

        try {
            tTransport.open();

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
  
                        System.out.println("executor <" + host + ',' + port
                            + ',' + componentId + "> transferred: " + transfer.get("600"));
                    }
                }

                Thread.sleep(2000);
            }
        } catch (TException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
