package com.bigdata.kafka.demo2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

public class CopyOfKafkaConsumer {

    private final ConsumerConnector consumer;

    private CopyOfKafkaConsumer() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "72.16.14.111:2181,172.16.14.112:2181,172.16.14.113:2181");
        props.put("group.id", "1");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        ConsumerConfig config = new ConsumerConfig(props);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }

    void consume() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put("monitor", new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get("monitor").get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()){
             String value = it.next().message().toString();
             //解析json的数据就可以了
            //System.out.println(it.next().message());
        }
    }

    public static void main(String[] args) {
        new CopyOfKafkaConsumer().consume();
    }
}