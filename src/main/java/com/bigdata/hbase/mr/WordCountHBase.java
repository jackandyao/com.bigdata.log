package com.bigdata.hbase.mr;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class WordCountHBase {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		String tablename = "wordcount";
		Configuration conf = new Configuration();
		conf.set(TableOutputFormat.OUTPUT_TABLE, tablename);
		createHBaseTable(tablename);
		Job job = new Job(conf,"WordCount table");
		job.setJarByClass(WordCountHBase.class);
		job.setNumReduceTasks(3);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		// ��������Ŀ¼ 
	    FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/input/wordcount/*")); 
		System.exit(job.waitForCompletion(true)?0:1);
	}

	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text text = new Text(); 
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException{
			String s = value.toString();
			StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreTokens()) {
				text.set(st.nextToken());
				context.write(text, one);
			}
		}
	}
	
	public static class Reduce extends TableReducer<Text, IntWritable, NullWritable> {
		
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,InterruptedException{
			int sum = 0;
			for(IntWritable i:values) {
				sum+=i.get();
			}
			Put put = new Put(Bytes.toBytes(key.toString()));
			// row,columnFamily:column,value = word,content:count,sum 
			put.add(Bytes.toBytes("content"),Bytes.toBytes("count"),Bytes.toBytes(String.valueOf(sum)));
			context.write(NullWritable.get(), put);
		}
	}
	/**
	 * create a table
	 * @param tablename
	 * @throws IOException
	 */
	public static void createHBaseTable(String tablename) throws IOException {
		HTableDescriptor htd = new HTableDescriptor(tablename);
		HColumnDescriptor col = new HColumnDescriptor("content");
		htd.addFamily(col);
		Configuration cfg = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(cfg);
		if(admin.tableExists(tablename)) {
			System.out.println("table exists,trying recreate table!");
			admin.disableTable(tablename);
			admin.deleteTable(tablename);
			admin.createTable(htd);
		}
		else {
			System.out.println("create new table:"+tablename);
			admin.createTable(htd);
		}
	}
}