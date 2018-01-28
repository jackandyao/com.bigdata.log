package com.bigdata.storm.redis.topolgy.save;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;

import backtype.storm.tuple.ITuple;
 public  class WordStoreMapper implements RedisStoreMapper {
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
        private RedisDataTypeDescription description;
        private final String hashKey = "wordCount";

        public WordStoreMapper() {
            description = new RedisDataTypeDescription(
                    RedisDataTypeDescription.RedisDataType.HASH, hashKey);
        }

        public RedisDataTypeDescription getDataTypeDescription() {
            return description;
        }
        
        public String getKeyFromTuple(ITuple tuple) {
            return tuple.getStringByField("word");
        }
 
        public String getValueFromTuple(ITuple tuple) {
            return tuple.getStringByField("count");
        }
    }