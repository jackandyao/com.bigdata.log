package com.bigdata.hbase;


import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;


/**
 * hbase�йر�����Ļ���������
 * @author �ֺ�ƽ
 *
 */
public class HBaseTableUtil {
	
	private HBaseTableUtil(){}
	
	static HBaseAdmin hbaseAdmin=null;
	
	static{
		try {
			hbaseAdmin=new HBaseAdmin(HBaseCfgUtil.getConfiguration());
		}catch (Exception e) {
			 
			e.printStackTrace();
		}
	}
	
	//������
	 public static void createTable(String tableName,String[]fs){
		try {
			 //�жϱ����ڽ�����
			 if (!hbaseAdmin.tableExists(tableName)) {
				//���������
				 TableName table=TableName.valueOf(tableName); 
				 HTableDescriptor tableDesp=new HTableDescriptor(table);
				//�����������
				 for (int i = 0; i < fs.length; i++) {
					 tableDesp.addFamily(new HColumnDescriptor(fs[i]));					 
				 }
				//����hbase��
				hbaseAdmin.createTable(tableDesp);
				System.out.println("����"+tableName+"��,�ɹ�...");
			 }
			 else{
				 System.out.println("���Ѿ�������...");
				 System.exit(0);
			 }
		} catch (Exception e) {
			System.out.println("����hbase��,�����˴���");
			e.printStackTrace();
		}
		finally{
			if (hbaseAdmin!=null) {
				try {
					hbaseAdmin.close();
				} catch (Exception e2) {
					System.out.println("�رճ����쳣");
				}
			}
		}
	 }
	 
	 
	//ɾ����
	public static void deleteTable(String tableName){
		try {
			//���ñ�
			hbaseAdmin.disableTable(tableName);
			//ɾ����
			hbaseAdmin.deleteTable(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		createTable("t_user", new String[]{"base_info","more_info"});
	}
}
