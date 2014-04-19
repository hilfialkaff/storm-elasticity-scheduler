package storm.scheduler;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorInfo;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;

public class StatisticTest {
    public static void main(String[] args) {
        TSocket tsocket = new TSocket("localhost", 6627);
        TFramedTransport tTransport = new TFramedTransport(tsocket);
        TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
        Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);

        try {
            tTransport.open();
            ClusterSummary clusterSummary = client.getClusterInfo();

            List<TopologySummary> topologies = clusterSummary.getTopologies();
            for (TopologySummary topo : topologies) {
                TopologyInfo topologyInfo = client
                    .getTopologyInfo(topo.getId());
                List<ExecutorSummary> executorSummaries = topologyInfo
                    .getExecutors();

                for (ExecutorSummary executorSummary : executorSummaries) {
                    String id = executorSummary.getHost();
                    ExecutorInfo executorInfo = executorSummary
                        .getExecutor_info();
                    ExecutorStats executorStats = executorSummary.getStats();
                    System.out.println("executorSummary :: " + id
                        + " emit size :: " + executorStats.getEmittedSize());
                }
            }
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
