package com.bigdata.rocketmq;

/** 
 * @ClassName: RocketProducer 
 * @Description: TODO 
 * @author jiahp
 * @date 2016-02-02 ����1:38:26 
 *  
 */
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

public class RocketProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
/**
         * һ��Ӧ�ô���һ��Producer����Ӧ����ά���˶��󣬿�������Ϊȫ�ֶ�����ߵ���<br>
         * ע�⣺ProducerGroupName��Ҫ��Ӧ������֤Ψһ<br>
         * ProducerGroup����������ͨ����Ϣʱ�����ò��󣬵��Ƿ��ͷֲ�ʽ������Ϣʱ���ȽϹؼ���
         * ��Ϊ��������ز����Group�µ�����һ��Producer
         */
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");

/**
         * Producer������ʹ��֮ǰ����Ҫ����start��ʼ������ʼ��һ�μ���<br>
         * ע�⣺�мǲ�������ÿ�η�����Ϣʱ��������start����
         */
        producer.setNamesrvAddr("ip:9876");
        producer.start();

/**
         * ������δ������һ��Producer������Է��Ͷ��topic�����tag����Ϣ��
         * ע�⣺send������ͬ�����ã�ֻҪ�����쳣�ͱ�ʶ�ɹ������Ƿ��ͳɹ�Ҳ�ɻ��ж���״̬��<br>
         * ������Ϣд��Master�ɹ�������Slave���ɹ������������Ϣ���ڳɹ������Ƕ��ڸ���Ӧ���������Ϣ�ɿ���Ҫ�󼫸ߣ�<br>
         * ��Ҫ������������������⣬��Ϣ���ܻ���ڷ���ʧ�ܵ������ʧ��������Ӧ��������
         */
        try {
	            {
	                Message msg = new Message("TopicTest1",// topic
	                        "TagA",// tag
	                        "OrderID001",// key
	                        ("Hello MetaQ").getBytes());// body
	                SendResult sendResult = producer.send(msg);
	                System.out.println(sendResult);
	            }
	
	            {
	                Message msg = new Message("TopicTest2",// topic
	                        "TagB",// tag
	                        "OrderID0034",// key
	                        ("Hello MetaQ").getBytes());// body
	                SendResult sendResult = producer.send(msg);
	                System.out.println(sendResult);
	            }
	
	            {
	                Message msg = new Message("TopicTest3",// topic
	                        "TagC",// tag
	                        "OrderID061",// key
	                        ("Hello MetaQ").getBytes());// body
	                
	                SendResult sendResult = producer.send(msg);
	                System.out.println(sendResult);
	            }
        } catch (Exception e) {
            e.printStackTrace();
        }

/**
         * Ӧ���˳�ʱ��Ҫ����shutdown��������Դ���ر��������ӣ���MetaQ��������ע���Լ�
         * ע�⣺���ǽ���Ӧ����JBOSS��Tomcat���������˳����������shutdown����
         */
        producer.shutdown();
    }
}