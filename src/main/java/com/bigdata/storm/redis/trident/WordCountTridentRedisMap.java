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
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.trident.state.RedisMapState;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;

public class WordCountTridentRedisMap {
    public static StormTopology buildTopology(String redisHost, Integer redisPort){
        Fields fields = new Fields("word", "count");
        FixedBatchSpout spout = new FixedBatchSpout(fields, 4,
                new Values("storm", 1),
                new Values("trident", 1),
                new Values("needs", 1),
                new Values("javadoc", 1)
        );
        spout.setCycle(true);

        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
                                        .setHost(redisHost).setPort(redisPort)
                                        .build();

        //指定redis的数据格式是map结构
        RedisDataTypeDescription dataTypeDescription = new RedisDataTypeDescription(
                RedisDataTypeDescription.RedisDataType.HASH, "test");
       
        //执行trident
        StateFactory factory = RedisMapState.transactional(poolConfig, dataTypeDescription);

        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("spout1", spout);

        //分组保存
        TridentState state = stream.groupBy(new Fields("word"))
                .persistentAggregate(factory, new Fields("count"), new Sum(), new Fields("sum"));
        //分组查询
        stream.stateQuery(state, new Fields("word"), new MapGet(), new Fields("sum"))
                .each(new Fields("word", "sum"), new PrintFunction(), new Fields());
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
