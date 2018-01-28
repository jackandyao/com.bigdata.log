package com.bigdata.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;

/**
 * ��ȡhbase�ı�����
 * @author �ֺ�ƽ
 *
 */
public class HBaseConnectionUtil {
	private static ThreadLocal<HConnection>thread=new ThreadLocal<HConnection>();
	private static HConnection hconnection=null;
	 
	
	private HBaseConnectionUtil(){}

	//��ȡ����
	public static HConnection getHConnection(){
		hconnection=thread.get();
		if (hconnection==null) {
			try {
				Configuration cfg=HBaseCfgUtil.getConfiguration();
				hconnection=HConnectionManager.createConnection(cfg);
				thread.set(hconnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hconnection;
		
	}
	
 
	
	//�ر�����
	public static void closeConnection(HConnection connection){
		try {
			 connection.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
}
