 
package com.bigdata.storm.redis.topolgy.save;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.ITuple;

import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

import com.bigdata.storm.redis.topolgy.search.WordSpout;


public class PersistentWordCountTopoy {
    private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String COUNT_BOLT = "COUNT_BOLT";
    private static final String STORE_BOLT = "STORE_BOLT";

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
       
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                .setHost(host).setPort(port).build();

        
        WordSpout spout = new WordSpout();
        
        WordCounterBolt bolt = new WordCounterBolt();
        
        RedisStoreMapper storeMapper = new WordStoreMapper();
         
        RedisStoreBolt storeBolt = new RedisStoreBolt(poolConfig, storeMapper);

        // wordSpout ==> countBolt ==> RedisBolt
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(WORD_SPOUT, spout, 1);
        builder.setBolt(COUNT_BOLT, bolt, 1).fieldsGrouping(WORD_SPOUT, new Fields("word"));
        builder.setBolt(STORE_BOLT, storeBolt, 1).shuffleGrouping(COUNT_BOLT);

        if (args.length == 2) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, builder.createTopology());
            Thread.sleep(30000);
            cluster.killTopology("test");
            cluster.shutdown();
            System.exit(0);
        } else if (args.length == 3) {
            StormSubmitter.submitTopology(args[2], config, builder.createTopology());
        } else {
            System.out.println("Usage: PersistentWordCount <redis host> <redis port> (topology name)");
        }
    }

    

   
}
