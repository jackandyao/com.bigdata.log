package com.bigdata.util;
/**
 * ������г����ĳ�����
 * @author �ֺ�ƽ
 *
 */
public interface ICommConstanUtil {
	// ����һЩ����
	public static final String HBASE_COLUMN_ROW_KEY = "rowkey";
	public static final String HBASE_COLUMN_FAMILY = "family";
	public static final String HBASE_COLUMN_QUALIFIER = "qualifier";
	public static final String HBASE_COLUMN_VALUE = "value";
	public static final String HBASE_COLUMN_FAMILY_QUALIFIER = HBASE_COLUMN_FAMILY+ ":" + HBASE_COLUMN_QUALIFIER;

}
