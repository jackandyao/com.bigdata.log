/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bigdata.storm.redis.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.redis.trident.state.RedisState;
import org.apache.storm.redis.trident.state.RedisStateQuerier;
import org.apache.storm.redis.trident.state.RedisStateUpdater;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;

public class WordCountTridentRedis {
    public static StormTopology buildTopology(String redisHost, Integer redisPort){
       
    	//发送的字段
    	Fields fields = new Fields("word", "count");
       //发送数据的bolt
        FixedBatchSpout spout = new FixedBatchSpout(fields, 4,
                new Values("storm", 1),
                new Values("trident", 1),
                new Values("needs", 1),
                new Values("javadoc", 1)
        );
        //设置是循环发送
        spout.setCycle(true);
        //reids服务配置
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                                        .setHost(redisHost).setPort(redisPort)
                                        .build();
        //tuple数据转换为redis
        RedisStoreMapper storeMapper = new WordCountStoreMapper();
        //redis数据转换为tuple
        RedisLookupMapper lookupMapper = new WordCountLookupMapper();
        //执行trident
        RedisState.Factory factory = new RedisState.Factory(poolConfig);

        TridentTopology topology = new TridentTopology();
        //返回的流
        Stream stream = topology.newStream("spout1", spout);
        //提供保存操作
        stream.partitionPersist(factory,
                                fields,
                                new RedisStateUpdater(storeMapper).withExpire(86400000),
                                new Fields());

        //提供查询操作
        TridentState state = topology.newStaticState(factory);
        
        stream = stream.stateQuery(state, new Fields("word"),
                                new RedisStateQuerier(lookupMapper),
                                new Fields("columnName","columnValue"));
        stream.each(new Fields("word","columnValue"), new PrintFunction(), new Fields());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: WordCountTrident 0(storm-local)|1(storm-cluster) redis-host redis-port");
            System.exit(1);
        }

        Integer flag = Integer.valueOf(args[0]);
        String redisHost = args[1];
        Integer redisPort = Integer.valueOf(args[2]);

        Config conf = new Config();
        conf.setMaxSpoutPending(5);
        if (flag == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test_wordCounter_for_redis", conf, buildTopology(redisHost, redisPort));
            Thread.sleep(60 * 1000);
            cluster.killTopology("test_wordCounter_for_redis");
            cluster.shutdown();
            System.exit(0);
        } else if(flag == 1) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology("test_wordCounter_for_redis", conf, buildTopology(redisHost, redisPort));
        } else {
            System.out.println("Usage: WordCountTrident 0(storm-local)|1(storm-cluster) redis-host redis-port");
        }
    }
}
