package com.bigdata.kafka.demo1;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class CustomizePartitioner implements Partitioner {  
    public CustomizePartitioner(VerifiableProperties props) {  
   
    }  
    /** 
     * ���ط���������� 
     * @param key sendMessageʱ�������partKey 
     * @param numPartitions topic�еķ������� 
     * @return 
     */  
    @Override  
    public int partition(Object key, int numPartitions) {  
        System.out.println("key:" + key + "  numPartitions:" + numPartitions);  
        String partKey = (String)key;  
        if ("part2".equals(partKey))  
            return 2;  
        return 0;  
    }  
}  