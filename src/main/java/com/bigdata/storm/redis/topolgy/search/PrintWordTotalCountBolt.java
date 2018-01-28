package com.bigdata.storm.redis.topolgy.search;

import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public  class PrintWordTotalCountBolt extends BaseRichBolt {
        private static final Logger LOG = LoggerFactory.getLogger(PrintWordTotalCountBolt.class);
        private static final Random RANDOM = new Random();
        private OutputCollector collector;

        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        public void execute(Tuple input) {
            String wordName = input.getStringByField("wordName");
            String countStr = input.getStringByField("count");

            // print lookup result with low probability
            if(RANDOM.nextInt(1000) > 995) {
                int count = 0;
                if (countStr != null) {
                    count = Integer.parseInt(countStr);
                }
                LOG.info("Lookup result - word : " + wordName + " / count : " + count);
            }

            collector.ack(input);
        }

        public void declareOutputFields(OutputFieldsDeclarer declarer) {
        }
    }
