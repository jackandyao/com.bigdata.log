package com.bigdata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * 属�?读取配置文件中属性的�?
 * @author jiahp
 *
 */
public class LoadPropertiesUtil {
	private static Properties pro;
	private static FileInputStream fis;
	private static String path;
	static {
		pro = new Properties();
		String home = System.getProperty("user.dir");
		path = home+"/data.properties";
		try {
			fis = new FileInputStream(path);
			pro.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 通过名称--获取对应属�?的�?
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		String value = pro.getProperty(key);
		return value;
	}
	/**
	 * 设置属�?的�? 根据指定属�?的名�?
	 * @param key
	 * @param value
	 */
	public static void setValue(String key, String value) {
		pro.setProperty(key, value);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(path));
			pro.store(fos, key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		 LoadPropertiesUtil.setValue("param", "hello,jiahp");
	}
}
