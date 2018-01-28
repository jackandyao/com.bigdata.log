
package com.bigdata.storm.hbase.topology.topol;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import org.apache.storm.hbase.bolt.HBaseBolt;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;

import com.bigdata.storm.hbase.topology.bolt.WordCounterBolt;
import com.bigdata.storm.hbase.topology.spout.WordSpout;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现把storm获取到的数据写入到hbase中  
 * 1 首先要在hbase中创建对应的表和列族信息
 * @author 贾红平
 *
 */
public class StromToHbaseTopology {
    private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String COUNT_BOLT = "COUNT_BOLT";
    private static final String HBASE_BOLT = "HBASE_BOLT";


    public static void main(String[] args) throws Exception {
        Config config = new Config();
        //如果hbase开启了安全策略 就需要使用如下配置
        config.put("storm.keytab.file", "$keytab");
        config.put("storm.kerberos.principal", "$principle");
        
        Map<String, Object> hbConf = new HashMap<String, Object>();
        if(args.length > 0){
            hbConf.put("hbase.rootdir", args[0]);
        }
        config.put("hbase.conf", hbConf);

        WordSpout spout = new WordSpout();
        
        WordCounterBolt bolt = new WordCounterBolt();

        //指定hbase中的行和列族信息
        SimpleHBaseMapper mapper = new SimpleHBaseMapper()
                .withRowKeyField("word")
                .withColumnFields(new Fields("word"))
                .withCounterFields(new Fields("count"))
                .withColumnFamily("cf");
        //使用自带的hbase的bolt
        HBaseBolt hbase = new HBaseBolt("WordCount", mapper)
                .withConfigKey("hbase.conf");


        // wordSpout ==> countBolt ==> HBaseBolt
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(WORD_SPOUT, spout, 1);
        builder.setBolt(COUNT_BOLT, bolt, 1).shuffleGrouping(WORD_SPOUT);
        builder.setBolt(HBASE_BOLT, hbase, 1).fieldsGrouping(COUNT_BOLT, new Fields("word"));


        if (args.length == 1) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, builder.createTopology());
            Thread.sleep(30000);
            cluster.killTopology("test");
            cluster.shutdown();
            System.exit(0);
        } else if (args.length == 2) {
            StormSubmitter.submitTopology(args[1], config, builder.createTopology());
        } else{
            System.out.println("Usage: HdfsFileTopology <hdfs url> [topology name]");
        }
    }
}
