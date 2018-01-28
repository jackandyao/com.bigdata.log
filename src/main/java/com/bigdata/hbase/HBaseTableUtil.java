package com.bigdata.hbase;


import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;


/**
 * hbase有关表操作的基本工具类
 * @author 贾红平
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
	
	//创建表
	 public static void createTable(String tableName,String[]fs){
		try {
			 //判断表不存在建创建
			 if (!hbaseAdmin.tableExists(tableName)) {
				//表的描述器
				 TableName table=TableName.valueOf(tableName); 
				 HTableDescriptor tableDesp=new HTableDescriptor(table);
				//列族的描述器
				 for (int i = 0; i < fs.length; i++) {
					 tableDesp.addFamily(new HColumnDescriptor(fs[i]));					 
				 }
				//建立hbase表
				hbaseAdmin.createTable(tableDesp);
				System.out.println("创建"+tableName+"表,成功...");
			 }
			 else{
				 System.out.println("表已经存在了...");
				 System.exit(0);
			 }
		} catch (Exception e) {
			System.out.println("创建hbase表,发生了错误");
			e.printStackTrace();
		}
		finally{
			if (hbaseAdmin!=null) {
				try {
					hbaseAdmin.close();
				} catch (Exception e2) {
					System.out.println("关闭出现异常");
				}
			}
		}
	 }
	 
	 
	//删除表
	public static void deleteTable(String tableName){
		try {
			//禁用表
			hbaseAdmin.disableTable(tableName);
			//删除表
			hbaseAdmin.deleteTable(tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		createTable("t_user", new String[]{"base_info","more_info"});
	}
}
