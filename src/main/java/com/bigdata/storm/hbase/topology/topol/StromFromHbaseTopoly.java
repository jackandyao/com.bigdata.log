 
package com.bigdata.storm.hbase.topology.topol;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import org.apache.storm.hbase.bolt.HBaseLookupBolt;
import org.apache.storm.hbase.bolt.mapper.HBaseProjectionCriteria;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;

import com.bigdata.storm.hbase.topology.bolt.TotalWordCounterBolt;
import com.bigdata.storm.hbase.topology.mapper.WordCountValueMapper;
import com.bigdata.storm.hbase.topology.spout.WordSpout;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能:从hbase中查询数据 然后写入到storm集群中
 * @author 贾红平
 *
 */
public class StromFromHbaseTopoly {
	
    private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
    private static final String TOTAL_COUNT_BOLT = "TOTAL_COUNT_BOLT";

    public static void main(String[] args) throws Exception {
        Config config = new Config();

        Map<String, Object> hbConf = new HashMap<String, Object>();
        if(args.length > 0){
            hbConf.put("hbase.rootdir", args[0]);
        }
        config.put("hbase.conf", hbConf);

        WordSpout spout = new WordSpout();
        
        TotalWordCounterBolt totalBolt = new TotalWordCounterBolt();
        
        //指定hbase的行键
        SimpleHBaseMapper mapper = new SimpleHBaseMapper().withRowKeyField("word");
        //创建hbase查询函数对象
        HBaseProjectionCriteria projectionCriteria = new HBaseProjectionCriteria();
        
        projectionCriteria.addColumn(new HBaseProjectionCriteria.ColumnMetaData("cf", "count"));
        //实现把hbase查询出来的值 
        WordCountValueMapper rowToTupleMapper = new WordCountValueMapper();
        //实现具体如何返回hbase中的列的相关信息
        HBaseLookupBolt hBaseLookupBolt = new HBaseLookupBolt("WordCount", mapper, rowToTupleMapper)
                .withConfigKey("hbase.conf")
                .withProjectionCriteria(projectionCriteria);

        //wordspout -> lookupbolt -> totalCountBolt
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(WORD_SPOUT, spout, 1);
        builder.setBolt(LOOKUP_BOLT, hBaseLookupBolt, 1).shuffleGrouping(WORD_SPOUT);
        builder.setBolt(TOTAL_COUNT_BOLT, totalBolt, 1).fieldsGrouping(LOOKUP_BOLT, new Fields("columnName"));

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
            System.out.println("Usage: LookupWordCount <hbase.rootdir>");
        }
    }
}
