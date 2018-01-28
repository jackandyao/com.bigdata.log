package com.bigdata.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * hbase�����ļ�������
 * @author �ֺ�ƽ
 *
 */
public class HBaseCfgUtil {
	
	public final static String ZK_CLUSTER="cdh245,cdh246,cdh247";
	public final static String ZK_PORT="2181";
	static Configuration hadoopCfg=null;
	 
	private HBaseCfgUtil(){}
	
	public static Configuration getConfiguration(){
		if (hadoopCfg==null) {
			synchronized (HBaseCfgUtil.class) {
				if (hadoopCfg==null) {
					hadoopCfg=HBaseConfiguration.create();
					//����zk��������Ⱥ����
					hadoopCfg.set("hbase.zookeeper.quorum", ZK_CLUSTER);
					//����zk���Ӷ˿ں�
					hadoopCfg.set("hbase.zookeeper.property.clientPort", ZK_PORT);
				}
			}
		}
		return hadoopCfg;
	}
}
