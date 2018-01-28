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
package com.bigdata.storm.hbase.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import org.apache.hadoop.hbase.client.Durability;
import org.apache.storm.hbase.bolt.mapper.HBaseProjectionCriteria;
import org.apache.storm.hbase.bolt.mapper.HBaseValueMapper;
import org.apache.storm.hbase.trident.mapper.SimpleTridentHBaseMapper;
import org.apache.storm.hbase.trident.mapper.TridentHBaseMapper;
import org.apache.storm.hbase.trident.state.HBaseQuery;
import org.apache.storm.hbase.trident.state.HBaseState;
import org.apache.storm.hbase.trident.state.HBaseStateFactory;
import org.apache.storm.hbase.trident.state.HBaseUpdater;

import com.bigdata.storm.hbase.topology.mapper.WordCountValueMapper;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;
/**
 * 创建一个事务性的拓扑 把storm的数据写入到hbase 保证数据不重复 不丢失
 * @author 贾红平
 *
 */
public class StormToHbaseTopolgyTrident {
	/**
	 * 创建拓扑
	 * @param hbaseRoot
	 * @return
	 */
    public static StormTopology buildTopology(String hbaseRoot){
        Fields fields = new Fields("word", "count");
        
        FixedBatchSpout spout = new FixedBatchSpout(fields, 4,
                new Values("storm", 1),
                new Values("trident", 1),
                new Values("needs", 1),
                new Values("javadoc", 1)
        );
        
        spout.setCycle(true);
        
        //指定habse的行键和列族
        TridentHBaseMapper tridentHBaseMapper = new SimpleTridentHBaseMapper()
                .withColumnFamily("cf")
                .withColumnFields(new Fields("word"))
                .withCounterFields(new Fields("count"))
                .withRowKeyField("word");
        //storm的数据转换为hbase的数据格式
        HBaseValueMapper rowToStormValueMapper = new WordCountValueMapper();
        //返回hbase特定的列的查询函数
        HBaseProjectionCriteria projectionCriteria = new HBaseProjectionCriteria();
        projectionCriteria.addColumn(new HBaseProjectionCriteria.ColumnMetaData("cf", "count"));
        //设置hbase表写ude一些基本属性
        HBaseState.Options options = new HBaseState.Options()
                .withConfigKey(hbaseRoot)
                .withDurability(Durability.SYNC_WAL)
                .withMapper(tridentHBaseMapper)
                .withProjectionCriteria(projectionCriteria)
                .withRowToStormValueMapper(rowToStormValueMapper)
                .withTableName("WordCount");
        //实例化一个状态工厂(事务性提交)
        StateFactory factory = new HBaseStateFactory(options);
        //创建事务拓扑
        TridentTopology topology = new TridentTopology();
        //必须返回流的数据
        Stream stream = topology.newStream("spout1", spout);
        //持久化
        stream.partitionPersist(factory, fields,  new HBaseUpdater(), new Fields());
        //事务提交状态
        TridentState state = topology.newStaticState(factory);
        //执行具体功能
        stream = stream.stateQuery(state, new Fields("word"), new HBaseQuery(), new Fields("columnName","columnValue"));
        stream.each(new Fields("word","columnValue"), new PrintFunction(), new Fields());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        conf.setMaxSpoutPending(5);
        if (args.length == 1) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wordCounter", conf, buildTopology(args[0]));
            Thread.sleep(60 * 1000);
            cluster.killTopology("wordCounter");
            cluster.shutdown();
            System.exit(0);
        }
        else if(args.length == 2) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[1], conf, buildTopology(args[0]));
        } else{
            System.out.println("Usage: TridentFileTopology <hdfs url> [topology name]");
        }
    }

}
