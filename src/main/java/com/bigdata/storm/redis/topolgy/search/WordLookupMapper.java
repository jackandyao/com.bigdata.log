package com.bigdata.storm.redis.topolgy.search;

import java.util.List;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisLookupMapper;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Values;

import com.google.common.collect.Lists;
 public  class WordLookupMapper implements RedisLookupMapper {
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	    //redis��ݽṹ����
		private RedisDataTypeDescription description;
        private final String hashKey = "wordCount";

        public WordLookupMapper() {
            description = new RedisDataTypeDescription(
                    RedisDataTypeDescription.RedisDataType.HASH, hashKey);
        }

        /**
         * redis���ת��Ϊtuple�ṹ�����
         */
        public List<Values> toTuple(ITuple input, Object value) {
            String member = getKeyFromTuple(input);
            List<Values> values = Lists.newArrayList();
            values.add(new Values(member, value));
            return values;
        }

        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("wordName", "count"));
        }

        public RedisDataTypeDescription getDataTypeDescription() {
            return description;
        }

        public String getKeyFromTuple(ITuple tuple) {
            return tuple.getStringByField("word");
        }

        public String getValueFromTuple(ITuple tuple) {
            return null;
        }
    }