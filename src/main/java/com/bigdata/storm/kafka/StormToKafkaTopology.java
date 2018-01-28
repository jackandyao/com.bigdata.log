package com.bigdata.storm.kafka;

import java.util.UUID;
import storm.kafka.Broker;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StaticHosts;
import storm.kafka.ZkHosts;
import storm.kafka.bolt.KafkaBolt;
import storm.kafka.trident.GlobalPartitionInformation;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

public class StormToKafkaTopology {
	public StormTopology createStormTopoly(){
		
		TopologyBuilder build=new TopologyBuilder();
		
		 
		BrokerHosts hosts=null;
		
	    String brokerZkStr="10.1.80.249:2181,10.1.80.250:2181,10.1.80.251:2181";
	    String brokerZkPath="/kafka";
	    
	   
	    hosts=new ZkHosts(brokerZkStr, brokerZkPath);
		
	    GlobalPartitionInformation partitionInfo = new GlobalPartitionInformation();
	    Broker brokerForPartition0 = new Broker("localhost");//localhost:9092
	    Broker brokerForPartition1 = new Broker("localhost", 9092);//localhost:9092 but we specified the port explicitly
	    Broker brokerForPartition2 = new Broker("localhost:9092");//localhost:9092 specified as one string.
	    partitionInfo.addPartition(0, brokerForPartition0);//mapping from partition 0 to brokerForPartition0
	    partitionInfo.addPartition(1, brokerForPartition1);//mapping from partition 1 to brokerForPartition1
	    partitionInfo.addPartition(2, brokerForPartition2);//mapping from partition 2 to brokerForPartition2
		 
	    hosts=new StaticHosts(partitionInfo);
		
	    
		String topic="";
		 
		String zkRoot="/"+topic;
		//��Ϣid
		String id=UUID.randomUUID().toString();
		
		//ʵ��һ��spout��һ����kafka����
		SpoutConfig config=new SpoutConfig(hosts, topic, zkRoot, id);
		
		//ʵ��kafkaspout����kafka���͹�������Ϣ
		KafkaSpout kafkaSpout=new KafkaSpout(config);
		
		//���kafka���͹�������Ϣ:һ�㵽�ⲽ ����ʵ���Զ����bolt ��Ϊ��ʱ�����ǻ�����д�뵽����Ŀ�ĵ�(����mysql,redis...)
		KafkaBolt kafkaBolt=new KafkaBolt();
		
		
		
		return build.createTopology();
	}
	public static void main(String[] args) {
		
	}
	 
}
