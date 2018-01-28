 
package com.bigdata.storm.jdbc;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.google.common.collect.Lists;

import java.util.*;

public class UserSpout implements IRichSpout {
    boolean isDistributed;
    SpoutOutputCollector collector;
    public static final List<Values> rows = Lists.newArrayList(
            new Values(1,"peter",System.currentTimeMillis()),
            new Values(2,"bob",System.currentTimeMillis()),
            new Values(3,"alice",System.currentTimeMillis()));

    public UserSpout() {
        this(true);
    }

    public UserSpout(boolean isDistributed) {
        this.isDistributed = isDistributed;
    }

    public boolean isDistributed() {
        return this.isDistributed;
    }

    @SuppressWarnings("rawtypes")
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    public void close() {

    }

    public void nextTuple() {
        final Random rand = new Random();
        final Values row = rows.get(rand.nextInt(rows.size() - 1));
        this.collector.emit(row);
        Thread.yield();
    }

    public void ack(Object msgId) {

    }

    public void fail(Object msgId) {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("user_id","user_name","create_date"));
    }

    public void activate() {
    }

    public void deactivate() {
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
