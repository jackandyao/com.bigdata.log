package com.bigdata.util;
/**
 * 存放所有常量的常量池
 * @author 贾红平
 *
 */
public interface ICommConstanUtil {
	// 定义一些常量
	public static final String HBASE_COLUMN_ROW_KEY = "rowkey";
	public static final String HBASE_COLUMN_FAMILY = "family";
	public static final String HBASE_COLUMN_QUALIFIER = "qualifier";
	public static final String HBASE_COLUMN_VALUE = "value";
	public static final String HBASE_COLUMN_FAMILY_QUALIFIER = HBASE_COLUMN_FAMILY+ ":" + HBASE_COLUMN_QUALIFIER;

}
