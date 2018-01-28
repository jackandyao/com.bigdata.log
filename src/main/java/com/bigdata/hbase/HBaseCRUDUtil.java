package com.bigdata.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.bigdata.util.ICommConstanUtil;

/**
 * hbase��ɾ�Ĳ鹤����
 * 
 * @author �ֺ�ƽ
 *
 */
public class HBaseCRUDUtil {
	
	public static HConnection hconnection = null;
	static {
		hconnection = HBaseConnectionUtil.getHConnection();
	}

	/* put���ݸ�ʽת�� */
	private static Put parseRowPut(String rowKey,
			List<Map<String, String>> columnsList) {
		Put put = new Put(Bytes.toBytes(rowKey));
		for (int i = 0; i < columnsList.size(); i++) {
			Map<String, String> column = columnsList.get(i);
			put.add(Bytes.toBytes(column.get(ICommConstanUtil.HBASE_COLUMN_FAMILY)),
					Bytes.toBytes(column.get(ICommConstanUtil.HBASE_COLUMN_QUALIFIER)),
					Bytes.toBytes(column.get(ICommConstanUtil.HBASE_COLUMN_VALUE)));
		}
		// ����ͬ��д��
		put.setDurability(Durability.SYNC_WAL);

		return put;
	}

	/* ��װ��ѯ���� */
	private Get parseColumnGet(String rowKey, String colFamily, String colName) {
		Get get = new Get(Bytes.toBytes(rowKey));
		get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(colName));
		return get;
	}

	/* ¼��ָ���еĶ������� ����rowkey */
	public void addMultiColByKey(String tableName, String rowKey,
			List<Map<String, String>> columns) {
		HTableInterface table = null;
		try {
			table = hconnection.getTable(tableName);
			Put put = parseRowPut(rowKey, columns);
			table.put(put);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				try {
					table.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			relaseHconneciton();
		}
	}

	/* ¼��ָ���е�һ������ */
	public void addOneColumByKey(String tableName, String rowKey,
			String columnFamily, String columnQualifier, String columnValue) {
		List<Map<String, String>> columns = new ArrayList<Map<String, String>>();
		Map<String, String> columnMap = new HashMap<String, String>();
		columnMap.put(ICommConstanUtil.HBASE_COLUMN_FAMILY, columnFamily);
		columnMap.put(ICommConstanUtil.HBASE_COLUMN_QUALIFIER, columnQualifier);
		columnMap.put(ICommConstanUtil.HBASE_COLUMN_VALUE, columnValue);
		columns.add(columnMap);
		addMultiColByKey(tableName, rowKey, columns);
	}

	/* ��hbase������¼������:����ָ���ı� */
	public void addMultiRowsByTable(String tableName,
			Map<String, List<Map<String, String>>> rows) {
		HTableInterface table = null;
		if (rows != null) {
			try {
				table = hconnection.getTable(tableName);
				for (String rowKey : rows.keySet()) {
					List<Map<String, String>> columns = rows.get(rowKey);
					Put put = parseRowPut(rowKey, columns);
					table.put(put);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (table != null) {
					try {
						table.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				relaseHconneciton();
			}
		}
	}

	/* ��ѯָ����rowkey��Ӧ���е�ֵ */
	public List<Map<String, String>> getOneResultByRowKey(String tableName,
			String rowKey, String colFamily, String colName) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		HTableInterface table = null;
		try {
			table = hconnection.getTable(tableName);

			Get get = parseColumnGet(rowKey, colFamily, colName);
			Result result = table.get(get);
			for (Cell cell : result.rawCells()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ICommConstanUtil.HBASE_COLUMN_ROW_KEY, Bytes.toString(result.getRow()));
				map.put(ICommConstanUtil.HBASE_COLUMN_FAMILY_QUALIFIER,
						Bytes.toString(CellUtil.cloneQualifier(cell)));
				map.put(ICommConstanUtil.HBASE_COLUMN_VALUE,
						Bytes.toString(CellUtil.cloneValue(cell)));
				resultList.add(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (table != null)
					table.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			relaseHconneciton();
		}
		return resultList;
	}

	/* ��ȡһ����ָ���������Ӧ��ֵ */
	public List<Map<String, String>> getOneResultByColumnFamily(
			String tableName, String rowKey, String colFamily) {
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		HTableInterface table = null;
		try {
			table = hconnection.getTable(tableName);
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addFamily(Bytes.toBytes(colFamily));
			Result result = table.get(get);
			for (Cell cell : result.rawCells()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ICommConstanUtil.HBASE_COLUMN_ROW_KEY, Bytes.toString(result.getRow()));
				map.put(ICommConstanUtil.HBASE_COLUMN_FAMILY,
						Bytes.toString(CellUtil.cloneFamily(cell)));
				map.put(ICommConstanUtil.HBASE_COLUMN_QUALIFIER,
						Bytes.toString(CellUtil.cloneQualifier(cell)));
				map.put(ICommConstanUtil.HBASE_COLUMN_VALUE,
						Bytes.toString(CellUtil.cloneValue(cell)));
				resultList.add(map);
			}
		} catch (Exception e) {

		} finally {
			try {
				if (table != null)
					table.close();
				relaseHconneciton();
			} catch (Exception ex) {

			}

		}
		return resultList;
	}

	/* ��ҳ���� */
	public List<List<Map<String, String>>> getMutliResultByPageFilter(
			String tableName, Integer size, Integer rowNum) {
		HTableInterface table = null;
		List<List<Map<String, String>>> dataList = null;
		try {
			table = hconnection.getTable(tableName);
			Scan scan = new Scan();
			scan.setCaching(size);

			Filter filter = new PageFilter(rowNum);
			scan.setFilter(filter);

			ResultScanner scanner = table.getScanner(scan);
			Iterator<Result> results = scanner.iterator();
			if (results != null)
				dataList = new ArrayList<List<Map<String, String>>>();
			while (results.hasNext()) {
				Result result = results.next();
				List<Map<String, String>> rowCells = new ArrayList<Map<String, String>>();
				for (Cell cell : result.rawCells()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put(ICommConstanUtil.HBASE_COLUMN_ROW_KEY,
							Bytes.toString(result.getRow()));
					map.put(ICommConstanUtil.HBASE_COLUMN_FAMILY,
							Bytes.toString(CellUtil.cloneFamily(cell)));
					map.put(ICommConstanUtil.HBASE_COLUMN_QUALIFIER,
							Bytes.toString(CellUtil.cloneQualifier(cell)));
					map.put(ICommConstanUtil.HBASE_COLUMN_VALUE,
							Bytes.toString(CellUtil.cloneValue(cell)));
					rowCells.add(map);
				}
				dataList.add(rowCells);
			}
		} catch (IOException e) {

		} finally {
			try {
				if (table != null)
					table.close();
				relaseHconneciton();
			} catch (Exception ex) {

			}

		}
		return dataList;
	}

	/* ����rowkeyɾ��ָ�������� */
	public static void deleteCFByRowKey(String tableName, String rowKey,
			String falilyName, String columnName) throws IOException {
		HTableInterface table = null;
		try {
			table = hconnection.getTable(tableName);
			Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
			deleteColumn.deleteColumns(Bytes.toBytes(falilyName),
					Bytes.toBytes(columnName));
			table.delete(deleteColumn);
			System.out.println(falilyName + ":" + columnName + "is deleted!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* ����rowkeyɾ�����е����� */
	public static void deleteAllCFByRowKey(String tableName, String rowKey)
			throws IOException {
		HTableInterface table = null;
		try {
			table = hconnection.getTable(tableName);
			Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
			table.delete(deleteColumn);
			System.out.println("all cf is deleted!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* �ͷ�hconnection */
	private static void relaseHconneciton() {
		try {
			if (hconnection != null) {
				HBaseConnectionUtil.closeConnection(hconnection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
