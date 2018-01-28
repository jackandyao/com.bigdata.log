 
package com.bigdata.storm.redis.topolgy.search;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.storm.redis.bolt.RedisLookupBolt;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
 
/**
 * ��redis���ݲ�ѯ���� Ȼ���䵽storm ����tuple����
 * @author �ֺ�ƽ
 *
 */
public class LookupWordCountTopy {
    private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
    private static final String PRINT_BOLT = "PRINT_BOLT";

    private static final String TEST_REDIS_HOST = "127.0.0.1";
    private static final int TEST_REDIS_PORT = 6379;

    
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        
        
        String host = TEST_REDIS_HOST;
        int port = TEST_REDIS_PORT;

        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        //����redis����
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost(host).setPort(port).build();
        //redis��Ⱥ����
        String redisHostPort="192.168.1.10:6393,192.168.1.11:6393,192.168.1.12:6393";
        Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
        for (String hostPort : redisHostPort.split(",")) {
            String[] host_port = hostPort.split(":");
            nodes.add(new InetSocketAddress(host_port[0], Integer.valueOf(host_port[1])));
        }
        JedisClusterConfig clusterConfig = new JedisClusterConfig.Builder().setNodes(nodes)
                                        .build();
        //������͵��ε�spout
        WordSpout spout = new WordSpout();
        //redis����ת��Ϊtuple
        RedisLookupMapper lookupMapper = new WordLookupMapper();
        RedisLookupBolt lookupBolt=null;
        //��������
        lookupBolt= new RedisLookupBolt(poolConfig, lookupMapper);
        //��Ⱥ����
        lookupBolt = new RedisLookupBolt(poolConfig, lookupMapper);
        //��ӡ���
        PrintWordTotalCountBolt printBolt = new PrintWordTotalCountBolt();

        //wordspout -> lookupbolt
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(WORD_SPOUT, spout, 1);
        builder.setBolt(LOOKUP_BOLT, lookupBolt, 1).shuffleGrouping(WORD_SPOUT);
        builder.setBolt(PRINT_BOLT, printBolt, 1).shuffleGrouping(LOOKUP_BOLT);

        if (args.length == 2) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, builder.createTopology());
            Thread.sleep(30000);
            cluster.killTopology("test");
            cluster.shutdown();
            System.exit(0);
        } else if (args.length == 3) {
            StormSubmitter.submitTopology(args[2], config, builder.createTopology());
        } else{
            System.out.println("Usage: LookupWordCount <redis host> <redis port> (topology name)");
        }
    }
}
